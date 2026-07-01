package modi.backend.domain.record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modi.backend.support.entity.BaseEntity;

@Entity
@Table(name = "records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "exhibition_id", nullable = false)
	private Long exhibitionId;

	@Enumerated(EnumType.STRING)
	@Column(name = "write_mode", nullable = false, length = 20)
	private WriteMode writeMode;

	@Column(name = "viewed_at", nullable = false)
	private LocalDate viewedAt;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@Column(name = "ai_summary", columnDefinition = "text")
	private String aiSummary;

	@Column(name = "representative_emotion", length = 50)
	private String representativeEmotion;

	@Column(name = "card_phrase", length = 255)
	private String cardPhrase;

	@Enumerated(EnumType.STRING)
	@Column(name = "ai_status", nullable = false, length = 20)
	private AiStatus aiStatus;

	@Column(nullable = false)
	private boolean bookmarked;

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<RecordKeyword> keywords = new ArrayList<>();

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<RecordEmotion> emotions = new ArrayList<>();

	@OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<RecordMedia> media = new ArrayList<>();

	private Record(Long userId, Long exhibitionId, WriteMode writeMode, LocalDate viewedAt, String content,
			String aiSummary, String representativeEmotion, String cardPhrase, AiStatus aiStatus) {
		this.userId = userId;
		this.exhibitionId = exhibitionId;
		this.writeMode = writeMode;
		this.viewedAt = viewedAt;
		this.content = content;
		this.aiSummary = aiSummary;
		this.representativeEmotion = representativeEmotion;
		this.cardPhrase = cardPhrase;
		this.aiStatus = aiStatus;
	}

	public static Record create(Long userId, Long exhibitionId, WriteMode writeMode, LocalDate viewedAt, String content,
			String aiSummary, String representativeEmotion, String cardPhrase, AiStatus aiStatus) {
		return new Record(userId, exhibitionId, writeMode, viewedAt, content, aiSummary, representativeEmotion,
				cardPhrase, aiStatus);
	}

	public void replaceContent(LocalDate viewedAt, String content, String aiSummary, String representativeEmotion,
			String cardPhrase, AiStatus aiStatus) {
		this.viewedAt = viewedAt;
		this.content = content;
		this.aiSummary = aiSummary;
		this.representativeEmotion = representativeEmotion;
		this.cardPhrase = cardPhrase;
		this.aiStatus = aiStatus;
	}

	public void replaceKeywords(List<RecordKeyword> newKeywords) {
		keywords.clear();
		newKeywords.forEach(this::addKeyword);
	}

	public void replaceEmotions(List<RecordEmotion> newEmotions) {
		emotions.clear();
		newEmotions.forEach(this::addEmotion);
	}

	public void replaceMedia(List<RecordMedia> newMedia) {
		media.clear();
		newMedia.forEach(this::addMedia);
	}

	public void bookmark() {
		this.bookmarked = true;
	}

	public void unbookmark() {
		this.bookmarked = false;
	}

	private void addKeyword(RecordKeyword keyword) {
		keyword.attach(this);
		keywords.add(keyword);
	}

	private void addEmotion(RecordEmotion emotion) {
		emotion.attach(this);
		emotions.add(emotion);
	}

	private void addMedia(RecordMedia mediaItem) {
		mediaItem.attach(this);
		media.add(mediaItem);
	}
}
