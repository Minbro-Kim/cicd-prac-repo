FROM azul/zulu-openjdk:17-jre-headless-latest

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
