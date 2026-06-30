package modi.backend.interfaces.record.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import modi.backend.domain.record.WriteMode;

public record RecordCreateRequest(
		@NotNull @Positive Long exhibitionId,
		@NotNull WriteMode writeMode,
		LocalDate viewedAt,
		@NotBlank @Size(max = 5000) String content,
		@NotEmpty List<@NotBlank String> emotionCodes,
		List<@NotBlank String> userKeywords,
		List<@NotBlank String> aiKeywords,
		String aiSummary,
		String representativeEmotion,
		String cardPhrase,
		List<@Valid RecordMediaRequest> media) {
}
