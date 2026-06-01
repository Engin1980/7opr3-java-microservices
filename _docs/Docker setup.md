2) Dockerfile (správná optimalizace layer cache)

Tvoje verze funguje, ale zpomaluje cache invalidaci.

Použij tohle:
```
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# 1. jen pom.xml (cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. zdroják až potom
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 9100
EXPOSE 9101

ENTRYPOINT ["java",
"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9101",
"-jar",
"app.jar"
]
```

