package modi.backend.infra.exhibition.sync;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import modi.backend.domain.exhibition.sync.CultureDetailResponse;

public interface CultureDetailResponseJpaRepository extends JpaRepository<CultureDetailResponse, Long> {

	Optional<CultureDetailResponse> findByExternalId(String externalId);
}
