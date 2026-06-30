package modi.backend.interfaces.record.dto;

import modi.backend.domain.record.RecordMediaType;

public record RecordMediaResponse(
		Long mediaId,
		RecordMediaType type,
		String url,
		int sortOrder,
		long sizeBytes) {
}
