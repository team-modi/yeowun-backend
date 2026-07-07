package modi.backend.infra.remind;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import modi.backend.domain.record.Record;
import modi.backend.domain.remind.Remind;

public interface RemindJpaRepository extends JpaRepository<Remind, Long> {

	Optional<Remind> findByIdAndDeletedAtIsNull(Long id);

	Page<Remind> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

	/**
	 * 소환 대상 후보 — 작성 후 {@code createdBefore} 이전이면서 아직 한 번도 회고하지 않은 내 기록을 최신순으로.
	 * (record↔remind 결합을 remind 인프라 쪽에만 둔다 — 기록 도메인은 리마인드를 모른다.)
	 * 호출 측에서 {@code PageRequest.of(0, 1)}로 1건만 가져간다.
	 */
	@Query("""
			select r
			from Record r
			where r.userId = :userId
			  and r.deletedAt is null
			  and r.createdAt <= :createdBefore
			  and r.id not in (
			      select rm.recordId from Remind rm
			      where rm.userId = :userId and rm.deletedAt is null
			  )
			order by r.createdAt desc
			""")
	List<Record> findRemindCandidates(
			@Param("userId") Long userId,
			@Param("createdBefore") ZonedDateTime createdBefore,
			Pageable pageable);
}
