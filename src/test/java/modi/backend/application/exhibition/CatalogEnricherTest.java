package modi.backend.application.exhibition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modi.backend.config.CatalogEnrichProperties;
import modi.backend.domain.exhibition.GenreClassification;
import modi.backend.domain.exhibition.GenreClassifier;
import modi.backend.domain.exhibition.GenreKeyword;
import modi.backend.domain.exhibition.GenreResult;

/**
 * CatalogEnricher 단위 검증 — 장르 백필만 담당한다(상세는 syncCatalog가 적재 시점에 함께 채우므로 여기서 다루지 않는다).
 * <p>
 * 배치 구조는 [조회 tx] → tx 밖 AI 호출 → [반영 tx]다. AI 호출이 트랜잭션 밖이라는 것이 이 클래스의 존재 이유이므로
 * (커넥션 장기 점유 방지), 여기서는 배치 반복·조기 종료와 "대상이 없으면 AI를 태우지 않는다"를 검증한다.
 */
class CatalogEnricherTest {

	private final CatalogEnrichProperties props = new CatalogEnrichProperties(40, 20);

	private static List<GenreTarget> targets(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> new GenreTarget((long) i,
						new GenreClassification("전시 " + i, null, null, null, null, null)))
				.toList();
	}

	private static List<GenreResult> randomResults(int count) {
		return IntStream.range(0, count).mapToObj(i -> GenreResult.random(GenreKeyword.random())).toList();
	}

	@Test
	@DisplayName("enrichGenres — 미분류가 소진될 때까지 배치를 반복하고, batchSize 미만 배치에서 종료한다")
	void enrichGenres_배치반복_소진시종료() {
		ExhibitionFacade facade = mock(ExhibitionFacade.class);
		GenreClassifier classifier = mock(GenreClassifier.class);
		// 40, 40, 15 → 세 번째가 batchSize(40) 미만이라 소진으로 보고 종료
		when(facade.findGenreTargets(40)).thenReturn(targets(40), targets(40), targets(15));
		when(classifier.classifyAll(anyList())).thenAnswer(i -> randomResults(i.<List<?>>getArgument(0).size()));
		when(facade.applyGenres(anyList(), anyList(), any())).thenAnswer(i -> i.<List<?>>getArgument(0).size());
		CatalogEnricher enricher = new CatalogEnricher(facade, props, classifier);

		int total = enricher.enrichGenres();

		assertThat(total).isEqualTo(95);
		verify(facade, times(3)).findGenreTargets(40);
		verify(classifier, times(3)).classifyAll(anyList()); // 배치당 1콜 — 전시마다 호출하지 않는다
	}

	@Test
	@DisplayName("enrichGenres — 첫 배치가 0이면 AI를 더 태우지 않고 즉시 종료한다")
	void enrichGenres_대상없으면_즉시종료() {
		ExhibitionFacade facade = mock(ExhibitionFacade.class);
		GenreClassifier classifier = mock(GenreClassifier.class);
		when(facade.findGenreTargets(40)).thenReturn(List.of());
		CatalogEnricher enricher = new CatalogEnricher(facade, props, classifier);

		assertThat(enricher.enrichGenres()).isZero();
		verify(facade, times(1)).findGenreTargets(40);
		verify(classifier, never()).classifyAll(anyList()); // 빈 배치로 AI를 태우지 않는다
		verify(facade, never()).applyGenres(anyList(), anyList(), any());
	}

	@Test
	@DisplayName("enrichGenres — 분류 결과를 대상과 같은 순서로 반영 단계에 넘긴다")
	void enrichGenres_분류결과를_반영단계로_전달() {
		ExhibitionFacade facade = mock(ExhibitionFacade.class);
		GenreClassifier classifier = mock(GenreClassifier.class);
		List<GenreTarget> batch = targets(2);
		List<GenreResult> results = List.of(GenreResult.ai("사진", modi.backend.domain.exhibition.GenreProvider.GEMINI,
				"gemini-2.5-flash"), GenreResult.random("미디어아트"));
		when(facade.findGenreTargets(anyInt())).thenReturn(batch);
		when(classifier.classifyAll(anyList())).thenReturn(results);
		when(facade.applyGenres(anyList(), anyList(), any())).thenReturn(2);
		CatalogEnricher enricher = new CatalogEnricher(facade, props, classifier);

		enricher.enrichGenres();

		verify(classifier).classifyAll(batch.stream().map(GenreTarget::classification).toList());
		verify(facade).applyGenres(org.mockito.ArgumentMatchers.eq(batch),
				org.mockito.ArgumentMatchers.eq(results), any());
	}
}
