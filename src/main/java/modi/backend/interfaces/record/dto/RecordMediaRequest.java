package modi.backend.interfaces.record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import modi.backend.domain.record.RecordMediaType;

public record RecordMediaRequest(
		@NotNull RecordMediaType type,
		@NotBlank String url,
		@PositiveOrZero int sortOrder,
		@PositiveOrZero long sizeBytes) {
}
