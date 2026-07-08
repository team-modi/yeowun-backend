package modi.backend.domain.bookmark;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modi.backend.support.entity.BaseEntity;

/**
 * 전시 북마크(관심 전시). 기존 "기록 북마크"와 별개인 신규 도메인 — 사용자가 전시(exhibition)를 관심 등록한다.
 * (user_id, exhibition_id) 한 쌍당 한 행. 해제는 soft-delete({@link BaseEntity#delete()}),
 * 재등록은 같은 행을 {@link BaseEntity#restore()}로 되살려 유니크 제약과 멱등성을 함께 만족한다.
 * 다른 애그리거트(User·Exhibition)는 ID 값으로만 참조한다(경계 넘는 @ManyToOne ❌).
 */
@Entity
@Table(name = "exhibition_bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionBookmark extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "exhibition_id", nullable = false)
	private Long exhibitionId;

	private ExhibitionBookmark(Long userId, Long exhibitionId) {
		this.userId = userId;
		this.exhibitionId = exhibitionId;
	}

	public static ExhibitionBookmark create(Long userId, Long exhibitionId) {
		return new ExhibitionBookmark(userId, exhibitionId);
	}

	public boolean isActive() {
		return getDeletedAt() == null;
	}
}
