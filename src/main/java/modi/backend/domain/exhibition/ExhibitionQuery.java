package modi.backend.domain.exhibition;

import java.time.LocalDate;

/**
 * 전시 목록/탐색 조회 조건(도메인 포트 입력). (03_전시.md 3.3.1)
 *
 * @param keyword     전시명·전시장명 부분 일치(null이면 미적용)
 * @param ongoingOn   해당 날짜에 진행 중(startDate ≤ ongoingOn ≤ endDate)인 전시만(null이면 미적용)
 * @param region      지역 필터(null이면 미적용)
 * @param category    카테고리 필터(null이면 미적용)
 * @param requesterId 요청자 id — CUSTOM 노출 판단용. null이면 CATALOG만 노출한다.
 */
public record ExhibitionQuery(
		String keyword,
		LocalDate ongoingOn,
		ExhibitionRegion region,
		ExhibitionCategory category,
		Long requesterId) {
}
