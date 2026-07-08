# modi-backend

## Docker 실행 (스프링 + MySQL 한 번에)

```bash
git pull origin develop
docker compose --profile app up -d --build   # 띄우기
docker compose --profile app down            # 끄기
```

> `--profile app` 없이 `docker compose up`만 하면 MySQL만 뜹니다.

- API: http://localhost:18090
- Swagger: http://localhost:18090/swagger-ui/index.html
- 헬스체크: http://localhost:18090/actuator/health

> ⚠️ develop 브랜치는 로컬 포트 충돌 회피를 위해 호스트 포트를 **18090**으로 노출한다(`compose.yaml`의 `18090:8080`).
> 8080이 아니니 주의 — `./gradlew bootRun`으로 직접 실행할 때만 8080이다.
