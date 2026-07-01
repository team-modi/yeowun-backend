package modi.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import modi.backend.config.PublicDataProperties;
import modi.backend.infra.auth.GoogleApi;
import modi.backend.infra.auth.KakaoApi;

/**
 * WebClient 기반 HTTP Interface 클라이언트 등록. (선언형 REST 엔드포인트 관리)
 */
@Configuration
@EnableConfigurationProperties(PublicDataProperties.class)
public class HttpClientConfig {

	@Bean
	public KakaoApi kakaoApi() {
		return createClient(KakaoApi.class);
	}

	@Bean
	public GoogleApi googleApi() {
		return createClient(GoogleApi.class);
	}

	/**
	 * 공공데이터 전시 API 전용 WebClient. baseUrl은 설정(app.public-data.culture.base-url)에서 주입한다.
	 * 공공데이터 응답은 단건일 때 배열이 아닌 객체로 오는 특성이 있어 SINGLE_VALUE_AS_ARRAY를 켠다.
	 */
	@Bean
	public WebClient cultureWebClient(PublicDataProperties properties) {
		JsonMapper mapper = JsonMapper.builder()
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.build();
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config -> {
					config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
					config.defaultCodecs().maxInMemorySize(8 * 1024 * 1024);
				})
				.build();
		String baseUrl = properties.baseUrl() == null ? "" : properties.baseUrl();
		return WebClient.builder()
				.baseUrl(baseUrl)
				.exchangeStrategies(strategies)
				.build();
	}

	private <T> T createClient(Class<T> apiType) {
		WebClient webClient = WebClient.builder().build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builderFor(WebClientAdapter.create(webClient))
				.build();
		return factory.createClient(apiType);
	}
}
