package modi.backend.domain.record;

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

@Entity
@Table(name = "record_emotions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordEmotion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "record_id", nullable = false)
	private Record record;

	@Column(name = "emotion_code", nullable = false, length = 50)
	private String emotionCode;

	private RecordEmotion(String emotionCode) {
		this.emotionCode = emotionCode;
	}

	public static RecordEmotion create(String emotionCode) {
		return new RecordEmotion(emotionCode);
	}

	void attach(Record record) {
		this.record = record;
	}
}
