FROM openjdk:17-oracle
VOLUME /tmp
COPY build/libs/crypto-recommendation-service-*.jar app.jar
ADD csv/prices /tmp/csv/prices
ENTRYPOINT ["java","-jar","/app.jar"]