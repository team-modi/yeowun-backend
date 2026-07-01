package modi.backend.interfaces.record.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RecordUpdateRequest(
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
