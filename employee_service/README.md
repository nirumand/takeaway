# Takeaway.com  Event Service 
This folder hosts source code for the microservice employee-service. 
In addition, the dockerfile and instruction how to compile and build the project into a container is provided.
Moreover, important classes are documented using Javadocs. 

## Introduction
The employee-service is responsible for persisting employee's specific events published in the message broker and retrieving this event.
These events are consumed by another microservice, namely *event-service*. 


An event has following fields. For example, following events is published when an employee entity is created.
```json
{"timestamp":"2018-08-09T10:22:44.118+02:00","eventName":"EMPLOYEE_CREATED","employeeId":"a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8","eventBody":"Employee{uuid=a5616ebe-f508-4b4e-a55a-c41e9fe8b9e8, email='reza@nirumand.com', fullName='Reza Nirumand', birthday='2018-08-09', hobbies=[Guitar, Piano]}"}
```
## Endpoints
The employee-service offers multiple REST APIs to create, update , delete and retrieve events.
```http request
http://127.0.0.1:8080/employees/{employeeId}
```

```bash
curl -X POST \
  http://localhost:8080/employees \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 27bf83d4-92ab-e1d7-09e7-c3b5338bd5cd'
```

Depending on the request processing status, following HTTP.Status are used:v  

- 200: When the input employeeId is valid UUID and a list of events are found and retrieved.
- 400: When the input is not valid or the client does not provide a valid request structure.
- 404: When the input employeeId is valid UUID but no events related to the specified id is found.

The API online documentation is accessible under URL (assuming running locally):  
```http request
http://127.0.0.1:8085/swagger-ui.html
```

## Technical Aspects
The employee-service consists of following main packages:  

- Boundary: Is a rest controller which offers an endpoint for clients to access the servie using REST API.
- Service: Responsible for managing root aggregates (here events). It consists of two services, namely EventService and KafkaService.   
The EventService provides service to REST controller and communicates with *EventRepository* for retrieving events.
The KafkaService listens to *message broker* in order to consume events. Any message published in the configured topic will be processed.
The Processing of events consists of validating and parsing the structure of a event as a *BusinessEvent* and finally persisting to the configured database.
- Repository: Is responsible for persisting and retrieving an event or a list of events.
- Model: Is the main entity persisted into the database. In this service, the only entity is *BusinessEvent*.

Also, the application is configurable using *application.yml* file. For each profile, there exists a configuraiton file which are located under *resources* folder.

## Running
Followings are required to compile, run the application:

- Java 1.8 jre/jdk
- Maven
- Docker
- Postgres database
- Kafka message broker

Simplest way to run the application is as follows, otherwise the datasource settings should be adjusted in the application.yml files for dev profile.

1) make sure a kafka instance is already running. If not, use following:  
```bash
docker run -d -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 �-name kafka -p 2181:2181 -p 9092:9092 -p 8000:8000 spotify/kafka
```

You can watch the events on Kafka using consumer-console:
```bash
docker exec -ti kafka /opt/kafka_2.11-0.10.1.0/bin/kafka-console-consumer.sh �bootstrap-server localhost:9092 �topic codechallenge
```

2) run a postgres database on port 7432:
```bash
docker run -d �-rm -p 8432:5432 -�name employee-service-db -e POSTGRES_USER=employeeservice -e POSTGRES_PASSWORD=employeeservice postgres:alpine -d employeeservice
```

3) From the employee-service folder, package the application and then run it:
```bash
mvn clean package
java -jar .\target\employee-service.jar
```

4) Another way to run the applicaiton is using a docker container for the application, but this is not recommended for this case.
Note, under windows additional configuration is required, because the localhost in the container refers to linux virtual machine, hence we need to refer to host network for our configurations.
We need to run all the infrastructure services such as kafka and databases under host network and run the application container under two network, Once which is connected to host network and another which is connected to bridge network. 
As of my experience, the host network under windows is very buggy and does not worth investing time. My suggestion for this task is to just run the jar file from command line.

To build the docker-image: 
```bash
docker build . -t employee-service:v1
```

## Example Scenario
To better understand what the service does, we can run following scenario:

On employee-Service:

1) Create an employee using post command. 
2) GET resource to verify if the employee is created
3) Update the employee using put command.
4) GET Resource to verify if the changes are applied
5) Delete the resource
6) Try to GET the resource. A Resource not found message should be returned.

On event-Service

- Monitor the events on kafka using kafka-consumer script.
- Use the event-service endpoint to retrieve all the events specific to an employeeId

Finally we will delete the create employee entity. 

## Clean up
To clean up all the created containers using following instructions:
```bash
docker rm kafka -f
docker rm event-service-db -f
docker rm employee-service -f
docker rmi employee-service:v1 -f
```