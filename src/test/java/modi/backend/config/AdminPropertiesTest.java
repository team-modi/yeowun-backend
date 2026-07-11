package modi.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** 관리자 화이트리스트 — 목록 판정 + null/빈 목록 안전 기본값 순수 단위 검증. */
class AdminPropertiesTest {

	@Test
	@DisplayName("목록에 있는 유저ID만 관리자")
	void 화이트리스트_판정() {
		AdminProperties props = new AdminProperties(List.of(1L, 2L));
		assertThat(props.isAdmin(1L)).isTrue();
		assertThat(props.isAdmin(2L)).isTrue();
		assertThat(props.isAdmin(3L)).isFalse();
		assertThat(props.isAdmin(null)).isFalse();
	}

	@Test
	@DisplayName("null·빈 목록이면 아무도 관리자 아님(안전 기본값)")
	void 빈_목록() {
		assertThat(new AdminProperties(null).isAdmin(1L)).isFalse();
		assertThat(new AdminProperties(List.of()).isAdmin(1L)).isFalse();
	}
}
