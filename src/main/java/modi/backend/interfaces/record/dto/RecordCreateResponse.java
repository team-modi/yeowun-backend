package modi.backend.interfaces.record.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import modi.backend.domain.record.AiStatus;
import modi.backend.domain.record.WriteMode;

public record RecordCreateResponse(
		Long recordId,
		Long exhibitionId,
		WriteMode writeMode,
		LocalDate viewedAt,
		AiStatus aiStatus,
		ZonedDateTime createdAt) {
}
