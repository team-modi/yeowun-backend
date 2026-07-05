package modi.backend.infra.ai.claude;

import java.time.Duration;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;

import modi.backend.config.AiProperties;
import modi.backend.domain.ai.AiChatClient;
import modi.backend.domain.ai.AiErrorCode;
import modi.backend.support.error.CoreException;

/**
 * {@link AiChatClient}의 Claude(Anthropic) 어댑터. 공식 anthropic-java SDK로 Messages API 호출.
 * api-key 미설정이면 클라이언트를 만들지 않고, 호출 시 {@link AiErrorCode#AI_DISABLED}를 던진다
 * (AI만 비활성, 나머지 기능은 정상). 모델·max-tokens·타임아웃은 {@link AiProperties} 설정값을 사용한다.
 * 응답마다 토큰 사용량을 로깅한다(비용/스파이크 모니터링).
 */
@Component
public class ClaudeAiChatClient implements AiChatClient {

	private static final Logger log = LoggerFactory.getLogger(ClaudeAiChatClient.class);

	private final AiProperties properties;
	private final AnthropicClient client; // api-key 미설정 시 null

	public ClaudeAiChatClient(AiProperties properties) {
		this.properties = properties;
		this.client = properties.isConfigured()
				? AnthropicOkHttpClient.builder()
						.apiKey(properties.apiKey())
						.timeout(Duration.ofSeconds(properties.timeoutSeconds()))
						.build()
				: null;
	}

	@Override
	public String complete(String systemPrompt, String userPrompt) {
		if (client == null) {
			throw new CoreException(AiErrorCode.AI_DISABLED);
		}
		try {
			MessageCreateParams params = MessageCreateParams.builder()
					.model(properties.model())
					.maxTokens(properties.maxTokens())
					.system(systemPrompt)
					.addUserMessage(userPrompt)
					.build();
			Message response = client.messages().create(params);
			logUsage(response);
			return response.content().stream()
					.flatMap(block -> block.text().stream())
					.map(text -> text.text())
					.collect(Collectors.joining("\n"))
					.trim();
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw new CoreException(AiErrorCode.AI_GENERATION_FAILED, e.getMessage());
		}
	}

	/** 토큰 사용량 로깅(모니터링). usage 접근 실패는 무시(본 응답에 영향 없음). */
	private void logUsage(Message response) {
		try {
			var usage = response.usage();
			log.info("AI 호출 사용량 model={} input={} output={}",
					properties.model(), usage.inputTokens(), usage.outputTokens());
		} catch (Exception ignored) {
			// 사용량 로깅은 부가 기능 — 실패해도 응답 처리엔 영향 없음
		}
	}
}
