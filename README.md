
### 実行

```bash
$ git clone https://github.com/masatomix/spring-boot-sample-tomcat.git  ← 実際はほしいbranchを取得
$ cd spring-boot-sample-tomcat
# $ mvn eclipse:clean eclipse:eclipse
$ mvn spring-boot:run
```

### jar作成して、実行

```
$ mvn install
$ java -jar ./target/app.jar --spring.profiles.active=dev  ← profileを指定する方法
```

実行jarができていて、上記のようにTomcatサーバを起動できます。


### dockerのイメージ作成、ECRへの手動Push、docker上で実行

```
$ ECR_REPOSITORY_NAME=spring-boot-sample-tomcat
$ version=0.0.3  <- pom.xmlのバージョンにあわせるのがよさそう

$ AWS_REGION_NAME=ap-northeast-1
$ AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
$ aws ecr --region  ${AWS_REGION_NAME} get-login-password | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com

$ REPOSITORY_URI=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com/${ECR_REPOSITORY_NAME}

$ docker image build -t ${REPOSITORY_URI}:${version} .
$ docker image push ${REPOSITORY_URI}:${version}
```

下記の感じのコマンドでDocker上で起動ができます。

```
$ docker container run --rm -p 8080:8080 \
 --link redis:REDIS_HOST \
 -e --spring.profiles.active=dev \
 --name app \
 {REPOSITORY_URI}:${version}
```


### redis起動(SpringSessionなどでSessionの保存先をRedisにしている場合)

```
docker run --rm -p 6379:6379 -d --name redis redis
```

これで起動したRedisには localhost:6379 で接続できます。
SpringBootをDockerで起動した場合は、localhostではないので、


```
docker run --rm -p 6379:6379 -d --name redis redis
docker run --rm -p 8080:8080 --link redis:REDIS_HOST -e --spring.profiles.active=dev --name myapp myorg/myapp
```

などDockerのlink機能でホスト名を規定(REDIS_HOST)して、接続先を設定ファイルで切り替えます。
上記は profileをdevにしているので、application-dev.properties に

```
# Redis server host.
spring.data.redis.host=REDIS_HOST
```

とかしておけばOKですね。