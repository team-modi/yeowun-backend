package modi.backend.domain.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "record_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordKeyword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "record_id", nullable = false)
	private Record record;

	@Column(nullable = false, length = 100)
	private String keyword;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private KeywordSource source;

	private RecordKeyword(String keyword, KeywordSource source) {
		this.keyword = keyword;
		this.source = source;
	}

	public static RecordKeyword create(String keyword, KeywordSource source) {
		return new RecordKeyword(keyword, source);
	}

	void attach(Record record) {
		this.record = record;
	}
}
