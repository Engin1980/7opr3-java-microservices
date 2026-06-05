# jak se v microservice prostředí dozví servica, na jaké URL/portu je jiná mikroservica

V mikroservisní architektuře se to obvykle neřeší natvrdo zapsanou IP adresou a portem. Mikroservisy se navzájem nacházejí pomocí **service discovery** nebo přes **API gateway**.

Nejběžnější způsoby:

### 1. Service Discovery Registry

Mikroservisy se při startu zaregistrují do centrálního registru.

Příklady:

* Consul
* Eureka
* ZooKeeper

Příklad:

```
OrderService
  ↓ registrace
Service Registry
  - UserService -> 10.0.1.5:8080
  - PaymentService -> 10.0.1.8:8080
```

Když chce `OrderService` zavolat `UserService`:

1. Zeptá se registru: „Kde běží UserService?“
2. Dostane seznam instancí.
3. Vybere jednu z nich (load balancing).
4. Odešle HTTP/gRPC požadavek.

---

### 2. DNS Service Discovery (Kubernetes)

V dnešní době velmi časté.

V Kubernetes vytvoříš Service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
```

Pak jiná služba jednoduše volá:

```http
http://user-service/api/users
```

DNS v clusteru přeloží:

```
user-service
↓
10.96.15.42
↓
Pod1
Pod2
Pod3
```

Mikroservisa vůbec neví, na jakém portu nebo IP běží konkrétní instance.

---

### 3. Konfigurace

U menších systémů bývá URL jiné služby v konfiguraci:

```json
{
  "Services": {
    "UserService": "http://localhost:5001"
  }
}
```

V kódu:

```csharp
var url = configuration["Services:UserService"];
```

To funguje, ale špatně se škáluje.

---

### 4. API Gateway

Někdy mikroservisy nekomunikují přímo.

Klient:

```
Client
  ↓
API Gateway
  ↓
UserService
PaymentService
OrderService
```

Gateway ví, kde jednotlivé služby běží.

Příklady:

* YARP
* Kong
* Traefik

---

### 5. Service Mesh

Ve velkých systémech řeší komunikaci speciální vrstva.

Příklady:

* Istio
* Linkerd

Aplikace pak pouze volá:

```http
http://user-service
```

a mesh zajistí:

* nalezení instance,
* load balancing,
* retry,
* TLS,
* monitoring.

---

### Jak se to typicky dělá v .NET dnes?

Pokud provozuješ mikroservisy v Kubernetes:

```csharp
var client = httpClientFactory.CreateClient();

var response =
    await client.GetAsync("http://user-service/api/users/123");
```

`user-service` je DNS jméno služby v Kubernetes a platforma sama zjistí správnou IP a port.

Mimo Kubernetes se často používá:

* Consul
* nebo novější .NET řešení jako .NET Aspire, které service discovery řeší automaticky během vývoje i nasazení.


### mám 3 micra na docker kontejnerech; jak složité je pro to rozchodti to kubernetees, když to vůbec neznám? je to v javě

Pokud už máš mikroservisy zabalené do Docker kontejnerů, přechod na Kubernetes není technicky složitý, ale je tam poměrně strmá křivka učení.

Pro 3 Java mikroservisy je potřeba pochopit hlavně:

* Pod
* Deployment
* Service
* ConfigMap
* Secret
* Ingress (pokud chceš přístup zvenku)

To je zhruba 80 % toho, co budeš běžně používat.

### Jak velký je rozdíl proti Docker Compose?

Například v Docker Compose:

```yaml
services:
  users:
    image: my/users
    ports:
      - "8081:8080"

  orders:
    image: my/orders
    ports:
      - "8082:8080"
```

V Kubernetes musíš pro každou službu vytvořit minimálně:

**Deployment**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: users
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: users
        image: my/users
```

a k tomu **Service**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: users
spec:
  selector:
    app: users
  ports:
  - port: 80
    targetPort: 8080
