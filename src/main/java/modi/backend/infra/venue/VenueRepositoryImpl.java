package modi.backend.infra.venue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.venue.Venue;
import modi.backend.domain.venue.VenueRepository;

/**
 * {@link VenueRepository} 어댑터(DIP). Spring Data로 위임하며, 조회는 살아있는 행만 본다.
 */
@Repository
@RequiredArgsConstructor
public class VenueRepositoryImpl implements VenueRepository {

	private final VenueJpaRepository jpaRepository;

	@Override
	public Optional<Venue> findById(Long id) {
		return jpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	/** 이름 부분 일치(대소문자 무시) 자동완성. keyword 공백/미입력이면 질의 없이 빈 목록. */
	@Override
	public List<Venue> searchByName(String keyword, int limit) {
		if (keyword == null || keyword.isBlank()) {
			return List.of();
		}
		return jpaRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(
				keyword.trim(), PageRequest.of(0, limit));
	}

	@Override
	public Venue save(Venue venue) {
		return jpaRepository.save(venue);
	}
}
