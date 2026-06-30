# Postman 검수 가이드

## 실행 서버

- Docker API 서버: `http://localhost:18080`
- Swagger UI: `http://localhost:18080/swagger-ui.html`
- 개발용 사용자 헤더: `X-User-Id: 1`

`localhost:8080`은 다른 로컬 컨테이너가 사용 중이라, Modi 백엔드는 `18080`으로 열어 두었다.

## Import 순서

1. Postman에서 `Import`를 누른다.
2. `postman/modi-record-archive.postman_collection.json`을 가져온다.
3. `postman/modi-record-archive.postman_environment.json`을 가져온다.
4. 우측 상단 Environment를 `Modi Local Docker - Record Archive`로 선택한다.
5. 컬렉션을 위에서 아래 순서대로 실행하거나, `Run collection`으로 전체 실행한다.

## 검수 흐름

컬렉션은 다음 흐름을 자동 검증한다.

1. 서버 헬스 체크
2. Swagger/OpenAPI 문서 노출 확인
3. 기록 생성 후 `recordId` 환경 변수 저장
4. 상세 조회, 목록 조회, 수정
5. 북마크/북마크 해제
6. 영상 100MB 초과 요청 거절 확인
7. `X-User-Id` 없는 요청 401 확인
8. 삭제 후 상세 조회 404 확인

사진은 10MB, 영상은 100MB 제한을 기준으로 검증한다.
