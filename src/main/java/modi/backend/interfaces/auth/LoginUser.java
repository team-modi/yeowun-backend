package modi.backend.interfaces.auth;

public record LoginUser(Long userId, String provider, String nickname, Boolean profileCompleted) {
}
