package modi.backend.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 관리자 화이트리스트. role 개념이 없으므로 관리자 유저ID 목록으로 `/api-admin/**` 접근을 통제한다.
 * 미설정(빈 목록)이면 아무도 관리자 아님 → 모든 admin 요청 403(안전 기본값).
 * 예: application.yaml `app.admin.user-ids: 1,2` 또는 env `ADMIN_USER_IDS=1,2`.
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(List<Long> userIds) {

	public AdminProperties {
		userIds = userIds == null ? List.of() : List.copyOf(userIds);
	}

	public boolean isAdmin(Long userId) {
		return userId != null && userIds.contains(userId);
	}
}
