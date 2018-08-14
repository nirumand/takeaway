{:toc max_level=3 }

# Takeaway.com Code challenge
This repository hosts the source code of the code challenge for the company takeaway.com

## Summary
The code challenge consists of implementing two microservices, namely *employee-Service* and *event-Service*.
Each service is located in its own folder. 
The employee-service is responsible for creating, updating, deleting and retrieving an employee resource.
The employees are also persisted in a postgres database. 
The employee-service publishes an event for each of the create, update and delete operations.  
The event-service is responsible for persisting the events in a database. 
In addition the events, can be retrieved from the event-service as a list of events.
Both services offer the resources as REST API endpoints. The detail explanation of the services are located in the respective service folder. 
The Kafka is used as the message broker and can be run in a docker container. 
For each microservice, there exists a postgres database instance as a docker container

## Recommendations (not implemented)
Followings are couple of recommendations for extending this code challenge. Due to lack of time, they are not implemented on this task:

- Use of Keycloak for authentication
- Use of Flyway for database source code versioning.
- Use of spring-cloud-config for centralized management
- Use of a parent pom to have the same version of libraries for all the services.
- Use of arc42 for documentation alongside the code. Therefore, the documentation is versioned properly with the code.


## Running
The are various ways to run this project. Following is the simplest and requires:

- Java 1.8 jdk
- Maven
- Docker
- Kafka (as docker container)
- Postgres (as docker container)

To run the code challenge do the followings.
Please note the application does not start and work properly if kafka and postgres database is not started.

1) Run Kafka as a message broker.
```bash
docker run -d –-rm -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 –-name kafka -p 2181:2181 -p 9092:9092 -p 8000:8000 spotify/kafka
```
2) Run two database instances, one for employee-service and one for event-service
```bash
docker run -d –-rm -p 8432:5432 -–name employee-service-db -e POSTGRES_USER=employeeservice -e POSTGRES_PASSWORD=employeeservice postgres:alpine -d employeeservice
docker run -d -–rm -p 7432:5432 -–name event-service-db -e POSTGRES_USER=eventservice -e POSTGRES_PASSWORD=eventservice postgres:alpine -d eventservice
```
3) Package the source code for each microservice to a jar file.
```bash
# assuming the current directory is "takeaway" directory
mvn -f .\employee_service\pom.xml clean package
mvn -f .\event_service\pom.xml clean package
```
5) Run the jar files. The employee-service and event-service are accessible under ports 8080,8085 respectively.
```http request
# On windows
start java -jar .\event_service\target\event-service.jar
start java -jar .\employee_service\target\employee-service.jar

# on linux
java -jar ./event_service/target/event-service.jar &
java -jar ./event_service/target/event-service.jar &
```

## Example Scenario
To better understand what the service does, we can run following scenario:

On employee-Service:

1) Create an employee using post command. 
```http request
curl -X POST \
  http://localhost:8080/employees \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 4b8d11cf-e6b5-d919-10f8-773c2fcbe638' \
  -d '{
    "email": "reza@nirumand.com",
    "fullName": "Reza Nirumand",
    "birthday": "1983-05-20",
    "hobbies": [
        "Guitar",
        "Piano"
    ]
}'
```
2) GET resource to verify if the employee is created. Note, the UUID is random.
```http request
curl -X GET \
  http://127.0.0.1:8080/employees/8d50f192-0cf2-4d54-9f73-cdebf9f74a85 \
  -H 'cache-control: no-cache' \
  -H 'postman-token: f962dc2b-269b-eed4-d811-dd7358e9b9fd'
```
3) Update the employee using put command.
```http request
curl -X PUT \
  http://127.0.0.1:8080/employees/8d50f192-0cf2-4d54-9f73-cdebf9f74a85 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 571cb983-fec5-f13b-9927-ce495e766cb5' \
  -d '{
    "email": "reza2@nirumand.com",
    "fullName": "Reza Nirumand",
    "birthday": "1983-05-20",
    "hobbies": [
        "Guitar",
        "Piano"
    ]
}'
```
4) GET Resource to verify if the changes are applied (same as step 2)
5) Delete the created employee resource
```http request
curl -X DELETE \
  http://127.0.0.1:8080/employees/8d50f192-0cf2-4d54-9f73-cdebf9f74a85 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: db4658c0-1623-83ab-bb63-b42bf9f3564d'
```
6) Try to GET the resource. A Resource not found message should be returned. (same as step 2)

On event-Service
- Monitor the events on kafka using kafka-consumer script.
- Use the event-service endpoint to retrieve all the events specific to an employeeId
```http request
curl -X GET \
  http://localhost:8085/events/8d50f192-0cf2-4d54-9f73-cdebf9f74a85 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: fb1ed110-9a08-bec6-5af2-089a69ab4aff'
```

## Cleanup
To clean up created containers do followings:
```bash
docker rm event-service-db -f
docker rm employee-service-db -f
docker rm kafka -f
```
