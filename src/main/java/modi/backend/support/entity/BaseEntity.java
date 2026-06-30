package modi.backend.support.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private ZonedDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private ZonedDateTime updatedAt;

	@Column(name = "deleted_at")
	private ZonedDateTime deletedAt;

	@PrePersist
	private void prePersist() {
		ZonedDateTime now = ZonedDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	private void preUpdate() {
		this.updatedAt = ZonedDateTime.now();
	}

	public void delete() {
		if (deletedAt == null) {
			deletedAt = ZonedDateTime.now();
		}
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}
}
