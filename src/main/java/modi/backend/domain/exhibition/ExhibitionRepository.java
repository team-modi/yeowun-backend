package modi.backend.domain.exhibition;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Exhibition 영속화 포트(도메인 소유). 구현은 infra(DIP). soft delete된 행은 조회에서 제외한다.
 */
public interface ExhibitionRepository {

	Exhibition save(Exhibition exhibition);

	Optional<Exhibition> findById(Long id);

	/** 조건·페이지네이션으로 전시 목록 조회. CUSTOM 노출은 {@code query.requesterId}로 필터링한다. */
	Page<Exhibition> search(ExhibitionQuery query, Pageable pageable);

	/** CATALOG 동기화 upsert용 — 원천 식별자로 기존 행 조회. */
	Optional<Exhibition> findByExternalId(String externalId);
}
