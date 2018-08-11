# Takeyaway.com Code challenge
This repository hosts the source code of the code challenge for the company takeaway.com

## Summary
The code challenge consists of implementing two microservices, namely *employee-Service* and *event-Service*.
Each service is located in its own folder. 
The employee-service is responsible for creating, updating, deleting and retrieving an employee resource.
The employees are also persisted in a postgres database. 
The employee-service publishes an event for each of the create, update and delete operations.  
The event-service is responsible for persisting the events in a database. 
In addition the events, can be retrieved from the event-service as a list of events.
Both services offer the resources as REST API endpoints. The detail explanation of the services are located in the respective service folder. \\
The Kafka is used as message and will be run as docker container. 
For each microservice, there exists a postgres database instance as a docker container
## Running
The are various ways to run this project. Following is the simplest and requires:

- Java 1.8 jdk
- Maven
- Docker
- Kafka (as docker container)
- Postgres (as docker container)

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
3) Package the source code for each microservice to a jar file.
```bash
## assuming the current directory is "takeaway" directory
mvn -f .\employee_service\pom.xml clean package
mvn -f .\event_service\pom.xml clean package
```
4) The employee-service and event-service are accessible under ports 8080,8085 respectively.

## Cleanup
To clean up created containers do followings:
```bash
docker rm event-service-db -f
docker rm employee-service-db -f
docker rm kafka -f
```