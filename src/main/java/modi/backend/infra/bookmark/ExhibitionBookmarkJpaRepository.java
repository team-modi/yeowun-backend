package modi.backend.infra.bookmark;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import modi.backend.domain.bookmark.ExhibitionBookmark;

public interface ExhibitionBookmarkJpaRepository extends JpaRepository<ExhibitionBookmark, Long> {

	/** (user, exhibition) 한 쌍은 유니크 — 상태(활성/해제) 무관하게 단건 조회(멱등 토글용). */
	Optional<ExhibitionBookmark> findByUserIdAndExhibitionId(Long userId, Long exhibitionId);

	long countByUserIdAndDeletedAtIsNull(Long userId);

	@Query("select b.exhibitionId from ExhibitionBookmark b "
			+ "where b.userId = :userId and b.exhibitionId in :exhibitionIds and b.deletedAt is null")
	List<Long> findActiveExhibitionIdsIn(@Param("userId") Long userId,
			@Param("exhibitionIds") Collection<Long> exhibitionIds);

	@Query("select b.exhibitionId from ExhibitionBookmark b "
			+ "where b.userId = :userId and b.deletedAt is null order by b.createdAt desc, b.id desc")
	List<Long> findActiveExhibitionIdsOrderByRegisteredDesc(@Param("userId") Long userId);
}
