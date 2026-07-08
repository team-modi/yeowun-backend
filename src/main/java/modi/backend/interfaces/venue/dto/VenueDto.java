package modi.backend.interfaces.venue.dto;

import java.util.List;

import modi.backend.application.venue.VenueResult;

/**
 * 전시관 API의 응답 DTO 모음. (파일 수 절감을 위해 중첩 record로 묶음)
 */
public final class VenueDto {

	private VenueDto() {
	}

	/** 자동완성 검색 응답 — { "venues": [...] }. 결과 없으면 빈 배열. */
	public record SearchResponse(List<Item> venues) {

		public static SearchResponse from(VenueResult.Search result) {
			return new SearchResponse(result.venues().stream().map(Item::from).toList());
		}
	}

	/** 전시관 항목. address·region은 미입력 시 null. */
	public record Item(Long venueId, String name, String address, String region) {

		public static Item from(VenueResult.Item item) {
			return new Item(item.venueId(), item.name(), item.address(), item.region());
		}
	}
}
