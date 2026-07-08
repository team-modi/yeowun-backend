package modi.backend.application.venue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.venue.VenueRepository;

/**
 * 전시관 유스케이스 조율. load·조율만(상태 변경 없음). 검색은 상위 20개 고정(커서 미적용).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueFacade {

	/** 자동완성 반환 상한(전시 5.5: 상위 20개 고정). */
	private static final int SEARCH_LIMIT = 20;

	private final VenueRepository venueRepository;

	/** 전시관명 자동완성. keyword 공백/미입력이면 빈 목록(에러 아님). */
	public VenueResult.Search search(VenueCriteria.Search criteria) {
		return VenueResult.Search.of(venueRepository.searchByName(criteria.keyword(), SEARCH_LIMIT));
	}
}
