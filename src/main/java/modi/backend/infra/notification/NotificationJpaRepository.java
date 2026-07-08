package modi.backend.infra.notification;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import modi.backend.domain.notification.Notification;

/**
 * Spring Data JPA. 키셋 페이지네이션은 @Query + Pageable(limit)로 구성한다(프로젝트에 QueryDSL 미도입).
 * 조회는 soft-delete 되지 않은 행만 본다.
 */
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

	Optional<Notification> findByIdAndDeletedAtIsNull(Long id);

	long countByUserIdAndDeletedAtIsNull(Long userId);

	/** 첫 페이지 — 최신순(createdAt desc, id desc). */
	@Query("select n from Notification n where n.userId = :userId and n.deletedAt is null "
			+ "order by n.createdAt desc, n.id desc")
	List<Notification> findFirstPage(@Param("userId") Long userId, Pageable pageable);

	/** 커서 이후 페이지 — (createdAt < :cAt) OR (createdAt = :cAt AND id < :cId), 최신순. */
	@Query("select n from Notification n where n.userId = :userId and n.deletedAt is null "
			+ "and (n.createdAt < :cursorCreatedAt "
			+ "or (n.createdAt = :cursorCreatedAt and n.id < :cursorId)) "
			+ "order by n.createdAt desc, n.id desc")
	List<Notification> findPageAfterCursor(@Param("userId") Long userId,
			@Param("cursorCreatedAt") ZonedDateTime cursorCreatedAt,
			@Param("cursorId") Long cursorId,
			Pageable pageable);
}
