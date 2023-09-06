# FROM maven:3.5.2-jdk-8  AS build1
# RUN mkdir -p /opt/java/src/
# ADD ./pom.xml /opt/java/
# ADD ./src /opt/java/src
# RUN cd /opt/java && mvn install

FROM openjdk:17-alpine
VOLUME /tmp
ARG JAR_FILE
# COPY --from=build1 /opt/java/${JAR_FILE} app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
# 
# ENTRYPOINT ["java","-jar","${JAVA_OPTS}","/app.jar"]
# ENTRYPOINT ["sh","-c","java -jar /app.jar ${JAVA_OPTS}"]
ENTRYPOINT ["java","-jar","/app.jar"]