```

Pak ostatní služby volají:

```text
http://users
```

nebo

```text
http://users/api/customers
```

bez znalosti IP adres.

---

# Kolik času zabere naučit se to?

Pokud umíš:

* Docker
* sítě
* základy Linuxu

tak:

* 1–2 dny → rozchodíš jednoduchý cluster lokálně
* 1 týden → začneš se orientovat v Deploymentech, Services a Ingressu
* 2–4 týdny → budeš schopen samostatně provozovat menší systém

Nejtěžší nebývá Kubernetes samotný, ale:

* networking
* persistentní data
* monitoring
* CI/CD

---

### Doporučil bych nejdřív Docker Compose

Pro pouhé 3 mikroservisy se zamysli, jestli Kubernetes skutečně potřebuješ.

Pokud máš:

* UserService
* OrderService
* NotificationService

a běží na jednom serveru,

tak často úplně stačí:

```text
Docker Compose
+ Nginx
+ PostgreSQL
```

a máš výrazně jednodušší provoz.

---

### Kdy Kubernetes začne dávat smysl

Když chceš:

* automatické škálování
* více serverů
* rolling deploymenty bez výpadku
* self-healing (spadlý kontejner se sám obnoví)
* service discovery
* cloudové nasazení

Pak je Kubernetes standard.

---

### Pro Java projekty

Pokud používáš:

* Spring Boot
* Quarkus
* Micronaut

tak je Kubernetes velmi dobře podporovaný a mnoho konfigurace lze generovat automaticky.

Pro první seznámení bych dnes doporučil:

1. Nainstalovat lokální Kubernetes přes Minikube nebo k3d.
2. Nasadit jednu Java službu.
3. Přidat druhou službu a nechat je komunikovat přes Kubernetes Service.
4. Teprve potom řešit Ingress, databázi a monitoring.

Se třemi existujícími Docker kontejnery se dá první funkční Kubernetes verze rozchodit klidně během jednoho odpoledne, ale pohodlně se v tom orientovat obvykle trvá spíš několik dní až týdnů.

# jak mi v tomhle pomůže nginx?

NGINX ti v mikroservisní architektuře pomůže hlavně jako **reverse proxy a vstupní bod do systému**. Není to náhrada Kubernetes, spíš jednodušší alternativa nebo doplněk.

---

## 1. Co NGINX řeší v mikroservisách

### Bez NGINX

Každá služba je dostupná přímo:

```text
http://localhost:8081/users
http://localhost:8082/orders
http://localhost:8083/notifications
```

Problémy:

* klient musí znát všechny porty
* žádné jednotné API
* horší bezpečnost (všechno venku)

---

### S NGINX (reverse proxy)

```text
Client
  ↓
http://api.myapp.com
  ↓
NGINX
  ↓
