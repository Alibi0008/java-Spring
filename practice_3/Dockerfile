# ---- stage 1: build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Копируем pom.xml и wrapper до исходников, чтобы кэшировать слой зависимостей
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q package -DskipTests

# ---- stage 2: extract layered jar ----
FROM eclipse-temurin:21-jre AS extract
WORKDIR /x
COPY --from=build /src/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination out

# ---- stage 3: hardened runtime ----
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 spring
USER 10001

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
WORKDIR /app

# Копируем слои от редко меняющихся к часто меняющимся
COPY --from=extract /x/out/dependencies/ ./
COPY --from=extract /x/out/spring-boot-loader/ ./
COPY --from=extract /x/out/snapshot-dependencies/ ./
COPY --from=extract /x/out/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
