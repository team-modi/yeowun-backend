package modi.backend.domain.remind;

import java.time.LocalDate;

/**
 * 리마인드 저장 시점에 원본 기록에서 복사해 두는 전시 카드 스냅샷.
 * 아카이브 '리마인드' 목록을 원본 기록/전시 조인 없이 자체적으로 렌더링하고,
 * 원본 기록이 삭제돼도 카드가 유지되도록 한다(기록이 전시를 스냅샷하는 것과 동일한 방식).
 */
public record RemindExhibitionSnapshot(
		Long exhibitionId,
		String title,
		String posterUrl,
		String place,
		LocalDate viewedAt) {
}
