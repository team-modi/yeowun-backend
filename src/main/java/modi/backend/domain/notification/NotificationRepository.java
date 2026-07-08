package modi.backend.domain.notification;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Notification 영속화 포트(도메인 소유). 구현은 infra(DIP). 조회는 살아있는 행만 본다.
 */
public interface NotificationRepository {

	Optional<Notification> findById(Long id);

	Notification save(Notification notification);

	/**
	 * 키셋 페이지네이션 한 페이지 조회(최신순: createdAt desc, id desc).
	 * 커서가 없으면(cursorCreatedAt=null) 첫 페이지, 있으면
	 * (createdAt &lt; cursorCreatedAt) OR (createdAt = cursorCreatedAt AND id &lt; cursorId) 행을 본다.
	 * hasNext 판단을 위해 limitPlusOne(=size+1)개까지 가져온다.
	 */
	List<Notification> findPage(Long userId, ZonedDateTime cursorCreatedAt, Long cursorId, int limitPlusOne);

	long countByUserId(Long userId);
}
