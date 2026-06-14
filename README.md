# spring-boot-sample-tomcat

Spring Boot の Web アプリケーションサンプル。REST API・JWT(Bearer トークン)認証・
Actuator・JSON 形式ログ・Docker 化のデモを含む。

- Spring Boot 4.0 / Java 21
- JWT 検証は Spring Security の OAuth2 Resource Server（Firebase の JWK Set を利用）
- ログは logback + logstash-logback-encoder（JSON 出力に対応）

## 必要環境

- Java 21
- Maven 3.9+
- （任意）Docker

## 実行

```bash
# 開発実行
$ mvn spring-boot:run

# jar を作成して実行（profile 指定例）
$ mvn package
$ java -jar ./target/app.jar --spring.profiles.active=dev
```

`target/app.jar` が実行可能 jar（組み込み Tomcat）。

## エンドポイント

| メソッド | パス | 認証 | 説明 |
|---|---|---|---|
| GET | `/status` | 不要 | 死活確認。`hello` を返す |
| POST | `/echo` | **JWT 必須** | Bearer トークン検証後、受領した JSON を返す |
| GET/POST | `/echo2` | 不要 | GET は `hello!!!`、POST は受領 JSON を返す |
| POST | `/echoBody` | 不要 | 受領した JSON を返す |
| GET | `/echoLogger`, `/echoLogger1` | 不要 | ログレベル(DEBUG/INFO/WARN/ERROR)出力のデモ |
| GET | `/echoSysout` | 不要 | `System.out` 出力のデモ（`/echoLogger` との対比用） |
| POST | `/exception` | 不要 | 400/500 系エラー応答のデモ |
| GET | `/save`, `/load` | 不要 | `HttpSession` への保存・読み出しデモ |
| GET | `/actuator/health`, `/actuator/info` | 不要 | Actuator（公開はこの2つのみに限定） |

### JWT 認証（`POST /echo`）

`POST /echo` のみ有効な Bearer トークン（Firebase の ID トークン）を要求する。検証鍵は
`application.properties` の `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` から取得し、
署名・有効期限を検証する（issuer/audience の厳密検証はプロジェクト ID を用いて設定する将来の改善）。

```bash
$ curl -X POST http://localhost:8080/echo \
    -H "Authorization: Bearer <ID_TOKEN>" \
    -H "Content-Type: application/json" \
    -d '{"id":"1","name":"taro"}'
```

トークンが無い・不正な場合は 401 を返す。

## Docker

マルチステージビルド（`maven:3.9-eclipse-temurin-21` でビルド、`eclipse-temurin:21-jre` で実行）。

```bash
$ docker image build -t spring-boot-sample-tomcat .
$ docker container run --rm -p 8080:8080 --name app spring-boot-sample-tomcat
```

### ECR への手動 Push（参考）

```bash
$ ECR_REPOSITORY_NAME=spring-boot-sample-tomcat
$ version=$(mvn -q help:evaluate -Dexpression=project.version -DforceStdout)
$ AWS_REGION_NAME=ap-northeast-1
$ AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
$ aws ecr --region ${AWS_REGION_NAME} get-login-password \
    | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com
$ REPOSITORY_URI=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com/${ECR_REPOSITORY_NAME}
$ docker image build -t ${REPOSITORY_URI}:${version} .
$ docker image push ${REPOSITORY_URI}:${version}
```

## Redis セッション（※未検証・参考、現在は無効）

Spring Session でセッション保存先を Redis にする構成は、依存・設定とも**コメントアウトして無効化**
している（`pom.xml` / `application.properties`）。有効化する場合の手順は [README_redis.md](README_redis.md) を参照。
記載内容は更新前のもので未検証のため、利用時は最新版で確認すること。