/users  → UserService
/orders → OrderService
/notify → NotificationService
```

NGINX přesměruje provoz:

```nginx id="x1k2ab"
server {
  listen 80;

  location /users {
    proxy_pass http://users-service:8080;
  }

  location /orders {
    proxy_pass http://orders-service:8080;
  }
}
```

---

## 2. Co ti to reálně přinese

### ✔ Jedna vstupní URL

Místo 3–10 endpointů máš:

```
http://api.myapp.com
```

---

### ✔ Routing podle cesty

* `/users/**` → UserService
* `/orders/**` → OrderService

---

### ✔ Skrytí interní architektury

Klient vůbec neví:

* kolik máš služeb
* na jakých portech běží
* kde běží fyzicky

---

### ✔ Load balancing (základní)

NGINX umí posílat requesty na více instancí:

```nginx id="a9b3cd"
upstream users {
  server 10.0.0.1:8080;
  server 10.0.0.2:8080;
}
```

---

### ✔ TLS/HTTPS termination

Místo aby každá microservice řešila HTTPS:

* NGINX to ukončí na vstupu
* dovnitř jde už HTTP

---

## 3. Jak to souvisí s Kubernetes

V Kubernetes už NGINX často ani nepotřebuješ jako "ručně konfigurovaný server", protože:

* Kubernetes Service řeší routing interně
* Ingress controller často *je právě NGINX*

Typický stack:

```text
Internet
  ↓
:contentReference[oaicite:1]{index=1}
  ↓
Services
  ↓
Pods (Java microservices)
```

---

## 4. Kdy dává NGINX smysl u tebe (3 Docker mikroservisy)

### Ano, hodí se pokud:

* chceš jednoduchý entry point bez Kubernetes
* běžíš jen Docker / Docker Compose
* chceš skrýt porty
* chceš jednoduchý routing `/api/...`

👉 Pak je NGINX super jednoduché řešení.

---

### Už ho nepotřebuješ pokud:

* jdeš plně do Kubernetes (tam použiješ Ingress)
* chceš service discovery a scaling (to NGINX neřeší)

---

## 5. Reálné doporučení pro tvůj případ

Máš 3 Java mikroservisy v Dockeru:

👉 Nejjednodušší evolution:

### Varianta A (nejrychlejší)

Docker Compose + NGINX

### Varianta B (moderní)

Kubernetes + Ingress (NGINX Ingress Controller)

---

## Shrnutí

NGINX ti dá:

* jednu vstupní URL
* routing na služby
* základní load balancing
* skrývání interní sítě

Ale:

* **není service discovery**
* **není orchestrace**
* **není self-healing**

---

Jestli chceš, můžu ti nakreslit konkrétní architekturu pro tvoje 3 Java služby (Docker Compose vs Kubernetes vs hybrid s NGINX), aby bylo jasné, co přesně bys měl udělat krok za krokem.

---

# ok, tak jak rozchodím nginx s tím, co jsem ti napsal předtím?

Jdeme na to prakticky — u 3 Docker mikroservis je NGINX fakt rychlé řešení.

Budu předpokládat něco jako:

* `user-service` (Java, port 8080)
* `order-service` (port 8080)
* `notification-service` (port 8080)
* všechno běží v Dockeru

Cíl:
👉 jedna URL (NGINX) a routing na služby

---

# 1. Nejjednodušší varianta: Docker Compose + NGINX

Uděláš si ještě jeden kontejner: **NGINX jako gateway**

---

## 📁 Struktura

```text
project/
  docker-compose.yml
  nginx/
    default.conf
  user-service/
  order-service/
  notification-service/
```

---

# 2. docker-compose.yml

```yaml
services:

  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - user-service
      - order-service
      - notification-service

  user-service:
    image: user-service:latest
    expose:
      - "8080"

  order-service:
    image: order-service:latest
    expose:
      - "8080"

  notification-service:
    image: notification-service:latest
    expose:
      - "8080"
```

👉 důležité:

* `expose` = jen v rámci Docker network
* žádné `ports` → ven jde jen NGINX

---

# 3. NGINX konfigurace (default.conf)

```nginx
server {
    listen 80;

    # USER SERVICE
    location /users/ {
        proxy_pass http://user-service:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # ORDER SERVICE
    location /orders/ {
        proxy_pass http://order-service:8080/;
    }

    # NOTIFICATION SERVICE
    location /notify/ {
        proxy_pass http://notification-service:8080/;
    }
}
```

---

# 4. Jak to funguje

Po spuštění:

```bash
docker compose up --build
```

---

## Volání:

### User service

```text
http://localhost/users/api/users
```

### Order service

```text
http://localhost/orders/api/orders
```

### Notification service

```text
http://localhost/notify/send
```

---

# 5. Co je důležité pochopit

## 🔹 Docker DNS

Uvnitř Docker network:

```text
user-service → IP automaticky
order-service → IP automaticky
```

Nemusíš řešit porty ani IP.

---

## 🔹 NGINX je jen router

Nedělá:

* service discovery
* health checks
* scaling

Jen:
👉 „kam poslat HTTP request“

---

# 6. Typická chyba

### ❌ špatně:

```nginx
proxy_pass http://localhost:8081;
```

### ✔ správně:

```nginx
proxy_pass http://user-service:8080;
```

Protože `localhost` uvnitř kontejneru ≠ host machine.

---

# 7. Co tobě to reálně přinese

✔ jedna vstupní URL
✔ žádné porty venku
✔ jednoduchý routing
✔ lepší struktura pro budoucí Kubernetes

---

# 8. Kdy tohle stačí a kdy ne

### Stačí:

* 3–10 mikroservis
* jeden server / VPS
* žádný cloud orchestration

### Nestačí:

* auto-scaling
* více serverů
* HA cluster
* service discovery

---

# 9. Pokud chceš další krok

Můžu ti navázat:

👉 jak přidat:

* HTTPS (Let’s Encrypt)
* load balancing (více instancí jedné služby)
* health checks
* nebo migraci z tohohle na Kubernetes Ingress (to je pak skoro 1:1)

Stačí říct.

# ok, může pak internetě servica A to posílat na NGINX jako obecný požadavek pro servisu B, nebo pořád interně servisy o sobě musí vědět, kde jsou a jak se mají volat?

Krátká odpověď:
👉 Ano, můžeš to navrhnout tak, aby **služby o sobě nevěděly nic konkrétního** a všechno šlo přes NGINX — ale není to automatické ani „default“ chování mikroservis.

Jsou v tom 3 běžné modely:

---

# 1) ❌ Přímo service A → service B (bez NGINX)

```text id="a1"
A → http://order-service:8080
```

✔ nejrychlejší
❌ slabá izolace
❌ A musí znát B

---

# 2) ⚠️ Service A → NGINX → Service B (proxy styl)

```text id="a2"
A → http://nginx/orders/api
  → order-service
```

Ano, tohle jde a funguje.

Ale důležité:
👉 Service A musí pořád vědět „kam na NGINX poslat request“

Např.:

```java id="a3"
httpClient.get("http://nginx/orders/api/orders/123");
```

Takže:

* A neví o IP B
* ale ví o URL routingu (API gateway styl)

---

# 3) ✔ Ideální „gateway model“ (doporučený)

```text id="a4"
A → NGINX / API Gateway → B
B → NGINX / API Gateway → C
```

Ale důležité pravidlo:

👉 mikroservisy **nevolají přímo jiné mikroservisy**
👉 všechno jde přes gateway (NGINX)

---

# Co to znamená prakticky

## ✔ Varianta, kterou chceš ty:

Ano, můžeš mít:

### Service A:

```text id="b1"
http://nginx/orders/123
```

### Service B:

```text id="b2"
http://nginx/users/456
```

👉 A i B znají jen NGINX (jediný endpoint)

---

# Ale pozor na jednu věc (důležité)

NGINX **není service discovery**

To znamená:

### ❌ špatně (křehké)

```nginx id="c1"
proxy_pass http://10.0.0.12:8080;
```

### ✔ správně

```nginx id="c2"
proxy_pass http://order-service:8080;
```

---

# Co se tím reálně mění v architektuře

## Bez NGINX:

```text id="d1"
A → B (přímé závislosti mezi servisy)
```

## S NGINX jako gateway:

```text id="d2"
A → NGINX → B
A → NGINX → C
```

👉 servis A už nepotřebuje vědět:

* IP
* port
* deployment detail

---

# Co NGINX NEvyřeší (častý omyl)

NGINX ti NEudělá:

* ❌ service discovery mezi službami
* ❌ orchestraci
* ❌ logiku „kdo koho smí volat“
* ❌ event-driven komunikaci

---

# Důležitá architektonická pravda

Existují 2 styly:

## 1) Request-driven (NGINX / API gateway)

* A → gateway → B
* jednoduché
* běžné pro menší systémy

## 2) Event-driven (Kafka apod.)

* A publikuje event
* B ho spotřebuje
* žádné přímé volání

---

# Co bych doporučil pro tvoje 3 služby

👉 teď (Docker + 3 služby):

✔ NGINX jako jediný vstup
✔ žádné přímé A → B volání
✔ jasné REST endpointy přes gateway

```text id="e1"
Client / Service A
        ↓
      NGINX
   ↓     ↓     ↓
User  Order  Notify
```

---

# Shrnutí jednou větou

👉 Ano, service A může volat service B přes NGINX, ale musí znát URL routu (ne interní IP/port), a pokud to chceš dělat správně, NGINX se stává jediným „známým bodem“ systému.

---

Jestli chceš, můžu ti ukázat i:

* jak z toho udělat „mini API gateway pattern“
* nebo jak to později přejde 1:1 do Kubernetes Ingressu (to je skoro stejný koncept)

# jak se tam dělá autorizace? když B potřebuje roli? a A volá B?

Tady se dostáváš k jedné z nejdůležitějších věcí mikroservis: **autorizace se nesmí „ztratit“ mezi službami**.

NGINX ti sám o sobě autorizaci nevyřeší – jen přeposílá requesty. O bezpečnost se musí postarat tvoje aplikace (nebo API gateway vrstva nad tím).

---

# 1) Základní princip

👉 Autorizace patří do každého mikroservisu, ne do NGINX

NGINX:

* jen posílá request
* může ověřit token (volitelně)
* ale nerozhoduje „co uživatel smí dělat v B“

---

# 2) Typický flow (A → B přes NGINX)

```text id="f1"
User → A → NGINX → B
```

Ale B musí vědět:

* kdo request poslal
* jakou má roli
* jestli to smí udělat

---

# 3) Standardní řešení: JWT token

Používá se:

* JWT

## Flow:

### 1. User se přihlásí

dostane token:

```json id="f2"
{
  "sub": "user123",
  "roles": ["USER", "ADMIN"]
}
```

---

### 2. A dostane request s tokenem

```http id="f3"
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

### 3. A volá B (a přepošle token)

```text id="f4"
A → B (přes NGINX)
Authorization: Bearer ...
```

👉 KLÍČOVÉ:
A **nesmí token zahodit**

---

### 4. B si token ověří sám

B:

* validuje podpis
* čte role
* rozhodne

```java id="f5"
if (roles.contains("ADMIN")) {
    // povol
} else {
    // 403 Forbidden
}
```

---

# 4) Role v praxi (tvůj případ)

## Service B chce roli ADMIN

### B neřeší:

* odkud request přišel
* jestli přišel z A nebo přímo od usera

### B řeší:

👉 „má token správnou roli?“

---

# 5) Důležitá zásada mikroservis

👉 každá služba je **nezávisle bezpečnostně validní**

To znamená:

❌ špatně:

* A říká B: „věř mi, user je admin“

✔ správně:

* B si sám ověří JWT

---

# 6) Co může dělat NGINX navíc (volitelné)

NGINX může:

### ✔ jen forwardovat token

```nginx id="n1"
proxy_set_header Authorization $http_authorization;
```

---

### ⚠️ nebo dělat autentizaci (méně běžné)

* přes OAuth 2.0
* nebo OpenID Connect

Ale to už je „API gateway level“, ne obyčejný NGINX setup.

---

# 7) Kdo má dělat co (správná architektura)

## ✔ NGINX

* routing
* TLS
* (volitelně auth pre-check)

## ✔ Service A

* předává token dál
* neřeší roli B

## ✔ Service B

* validuje JWT
* rozhoduje o roli

---

# 8) Častý problém (a důležitý insight)

## ❌ Anti-pattern

```text id="x1"
A → B: "tenhle user je admin, trust me"
```

👉 to je špatně, protože A by se dalo obejít

---

## ✔ Správně

```text id="x2"
A → B: JWT token
B → validace podpisu + role
```

---

# 9) Ještě lepší varianta (pro větší systémy)

Používá se:

* central identity provider (např. Keycloak)
* služby jen validují token

---

# Shrnutí

👉 Autorizace v mikroservisách funguje takto:

* NGINX = router
* JWT = nosič identity + rolí
* každá služba = vlastní rozhodnutí o přístupu
* A nikdy „neříká B co smí“ bez důkazu (tokenu)

---

Jestli chceš, můžu ti ukázat:

* konkrétní Spring Boot security konfiguraci (JWT validation)
* nebo jak udělat, aby A volalo B přes Feign/WebClient správně s token propagation
* nebo jak to celé zjednodušuje API Gateway vs čistý NGINX

Tohle je přesně bod, kde se z „docker projektu“ stává „opravdová microservice architektura“.

