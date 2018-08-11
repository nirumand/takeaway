# Takeyaway.com Code challenge
This repository hosts the source code of the code challenge for the company takeawayc.om

# Introduction
The code challenge consists of implementing two Microservices, namely Employee-Service and Event-Service.
Each service is located in its own folder. The detail explanation of the services are located in the service folder.


## Running
The are various vays to run this project. Following is the simplest and requires:

- Java 1.8 jdk
- Maven
- Docker
- Kafka (as docker image)
- Postgres ( as docker image)

To run the code challenge do the followings:

1) Run Kafka as message broker.
```bash
docker run -d -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 –-name kafka -p 2181:2181 -p 9092:9092 -p 8000:8000 spotify/kafka
```
2) Run two databases, one for employee-service and one for event-service
```bash
docker run -d –-rm -p 8432:5432 -–name employee-service-db -e POSTGRES_USER=employeeservice -e POSTGRES_PASSWORD=employeeservice postgres:alpine -d employeeservice
docker run -d -–rm -p 7432:5432 -–name event-service-db -e POSTGRES_USER=eventservice -e POSTGRES_PASSWORD=eventservice postgres:alpine -d eventservice
```
3) Package the source code for each Microservice to a jar file.
```bash
## assuming the current directory is "takeaway" directory
mvn -f .\employee_service\pom.xml clean package
mvn -f .\event_service\pom.xml clean package
```
4) The employee-service and event-service are accessible under ports 8080,8085 respectively. 

