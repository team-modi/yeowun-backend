package modi.backend.domain.exhibition;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 개인 전시(CUSTOM) 등록 시 부여하는 장르 키워드 마스터(임시). 지금은 마스터에서 무작위로 1개를 뽑아 붙인다 —
 * 추후 AI 분류가 이 무작위 선택을 대체한다. 무작위 선택은 앱(Facade) 레이어에서 하고, 엔티티는 고른 값을 저장만 한다.
 */
public final class GenreKeyword {

	/** 한국어 장르 키워드 마스터. */
	private static final List<String> MASTER = List.of(
			"회화·드로잉", "사진", "미디어아트", "조각·설치", "디자인",
			"공예", "건축", "공연", "현대미술", "일러스트레이션");

	private GenreKeyword() {
	}

	/** 마스터에서 무작위로 1개 선택. */
	public static String random() {
		return MASTER.get(ThreadLocalRandom.current().nextInt(MASTER.size()));
	}

	/** 마스터 전체(참고·검증용). */
	public static List<String> all() {
		return MASTER;
	}
}
