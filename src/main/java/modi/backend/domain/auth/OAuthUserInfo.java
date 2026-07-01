package modi.backend.domain.auth;

import modi.backend.domain.user.AgeGroup;

/**
 * provider userinfo에서 추출한 공통 사용자 정보(VO). provider별 응답 차이를 흡수한다.
 * 연령대·출생연도는 미동의(카카오)/미지원(구글)이면 각각 {@link AgeGroup#UNSPECIFIED}·null.
 *
 * @param sub       provider 내 고유 식별자
 * @param email     이메일(카카오 비동의 시 null 가능)
 * @param name      이름/실명(카카오 name·구글 name 동의항목, 미동의/미지원 시 null)
 * @param nickname  닉네임(카카오 profile_nickname·구글 name, 미동의/미지원 시 null)
 * @param ageGroup  연령대(카카오 age_range 매핑, 미동의/미지원 시 UNSPECIFIED)
 * @param birthYear 출생연도(카카오 birthyear, 미동의/미지원 시 null)
 */
public record OAuthUserInfo(String sub, String email, String name, String nickname, AgeGroup ageGroup,
		Integer birthYear) {
}
