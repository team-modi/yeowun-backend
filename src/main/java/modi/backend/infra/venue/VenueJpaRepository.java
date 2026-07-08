package modi.backend.infra.venue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import modi.backend.domain.venue.Venue;

/**
 * Spring Data JPA. 전시관명 자동완성은 파생 쿼리(대소문자 무시 contains + soft-delete 필터)로 처리하고,
 * 상한(limit)은 {@link Pageable}로 준다.
 */
public interface VenueJpaRepository extends JpaRepository<Venue, Long> {

	/** soft delete된 행은 제외하고 조회. */
	Optional<Venue> findByIdAndDeletedAtIsNull(Long id);

	/** 이름 부분 일치(대소문자 무시) + 살아있는 행. 개수 상한은 {@code pageable}로 제한한다. */
	List<Venue> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);
}
