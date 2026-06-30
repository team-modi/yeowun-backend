# AI 작업 메모리

이 문서는 Modi/Yeo-un 백엔드 작업 시 AI가 항상 먼저 참고해야 하는 프로젝트 메모리다. 기능 요구사항 초안은 프롬프트로 생성된 산출물이므로, 현재 백엔드 프로젝트 컨벤션과 다를 수 있다. 구현할 때는 이 메모리, 현재 코드 구조, 실제 담당자 합의 순서로 판단한다.

## 최근 논의 요약

- 2026-06-28 논의 기준으로 MVP(P0)를 빠르게 구현한다.
- 이번 주 안에 프론트엔드가 사용할 수 있는 MVP API를 제공하는 것이 목표다.
- ERD는 변하지 않을 가능성이 높은 핵심 도메인만 우선 설계한다.
  - User
  - SocialAccount
  - Exhibition
  - Record
  - RecordKeyword
  - RecordMedia
- 자주 바뀔 수 있는 추천, 취향 분석 확장, 복잡한 AI 메타데이터, 리마인드 고도화 필드는 P0 DB 설계에 성급히 넣지 않는다.
- 백엔드는 우선 빠르게 구현하고, 이후 개인 목표 달성을 위한 개선/리팩토링을 계획한다.

## 요구사항 초안 출처

아래 문서들은 AI에게 기능 요구사항을 도메인별로 분류해달라고 요청해 생성된 초안이다. 프롬프트에는 여운 기능 정의서, 와이어프레임, 여운 유저 플로우가 사용되었다.

- `/Users/seobyeongpil/Downloads/00_개요_및_공통규칙.md`
- `/Users/seobyeongpil/Downloads/01_인증인가.md`
- `/Users/seobyeongpil/Downloads/02_유저.md`
- `/Users/seobyeongpil/Downloads/03_전시.md`
- `/Users/seobyeongpil/Downloads/04_AI_정리.md`
- `/Users/seobyeongpil/Downloads/05_기록_아카이브.md`
- `/Users/seobyeongpil/Downloads/06_리마인드.md`
- `/Users/seobyeongpil/Downloads/99_부록.md`

주의: 위 문서 전체를 그대로 구현 범위로 보지 않는다. 전체 일정 기준으로 만든 초안이며, 실제로는 MVP에 필요한 도메인 기능만 선별해 구현한다.

## 담당 범위

- 김진수: 인증/인가, 유저, 전시 쪽을 주로 담당할 가능성이 높다.
- 서병필: 기록/아카이브 쪽을 주로 담당할 가능성이 높다.
- 김진수가 전역으로 필요한 작업들을 일부 만들어두었을 수 있다. 필요하면 임의 수정 가능하지만, 수정 전 현재 코드 의도와 영향 범위를 확인한다.

## AI 작업 원칙

- 기능 구현 전 현재 프로젝트 코드, 패키지 구조, 테스트, 설정을 먼저 읽는다.
- 초안 문서와 현재 프로젝트 컨벤션이 충돌하면 현재 코드 컨벤션을 우선한다.
- 도메인 경계를 임의로 섞지 않는다.
- P0에 없는 기능을 AI가 선제적으로 크게 만들지 않는다.
- Controller는 HTTP 요청/응답과 DTO 변환을 담당한다.
- Service는 유스케이스, 트랜잭션, 도메인 규칙을 담당한다.
- Repository는 영속성 접근만 담당한다.
- Entity를 API 응답으로 직접 반환하지 않는다.
- 모든 구현은 프론트엔드가 이번 주 안에 MVP API를 붙일 수 있는 방향으로 작게 끊는다.
- 기능 하나를 끝낼 때마다 최소한의 테스트 또는 실행 검증을 남긴다.
- JPA ORM을 사용하더라도 공유/배포 DB 변경은 Entity 자동 변경에 맡기지 않고 migration 파일로 관리한다.
- MVP API 공유 이후에는 `ddl-auto: create-drop/update`보다 `validate` + Flyway/Liquibase 방식을 우선 검토한다.

## 공통 API 규칙 후보

초안 문서 기준 공통 응답 포맷은 다음 형태다. 프로젝트에 이미 공통 응답 컨벤션이 생겼다면 그것을 우선한다.

```json
{
  "meta": { "result": "SUCCESS" },
  "data": {}
}
```

실패 응답 후보:

```json
{
  "meta": {
    "result": "FAIL",
    "errorCode": "INVALID_INPUT",
    "message": "..."
  },
  "data": null
}
```

공통 에러 후보:

- `UNAUTHORIZED`: 인증 토큰 누락/무효
- `TOKEN_EXPIRED`: Access Token 만료
- `FORBIDDEN`: 타인 리소스 접근
- `INVALID_INPUT`: 입력 검증 실패
- `NOT_FOUND`: 리소스 없음
- `INVALID_MEDIA`: 미디어 타입/용량/개수 오류
- `AI_UNAVAILABLE`: AI 처리 실패
- `EXTERNAL_API_UNAVAILABLE`: 외부 API 실패
- `INTERNAL_ERROR`: 서버 내부 오류

## P0 도메인 기준

### User / SocialAccount

- 소셜 계정 기반 사용자다.
- P0 ERD 필드는 `nickname`, `ageGroup`, `regionSido`, `regionSigungu`, `profileCompleted` 정도만 우선한다.
- 소셜 계정은 `provider`, `providerUserId` 조합을 유니크하게 관리한다.
- 인증/유저 세부 구현은 김진수 담당 가능성이 높으므로, 기록/아카이브 작업 중에는 필요한 최소 인터페이스만 사용한다.

