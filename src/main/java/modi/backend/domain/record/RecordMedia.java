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
@Table(name = "record_media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordMedia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "record_id", nullable = false)
	private Record record;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RecordMediaType type;

	@Column(nullable = false, length = 2048)
	private String url;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "size_bytes", nullable = false)
	private long sizeBytes;

	private RecordMedia(RecordMediaType type, String url, int sortOrder, long sizeBytes) {
		this.type = type;
		this.url = url;
		this.sortOrder = sortOrder;
		this.sizeBytes = sizeBytes;
	}

	public static RecordMedia create(RecordMediaType type, String url, int sortOrder, long sizeBytes) {
		return new RecordMedia(type, url, sortOrder, sizeBytes);
	}

	void attach(Record record) {
		this.record = record;
	}
}
