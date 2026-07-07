package modi.backend.application.remind;

import java.util.List;

/**
 * 리마인드 유스케이스 입력. (Controller가 Request → Criteria로 변환)
 */
public final class RemindCriteria {

	private RemindCriteria() {
	}

	/** 리마인드 저장 — 대상 기록 + 지금 다시 남기는 감정(선택) + 소감(필수). */
	public record Save(Long userId, Long recordId, List<String> emotionCodes, String reflection) {
	}
}