### Exhibition

- 기록은 전시 하나에 연결된다.
- P0 ERD 필드는 `externalId`, `title`, `startDate`, `endDate`, `venueName`, `region`, `posterUrl`, `description`, `source` 중심이다.
- 전시 도메인은 김진수 담당 가능성이 높다.
- 개인 전시를 독립 엔티티로 둘지, 기록 안의 내장 객체로 둘지는 초안 문서에서도 미확정이다. 구현 전 팀 합의 또는 현재 코드 방향을 확인한다.
- 기록 도메인의 `userId` 기반 느슨한 연결은 병렬 개발을 위한 임시 방식이다. 유저/인증 도메인이 완성되면 인증된 사용자 정보(`CurrentUser`) 기반으로 전환한다.
- 기록 API는 클라이언트가 보낸 `userId`를 신뢰하지 않는다. 사용자 식별은 항상 인증 토큰 또는 `CurrentUserProvider`에서 얻는다.

### Record / Archive

기록/아카이브는 서병필 담당 가능성이 높은 핵심 작업 영역이다.

P0에서 우선 고려할 기능:

- 기록 생성
- 내 기록 목록 조회
- 기록 상세 조회
- 기록 수정
- 기록 삭제
- 감정/키워드 저장
- 미디어 연결
- 전시 연결
- 아카이브 검색은 전시명/감정 키워드 중심으로 우선 검토

초안 기준 주요 규칙:

- 기록은 한 번의 관람 감상 단위다.
- `writeMode`: `AI` 또는 `DIRECT`
- `viewedAt`: 실제 관람일이며 작성일과 다를 수 있다. 미래 날짜는 허용하지 않는다.
- 본문은 필수다. 초안은 `content` 1~5000자를 제안한다.
- 감정 코드는 1개 이상 필요하다.
- 미디어는 기록당 최대 3개 후보이며, 사진은 Must, 동영상은 Should로 분류되어 있다.
- MVP 미디어 용량 기준은 사진 10MB, 영상 100MB로 둔다.
- 기록 목록은 본인 기록만 조회한다.
- 타인 기록 조회/수정/삭제는 금지한다.
- AI 모드에서는 분석 결과를 사용자가 수정한 뒤 기록 저장 시 함께 전달하는 흐름을 고려한다.
- DIRECT 모드에서는 저장 후 AI 요약/키워드 비동기 생성 가능성이 있지만, P0 일정상 별도 합의 없이 과하게 만들지 않는다.

초안 기준 기록 API 후보:

- `POST /api/v1/records/media`: 미디어 선업로드
- `POST /api/v1/records`: 기록 작성
- `GET /api/v1/records`: 내 기록 목록/아카이브
- `GET /api/v1/records/{recordId}`: 기록 상세
- `PUT /api/v1/records/{recordId}`: 기록 수정
- `DELETE /api/v1/records/{recordId}`: 기록 삭제
- `POST /api/v1/records/{recordId}/bookmark`: 북마크 추가
- `DELETE /api/v1/records/{recordId}/bookmark`: 북마크 해제

주의: 북마크, 미디어 선업로드, AI 비동기, 리마인드 트리거는 MVP 일정에 따라 축소될 수 있다. 프론트 제공에 필요한 최소 API를 먼저 구현한다.

### AI

- AI 정리 도메인은 감상 요약, 키워드, 대표 감정, 카드 문구를 생성하는 초안이 있다.
- P0에서는 실제 LLM 연동보다 API 계약, 저장 필드, 실패 시 처리 방식이 더 중요할 수 있다.
- `POST /api/v1/ai/analyze`는 DB 저장 없는 미리보기 성격으로 제안되어 있다.
- AI 결과 수정 후 저장은 별도 API가 아니라 기록 생성/수정 시 필드로 전달하는 방향이 초안이다.

### Reminder

- 리마인드는 Should/P1 성격이 강하다.
- 기록 저장 시 리마인드 생성을 위한 확장 지점을 고려할 수는 있지만, P0 기록/아카이브 API 제공을 지연시키면서까지 구현하지 않는다.
- 관람일 기준 7일 이후 노출, 하루 1회 팝업, confirm/dismiss 구분 등은 초안이며 확정 구현 범위가 아니다.

## 구현 우선순위

1. 현재 코드 컨벤션과 패키지 구조 확인
2. 공통 응답/예외/검증 구조 확인 또는 최소 구현
3. P0 Entity/Repository 구성
4. 프론트가 바로 붙을 수 있는 API DTO 정의
5. 기록/아카이브 CRUD
6. 전시/유저/인증 쪽과 연결되는 부분은 담당자 작업과 충돌하지 않게 최소 의존으로 연결
7. 테스트 및 실행 검증
8. 이후 리팩토링/품질 개선

## 작업 시 금지/주의

- 초안 문서의 모든 기능을 한 번에 구현하지 않는다.
- 도슨트, 전시 추천, 커뮤니티, 취향 리포트, My Space, 푸시 알림은 P0 기록/아카이브 작업에서 만들지 않는다.
- 요구사항이 불확실한 필드를 DB에 먼저 박지 않는다.
- 기존 작성자가 만든 전역 코드가 마음에 들지 않는다는 이유만으로 대규모 리팩토링하지 않는다.
- 프론트 MVP 제공 일정에 필요 없는 추상화는 미룬다.
