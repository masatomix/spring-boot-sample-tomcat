
### 実行

```bash
$ git clone https://github.com/masatomix/spring-boot-sample-tomcat.git 
$ cd spring-boot-sample-tomcat
$ mvn eclipse:clean eclipse:eclipse
$ mvn spring-boot:run
```

### jar作成

```
$ mvn install
$ java -jar ./target/app.jar --spring.profiles.active=dev
```
実行jarができている

### dockerのイメージ作成、ECRへの手動Push

```
$ ECR_REPOSITORY_NAME=spring-boot-sample-tomcat
$ version=0.0.6-SNAPSHOT  <- pom.xmlのバージョンを使おう

$ AWS_REGION_NAME=ap-northeast-1
$ AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
$ aws ecr --region  ${AWS_REGION_NAME} get-login-password | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com

$ REPOSITORY_URI=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION_NAME}.amazonaws.com/${ECR_REPOSITORY_NAME}

# $ docker image build --build-arg JAR_FILE=target/app.jar -t ${REPOSITORY_URI}:${version} .
$ docker image build -t ${REPOSITORY_URI}:${version} .
$ docker push ${REPOSITORY_URI}:${version}
```
