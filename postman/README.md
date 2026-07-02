# Postman 검수 가이드

Modi 백엔드 전체 API(Auth·User·Exhibition·Record)를 게스트 로그인 기반으로 검수하는 컬렉션이다.

## 실행 서버

- Docker API 서버: `http://localhost:18080`
- Swagger UI: `http://localhost:18080/swagger-ui.html`

`localhost:8080`은 다른 로컬 컨테이너가 사용 중이라, Modi 백엔드는 `18080`으로 열어 두었다.
(포트가 다르면 환경변수 `baseUrl`만 바꾸면 된다.)

## 인증 방식

기록 등 로그인 전용 API는 **access 토큰(Bearer/쿠키)** 으로 인증한다. `X-User-Id` 헤더 방식은 폐기됐다.

- `Auth ▸ Guest Login`(`POST /api/v1/auth/guest`)이 소셜 인증 없이 임시 사용자를 만들고 자체 JWT를 발급한다.
- 응답의 `accessToken`이 환경변수 `accessToken`에 자동 저장되고, 컬렉션 레벨 **Bearer 인증**으로 이후 모든 로그인 전용 요청에 자동 첨부된다.
- `refresh_token`은 HttpOnly 쿠키로 내려가 Postman 쿠키 저장소에 보관되며 `Auth ▸ Refresh Token`에서 재발급에 쓰인다.

## Import 순서

1. Postman에서 `Import`를 누른다.
2. `postman/modi-backend.postman_collection.json`을 가져온다.
3. `postman/modi-backend.postman_environment.json`을 가져온다.
4. 우측 상단 Environment를 `Modi Local Docker`로 선택한다.
5. `Run collection`으로 전체 실행하거나, 폴더를 위에서 아래 순서대로 실행한다.

## 검수 흐름

`Run collection` 시 아래 순서로 실행되며, 각 단계가 `accessToken`·`exhibitionId`·`recordId`를 환경변수로 넘겨 전체가 한 번에 통과한다.

1. **Health / OpenAPI Docs** — 서버 헬스 + 문서에 게스트 로그인·기록 경로 노출 확인
2. **Auth ▸ Guest Login** — 게스트 토큰 발급(provider=guest) 후 `accessToken` 저장
3. **User ▸ Get My Profile / Update My Profile** — 게스트 사용자로 프로필 조회·온보딩
4. **Exhibition ▸ List / Register Custom / Detail** — 개인 전시 등록 후 `exhibitionId` 저장
5. **Record** — 로그인 전용 기록 CRUD·검색·북마크
   - 기록 생성 후 `recordId` 저장 → 상세/검색/다녀온 전시/수정/북마크
   - `Reject Video Over 100MB` — 영상 100MB 초과 400 `INVALID_MEDIA`
   - `Reject Without Token` — 토큰 없는 기록 요청 401 `NO_ACCESS_TOKEN`(인증 강제 확인)
   - 삭제 후 상세 조회 404 `NOT_FOUND`

사진은 10MB, 영상은 100MB 제한을 기준으로 검증한다.

## 참고

- `Auth ▸ Social Login (Kakao) - 참고용`은 실제 카카오 인가 코드가 필요하므로 기본 실행 흐름에서 제외돼 있다. 실제 소셜 로그인을 검수하려면 body의 `code`·`redirectUri`를 실제 값으로 교체한다.
- `Auth ▸ Logout`은 실행하면 이후 인증 요청이 실패하므로 전체 실행에 포함하지 않았다. 필요 시 개별 요청으로 확인한다.
