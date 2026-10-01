# currency-service

Requirements: JDK 17+ and Maven 3.6+.

Run:
    mvn spring-boot:run

Or build a jar:
    mvn clean package
    java -jar target/currency-service-0.0.1-SNAPSHOT.jar

Test (rates load ~5 seconds after startup):
    curl http://localhost:8080/api/rates
    curl http://localhost:8080/api/rates/EUR
    curl http://localhost:8080/api/rates/INR
