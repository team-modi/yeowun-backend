package modi.backend.domain.remind;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리마인드 시점에 "지금 다시 남긴" 감정 코드. {@link Remind} 애그리거트 내부 엔티티.
 * (기록의 {@code RecordEmotion}과 동일한 패턴)
 */
@Entity
@Table(name = "remind_emotions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemindEmotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "remind_id", nullable = false)
	private Remind remind;

	@Column(name = "emotion_code", nullable = false, length = 50)
	private String emotionCode;

	private RemindEmotion(String emotionCode) {
		this.emotionCode = emotionCode;
	}

	public static RemindEmotion create(String emotionCode) {
		return new RemindEmotion(emotionCode);
	}

	void attach(Remind remind) {
		this.remind = remind;
	}
}
