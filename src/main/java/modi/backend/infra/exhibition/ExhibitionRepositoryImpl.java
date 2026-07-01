package modi.backend.infra.exhibition;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.exhibition.Exhibition;
import modi.backend.domain.exhibition.ExhibitionQuery;
import modi.backend.domain.exhibition.ExhibitionRepository;

/**
 * {@link ExhibitionRepository} 어댑터(DIP). Spring Data + Specification으로 위임하며, 조회는 살아있는 행만 본다.
 */
@Repository
@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionRepository {

	private final ExhibitionJpaRepository jpaRepository;

	@Override
	public Exhibition save(Exhibition exhibition) {
		return jpaRepository.save(exhibition);
	}

	@Override
	public Optional<Exhibition> findById(Long id) {
		return jpaRepository.findById(id).filter(exhibition -> exhibition.getDeletedAt() == null);
	}

	@Override
	public Page<Exhibition> search(ExhibitionQuery query, Pageable pageable) {
		return jpaRepository.findAll(ExhibitionSpecifications.from(query), pageable);
	}

	@Override
	public Optional<Exhibition> findByExternalId(String externalId) {
		return jpaRepository.findByExternalIdAndDeletedAtIsNull(externalId);
	}
}
