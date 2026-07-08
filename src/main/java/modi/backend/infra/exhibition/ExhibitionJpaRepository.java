package modi.backend.infra.exhibition;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import modi.backend.domain.exhibition.Exhibition;
import modi.backend.domain.exhibition.ExhibitionType;

/**
 * Spring Data JPA. 동적 필터(keyword·date·region·category·CUSTOM 노출)는
 * {@link JpaSpecificationExecutor}로 조합한다(프로젝트에 QueryDSL 미도입 → Specification 사용).
 */
public interface ExhibitionJpaRepository
		extends JpaRepository<Exhibition, Long>, JpaSpecificationExecutor<Exhibition> {

	/** soft delete된 행은 제외하고 원천 식별자로 조회(동기화 upsert용). */
	Optional<Exhibition> findByExternalIdAndDeletedAtIsNull(String externalId);

	/** 진행 중(startDate≤today≤endDate)인 지정 타입 전시를 조회수 내림차순으로(홈 배너 후보). limit은 {@code pageable}로. */
	@Query("""
			select e from Exhibition e
			where e.type = :type and e.deletedAt is null
			  and e.startDate <= :today and e.endDate >= :today
			order by e.ourViewCount desc, e.id desc
			""")
	java.util.List<Exhibition> findOngoingByViewsDesc(@Param("type") ExhibitionType type,
			@Param("today") LocalDate today, Pageable pageable);
}
