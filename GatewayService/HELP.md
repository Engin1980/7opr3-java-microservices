# Gateway Service

Spring Cloud Gateway service sloužící jako API Gateway pro microservices.

## Dostupné routy

- `/appuser/**` -> AppUserService (port 9200)
- `/applog/**` -> AppLogService (port 9100)
- `/auth/**` -> AuthService (port 9300)

## Spuštění

```bash
mvn spring-boot:run
```

Server běží na portu 8080.

## Docker

```bash
docker build -t gateway-service .
docker run -p 8080:8080 gateway-service
```

