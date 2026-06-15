# Java Microservices - Spring Cloud Gateway Edition

Projekt mikroservis Java s Spring Cloud Gateway jako API Gateway namísto NGINX.

## Architektura

```
┌─────────────────────────┐
│      Client/Browser     │
└────────────┬────────────┘
             │
             ▼
┌──────────────────────────────┐
│   Spring Cloud Gateway       │ (port 80)
│   └─ /appuser/ → port 9200   │
│   └─ /applog/  → port 9100   │
│   └─ /auth/    → port 9300   │
└──────────────────────────────┘
             │
    ┌────────┼────────┐
    ▼        ▼        ▼
┌────────┐ ┌────────┐ ┌────────┐
│ AppLog │ │AppUser │ │  Auth  │
│Service │ │Service │ │Service │
│:9100   │ │:9200   │ │:9300   │
└┬───────┘ └┬───────┘ └┬───────┘
 │         │         │
 └─────────┴─────────┘
           │
           ▼
    ┌──────────────┐
    │   MariaDB    │
    │  :3306       │
    └──────────────┘
```

## Služby

### GatewayService (NEW)
- **Framework**: Spring Cloud Gateway
- **Port**: 8080 (interně), 80 (na hosta)
- **Debug Port**: 8081
- **Role**: API Gateway a reverse proxy

### AppLogService
- **Port**: 9100
- **Debug Port**: 9101
- **Route**: `/applog/**`
- **Database**: MariaDB table `applog`

### AppUserService
- **Port**: 9200
- **Debug Port**: 9201
- **Route**: `/appuser/**`
- **Database**: MariaDB table `appuser`

### AuthService
- **Port**: 9300
- **Debug Port**: 9301
- **Route**: `/auth/**`
- **Database**: MariaDB table `auth`

## Spuštění

### Příprava

1. Vytvoř `.env` soubor v root adresáři s potřebnými proměnnými:
```bash
MARIADB_ROOT_PASSWORD=your_password
MARIADB_PORT=3306
APPLOGSERVICE_PORT=9100
APPUSERSERVICE_PORT=9200
AUTHSERVICE_PORT=9300
APPLOGSERVICE_DB_NAME=applog
APPUSERSERVICE_DB_NAME=appuser
AUTHSERVICE_DB_NAME=auth
```

2. Inicializuj databází:
```bash
docker compose up mariadb
# nebo ručně spusť init-databases.sql
```

### Spuštění všech služeb

```bash
docker compose up --build
```

## Přístup k API

- **AppLog API**: http://localhost/applog/...
- **AppUser API**: http://localhost/appuser/...
- **Auth API**: http://localhost/auth/...

## Debugging

Každá služba je dostupná na svém debug portu:

- AppLogService debug: `localhost:9101`
- AppUserService debug: `localhost:9201`
- AuthService debug: `localhost:9301`
- GatewayService debug: `localhost:8081`

### Nastavení v IntelliJ IDEA

1. Run → Edit Configurations
2. Add new → Remote JVM Debug
3. Nastav host `localhost` a příslušný port

## Změny vs NGINX verze

| Funkce | NGINX | Spring Cloud Gateway |
|--------|-------|----------------------|
| Framework | Lightweight webserver | Java/Spring Boot |
| Konfigurace | Statická (nginx.conf) | Kód/YAML |
| Dynamické routy | Obtížné | Snadné |
| Load balancing | Vestavěné | Spring Cloud |
| Health checks | Základní | Pokročilé |
| Métriky | Třetí strany | Spring Actuator |

## Struktura projektu

```
JavaMicroservices/
├── GatewayService/          (NEW - Spring Cloud Gateway)
│   ├── src/
│   │   ├── main/java/
│   │   │   └── GatewayServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── AppLogService/
├── AppUserService/
├── AuthService/
├── docker-compose.yml       (AKTUALIZOVÁN - bez NGINX, s Gateway)
└── _docs/
```

## Migrace z NGINX

Pokud jste používali NGINX verzi:

1. **NGINX konfigurace** → **GatewayServiceApplication.java** (RouteLocator bean)
2. **Docker port 80** → **Gateway na portu 8080**, který mapuje na port 80 na hostu
3. **Routy** zůstávají stejné:
   - `/appuser/**` → AppUserService
   - `/applog/**` → AppLogService
   - `/auth/**` → AuthService

## Výhody Spring Cloud Gateway

✅ Integrován s Java ekosystémem
✅ Jednoduší konfigurace pro Spring Boot projekty
✅ Pokročilé filtrování a transformace requestů
✅ Lepší debugging v Java IDE
✅ Stejný jazyk a framework jako ostatní služby
✅ Snadnější přidání vlastní logiky

## Budoucí vylepšení

- [ ] Service Discovery (Eureka)
- [ ] Circuit Breaker (Resilience4j)
- [ ] Autentizace/Autorizace (OAuth2/JWT)
- [ ] Rate Limiting
- [ ] Request/Response logging
- [ ] Centralized configuration (Spring Cloud Config)

## Kontakty / Další informace

Viz `_docs/` složka pro více technických detailů.

