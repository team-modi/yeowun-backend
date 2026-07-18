package modi.backend.infra.exhibition.sync;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import modi.backend.domain.exhibition.sync.CultureDetailResponse;
import modi.backend.domain.exhibition.sync.CultureDetailResponseRepository;

@Repository
@RequiredArgsConstructor
public class CultureDetailResponseRepositoryImpl implements CultureDetailResponseRepository {

	private final CultureDetailResponseJpaRepository jpaRepository;

	@Override
	public CultureDetailResponse save(CultureDetailResponse cultureDetailResponse) {
		return jpaRepository.save(cultureDetailResponse);
	}

	@Override
	public Optional<CultureDetailResponse> findByExternalId(String externalId) {
		return jpaRepository.findByExternalId(externalId);
	}
}
