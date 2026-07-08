package modi.backend.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modi.backend.support.entity.BaseEntity;

/**
 * 알림(09_알림.md) — 사용자에게 전달된 한 건의 알림. 홈 우측 상단 종 아이콘 목록에 노출된다.
 * 다른 애그리거트(리마인드)는 {@code targetId}(=remindId)로만 참조한다(경계 넘는 @ManyToOne 금지).
 * NOTICE(공지)는 이동 대상이 없어 targetId=null. id·생성/수정/삭제 시각은 {@link BaseEntity}.
 */
@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private NotificationType type;

	@Column(nullable = false)
	private String title;

	@Column(length = 1000)
	private String body;

	/** 이동 대상 식별자. REMIND=remindId, NOTICE=null. */
	@Column(name = "target_id")
	private Long targetId;

	/** 읽음 여부. 예약어 회피를 위해 컬럼명은 is_read. */
	@Column(name = "is_read", nullable = false)
	private boolean read;

	private Notification(Long userId, NotificationType type, String title, String body, Long targetId) {
		this.userId = userId;
		this.type = type;
		this.title = title;
		this.body = body;
		this.targetId = targetId;
		this.read = false;
	}

	/** 새 알림 생성 — 읽지 않음 상태로 시작한다. */
	public static Notification create(Long userId, NotificationType type, String title, String body, Long targetId) {
		return new Notification(userId, type, title, body, targetId);
	}

	/** 읽음 처리(멱등) — 이미 읽음이어도 결과는 동일하게 읽음. */
	public void markRead() {
		this.read = true;
	}

	/** 이 알림의 소유자인지 확인. 타인 알림 접근 차단(404 판정)에 사용. */
	public boolean isOwnedBy(Long userId) {
		return this.userId != null && this.userId.equals(userId);
	}
}
