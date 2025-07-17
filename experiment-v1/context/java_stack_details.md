**Tech Stack:**
Java
Spring Boot (with Spring WebFlux for reactive APIs)
Build Tool - Gradle
Database - PostgreSQL, Flyway for schema versioning, Separate test db for testing
Logging - SLF4J with Logback, configure levels INFO, DEBUG, ERROR
Utilities - Mockito
Testing dependencies: JUnit 5, Mockito, Reactor Test
No Lombok — explicitly generate all boilerplate code

Postgres database access - user : postgres, password : postgres, database name : promotion_engine_v1

Code Quality - Sonar 

Integrate SonarQube without docker , plugin org.sonarqube (latest version), configured to run analysis on local server at http://localhost:9000

Derive Database Schema from API contracts

Dependency Versions: Latest compatible versions can be used

Project structure with packages:
    * controller
    * service
    * repository
    * model
    * dto.request // optional if needed
    * dto.response
    * exception

### Library Versions ###
Java - 17
jacoco - 0.8.13
Sonarqube - 6.2.0.5505
Mockito - 5.14.2
Spring Boot 3.4.4
gradle-version 8.14.2

