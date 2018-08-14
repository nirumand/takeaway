# Takeaway.com  Employee Service
This folder hosts source code for the microservice employee-service. 
In addition, the dockerfile and instruction how to compile and build the project into a container is provided.
Moreover, important classes are documented using Javadocs. 

## Introduction
The employee-service is responsible for creating, updating, deleting and retrieving employee resources persisted in the employee-service database. For each of the DML operations, the service publishes an event into the message broker.

An example event is provided below. The following event is published when the employee entity is persisted.
```json
{
  "eventID": "3d2a42cd-e016-4b46-af93-da2679f079d2",
  "timestamp": "2018-08-14T22:22:02.637+02:00",
  "eventName": "EMPLOYEE_CREATED",
  "employeeId": "d453dadf-9a1b-4207-91c8-77163324918b",
  "eventBody": "{\"employeeId\":\"d453dadf-9a1b-4207-91c8-77163324918b\",\"email\":\"rezas@nirumand.com\",\"fullName\":\"Reza Nirumand\",\"birthday\":\"1983-05-20\",\"hobbies\":[\"Guitar\",\"Piano\"]}"
}
```
## Endpoints
The API online documentation is accessible under URL (assuming running locally):  
```http request
http://127.0.0.1:8080/swagger-ui.html
```
## Technical Aspects

The employee-service consists of following main packages:  

- Boundary: Is a rest controller which offers multiple endpoint for managing employee entities.
- Service: Responsible for managing root aggregates (here Employee). It consists of two services, namely EmployeeService and KafkaService.   
The EmployeeService provides service to REST controller and communicates with *EmployeeRepository* for CRUD operations. The KafkaService is responsible for publishing events into the configured topic.
- Repository: Is responsible for persisting and retrieving an event or a list of events.
- Model: Is the main entity persisted into the database. In this service, the only entity is *Employee*.

Also, the application is configurable using *application.yml* file. For each profile, there exists a configuration file which are located under *resources* folder.

## Running
Followings are required to compile, run the application:

- Java 1.8 jre/jdk
- Maven
- Docker
- Postgres database
- Kafka message broker

Simplest way to run the application is as follows, otherwise the data source settings should be adjusted in the *application.yml* for the *dev* profile.

1) make sure a kafka instance is already running. If not, run following:  
```bash
docker run -d --rm -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 --name kafka -p 2181:2181 -p 9092:9092 -p 8000:8000 spotify/kafka
```

You can watch the events on Kafka using consumer-console:
```bash
docker exec -ti kafka /opt/kafka_2.11-0.10.1.0/bin/kafka-console-consumer.sh –bootstrap-server localhost:9092 –topic codechallenge
```

2) run a postgres database on port 8432:
```bash
docker run -d --rm -p 8432:5432 --name employee-service-db -e POSTGRES_USER=employeeservice -e POSTGRES_PASSWORD=employeeservice postgres:alpine -d employeeservice
```

3) From the employee-service folder, package the application and then run it:
```bash
mvn clean package
java -jar .\target\employee-service.jar
```

To build the docker-image: 
```bash
docker build . -t employee-service:v1
```

## Clean up
To clean up all the created containers using following instructions:
```bash
docker rm employee-service -f
docker rm employee-service-db -f
docker rmi employee-service:v1 -f
```
