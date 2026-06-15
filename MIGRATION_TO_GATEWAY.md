# Migrace z NGINX na Spring Cloud Gateway - Souhrn změn

## Co bylo změněno

### ✅ Nový GatewayService

Vytvořen nový Spring Boot projekt `GatewayService` se Spring Cloud Gateway:

**Lokace**: `./GatewayService/`

**Struktura**:
```
GatewayService/
├── .mvn/wrapper/
│   └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/cz/osu/prf/kip/gatewayservice/
│   │   │   └── GatewayServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/cz/osu/prf/kip/gatewayservice/
│           └── GatewayServiceApplicationTests.java
├── .gitignore
├── Dockerfile
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

### ✅ Konfiguracia routů

**GatewayServiceApplication.java** - Bean `RouteLocator`:
```java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("appuser", r -> r
                    .path("/appuser/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri("http://appuserservice:9200"))
            .route("applog", r -> r
                    .path("/applog/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri("http://applogservice:9100"))
            .route("auth", r -> r
                    .path("/auth/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri("http://authservice:9300"))
            .build();
}
```

### ✅ Docker Compose aktualizace

**docker-compose.yml** - Nahrazení NGINX za Gateway:

**Staré (NGINX)**:
```yaml
nginx:
  image: nginx:latest
  container_name: nginx-reverse-proxy
  ports:
    - "80:80"
  volumes:
    - ./nginx/default.conf:/etc/nginx/conf.d/default.conf
  depends_on:
    - authservice
    - applogservice
    - appuserservice
```

**Nové (Gateway)**:
```yaml
gateway:
  build:
    context: ./GatewayService
    dockerfile: Dockerfile
  container_name: gateway-service
  environment:
    SPRING_APPLICATION_NAME: GatewayService
    SERVER_PORT: 8080
    DEBUG_PORT: 8081
  ports:
    - "80:8080"
    - 8081:8081
  expose:
    - 8080
  depends_on:
    - authservice
    - applogservice
    - appuserservice
  networks:
    - microservices-network
  volumes:
    - maven-cache:/root/.m2
```

### ✅ Dokumentace

- **README.md** - Nový soubor s kompletní dokumentací projektu
- **_docs/Services Acces.md** - Aktualizováno s poznámkou o Gateway

### ⚠️ Starý NGINX (zachován pro historii)

Folder `./nginx/default.conf` zůstává v projektu pro referenci, ale není více používán.

## Jak spustit nový systém

### 1. Build a spuštění

```bash
docker compose up --build
```

### 2. Přístup k API

- AppUser: `http://localhost/appuser/...`
- AppLog: `http://localhost/applog/...`
- Auth: `http://localhost/auth/...`

### 3. Debugging

Port 8081 pro GatewayService debug v IntelliJ IDEA.

## Porovnání NGINX vs Spring Cloud Gateway

| Aspekt | NGINX | Spring Cloud Gateway |
|--------|-------|----------------------|
| **Konfigurace** | nginx.conf (statická) | application.properties + Java Bean |
| **Dynamické routy** | Obtížné - restart potřebný | Snadné - bez restartu |
| **Filtrování** | Omezené | Rozšiřitelné |
| **Monitorování** | Třetí strany | Spring Actuator |
| **Debugging** | Logy | Java debugger |
| **Jazyk** | C | Java |
| **Integrace** | Generická | Spring Boot optimalizovaná |

## Migrace je hotová

✅ Projekt je plně funkční s Spring Cloud Gateway

✅ Všechny routy fungují stejně jako s NGINX

✅ Debugging je jednodušší (Java IDE)

✅ Budoucí rozšíření je jednodušší (Spring Cloud ecosystem)

## Možná budoucí vylepšení

- Circuit Breaker (Resilience4j)
- Service Discovery (Eureka)  
- Auth/autorizace (OAuth2/JWT)
- Rate limiting
- Request logging
- Centrální konfigurace

