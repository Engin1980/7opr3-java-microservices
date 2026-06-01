w# Debugging Java Microservices v Docker - Nastavení

## Problém
Při spuštění remote debug v IDEA hlásí:
- "handshake failed"
- "unable to open port"
- "connection prematurely closed"

## Opravy, které byly provedeny

### 1. ✅ Dockerfily - přidání EXPOSE příkazů
- **AppUserService**: Přidán `EXPOSE 9201` (debug port)
- **AuthService**: Přidán `EXPOSE 9301` (debug port)

### 2. ✅ JDWP parametry - změna adresy
Všechny služby nyní používají:
```
address=0.0.0.0:PORT
```
místo `address=*:PORT` pro lepší kompatibilitu s Windows Docker Desktop.

## Kroky k vyřešení problému v IDEA

### Krok 1: Smazání starých kontejnerů a images
```bash
docker compose down
docker image prune -a
```

### Krok 2: Rebuild images s novými Dockerfile
```bash
docker compose build --no-cache
```

### Krok 3: Spuštění docker-compose
```bash
docker compose up
```

### Krok 4: Konfiguraci Remote Debug v IDEA

1. Jděte do: **Run → Edit Configurations**

2. Klikněte na **+ (Add New Configuration)** a vyberte **Remote JVM Debug**

3. Pro každou službu vytvořte konfiguraci:

#### AppLogService Debug Config:
- **Name**: `AppLogService Debug`
- **Host**: `localhost`  (nebo `127.0.0.1`)
- **Port**: `9101`
- **Search sources using module's classpath**: ✓ zatrhněte
- **Use module classpath**: ✓ vyberte `AppLogService`

#### AppUserService Debug Config:
- **Name**: `AppUserService Debug`
- **Host**: `localhost`
- **Port**: `9201`
- **Use module classpath**: ✓ vyberte `AppUserService`

#### AuthService Debug Config:
- **Name**: `AuthService Debug`
- **Host**: `localhost`
- **Port**: `9301`
- **Use module classpath**: ✓ vyberte `AuthService`

### Krok 5: Spuštění debuggeru

1. Nastavte breakpoint v kódu
2. V IDEA klikněte **Run → Debug** a vyberte příslušnou konfiguraci (např. `AppLogService Debug`)
3. Byť měla být zpráva: `Connected to the target VM, address: 'localhost:9101', transport: 'socket'`

## Pokud stále nefunguje:

### 1. Ověřit, že kontejner běží a port je dostupný:
```bash
docker ps  # ověřit që AppLogService běží
netstat -an | findstr 9101  # ověřit port je listenet
telnet localhost 9101  # test připojení
```

### 2. Zkontrolovat logy kontejneru:
```bash
docker logs applog-service
```

### 3. Ověřit firewall:
- Zkontrolujte Windows Firewall - zkontrolujte, že nejsou blokovány porty 9101, 9201, 9301

### 4. Pokud Docker běží v WSL2:
```bash
# Zjistit IP adresu WSL2
wsl hostname -I
```
Pak v IDEA použít tuto IP adresu místo `localhost`.

### 5. Nastavit suspend=y (během testování):
Pokud chcete, aby se aplikace zastavila při spuštění a čekala na debugger:
```
address=0.0.0.0:PORT,suspend=y
```
Změňte `suspend=n` na `suspend=y` v Dockerfile.

## Ověření funkcía debug portu

```bash
# Spustit netstat a zkontrolovat debug porty
netstat -an | findstr LISTENING | findstr "910\|920\|930"
```

Měly by se zobrazit:
```
  TCP    127.0.0.1:9101         0.0.0.0:0              LISTENING
  TCP    127.0.0.1:9201         0.0.0.0:0              LISTENING
  TCP    127.0.0.1:9301         0.0.0.0:0              LISTENING
```

## Debugování v IDEA bez Docker

Pokud chcete ladit bez Docker:

1. Spusťte aplikaci lokálně přes Maven:
```bash
cd AppLogService
mvn spring-boot:run
```

2. Nebo použijte IDEA run configuration s editací:
```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:9101 -jar target/app.jar
```

