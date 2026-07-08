package modi.backend.domain.remind;

/**
 * 리마인드 감정 변화 AI 요약의 생성 상태.
 * <ul>
 *   <li>{@code READY} — 요약 생성 완료(aiSummary 존재).</li>
 *   <li>{@code SKIPPED} — AI 비활성(키 없음) 또는 rate-limit으로 생성 안 함. 저장 자체는 성공.</li>
 *   <li>{@code FAILED} — 생성 시도 중 오류. 저장은 성공, 요약 null.</li>
 * </ul>
 */
public enum RemindAiStatus {
	READY, SKIPPED, FAILED
}
