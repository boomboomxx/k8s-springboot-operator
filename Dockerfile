FROM harbor.szistech.com/docker.io/eclipse-temurin:21.0.5_11-jdk
WORKDIR /apps
ENV JVM_OPTS="-Djava.security.egd=file:/dev/./urandom"
ADD target/spring-boot-operator-0.1.0-SNAPSHOT.jar /apps/spring-boot-operator.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c", "java $JVM_OPTS -jar /apps/spring-boot-operator.jar" ]