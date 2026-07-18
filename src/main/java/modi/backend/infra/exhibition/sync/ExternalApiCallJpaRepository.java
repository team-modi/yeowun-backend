package modi.backend.infra.exhibition.sync;

import org.springframework.data.jpa.repository.JpaRepository;

import modi.backend.domain.exhibition.sync.ExternalApiCall;

public interface ExternalApiCallJpaRepository extends JpaRepository<ExternalApiCall, Long> {
}
