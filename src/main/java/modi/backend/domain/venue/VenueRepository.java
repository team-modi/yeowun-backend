package modi.backend.domain.venue;

import java.util.List;
import java.util.Optional;

/**
 * 전시관 영속화 포트(도메인 소유, DIP). 구현은 infra. 조회는 살아있는 행만 본다.
 */
public interface VenueRepository {

	Optional<Venue> findById(Long id);

	/** 전시관명 부분 일치(대소문자·공백 무시) 자동완성. 최대 {@code limit}개. keyword 공백이면 빈 목록. */
	List<Venue> searchByName(String keyword, int limit);

	Venue save(Venue venue);
}
