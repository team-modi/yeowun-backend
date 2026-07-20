package modi.backend.application.remind;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import modi.backend.infra.remind.RemindJpaRepository;

/**
 * 리마인드 감정 변화 AI 요약을 저장 커밋 이후 백그라운드에서 생성·반영한다(응답 지연·서블릿 워커 점유 제거 — M-2).
 * 저장 시점엔 상태를 PENDING으로 두고, {@code aiExecutor} 스레드에서 요약(느린 LLM 호출)을 만든 뒤 짧은 tx로 Remind를 갱신한다.
 * best-effort — 실패/미설정/rate-limit이면 요약 없이 상태만 FAILED/SKIPPED로 남고, 본 저장/응답에는 영향이 없다.
 * ({@code RemindFacade.save}는 비트랜잭션이라 {@code remindRepository.save}가 이미 커밋된 뒤 호출된다 → afterCommit 훅 불필요.)
 */
@Component
public class RemindSummaryBackfill {

	private static final Logger log = LoggerFactory.getLogger(RemindSummaryBackfill.class);

	private final RemindAiSummarizer summarizer;
	private final RemindJpaRepository remindRepository;
	private final Executor aiExecutor;
	private final TransactionTemplate transactionTemplate;

	public RemindSummaryBackfill(RemindAiSummarizer summarizer, RemindJpaRepository remindRepository,
			Executor aiExecutor, PlatformTransactionManager transactionManager) {
		this.summarizer = summarizer;
		this.remindRepository = remindRepository;
		this.aiExecutor = aiExecutor;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}

	/** 커밋된 Remind(remindId)의 감정 변화 요약을 백그라운드에서 생성해 반영한다. */
	public void schedule(Long remindId, RemindAiSummarizer.Context context) {
		aiExecutor.execute(() -> generateAndApply(remindId, context));
	}

	private void generateAndApply(Long remindId, RemindAiSummarizer.Context context) {
		try {
			RemindAiSummarizer.Result result = summarizer.summarize(context); // 느린 LLM 호출 — DB tx 밖
			transactionTemplate.executeWithoutResult(status ->
					remindRepository.findByIdAndDeletedAtIsNull(remindId)
							.ifPresent(remind -> remind.applyAiSummary(result.status(), result.summary())));
		} catch (RuntimeException e) {
			log.warn("리마인드 감정 변화 요약 백필 실패 remindId={}: {}", remindId, e.getMessage());
		}
	}
}
