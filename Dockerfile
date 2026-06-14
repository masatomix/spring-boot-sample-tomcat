# マルチステージビルド（task#1056 Step 3）。
# 旧来は openjdk:17-alpine（メンテ終了イメージ）に事前ビルド済み jar を COPY する方式
# だったが、ビルドからイメージ内で完結させ、実行は eclipse-temurin の JRE で行う。

# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
# 依存解決を先に行いレイヤキャッシュを効かせる
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# テストは CI(mvn verify)で実施済みのためイメージビルドでは省略
RUN mvn -B -q clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
VOLUME /tmp
COPY --from=build /workspace/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
