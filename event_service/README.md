# Takeaway.com  Event Service 
This folder hosts source code for the microservice event-service. 
In addition, the dockerfile and instruction how to compile and build the project into a container is provided.
Moreover, important classes are documented using Javadocs. 

## Introduction
The event-service is responsible for persisting employee's specific events published in the message broker and retrieving these events.
These events are produced by another microservice, namely *employee-service*. 


## Endpoints
The API online documentation is accessible under URL (assuming running locally):  
```http request
http://127.0.0.1:8085/swagger-ui.html
```

## Technical Aspects
The event-service consists of following main packages:  

- Boundary: Is a rest controller which offers an endpoint for clients to get the persisted events using a REST API.
- Service: Responsible for managing root aggregates (here events). It consists of two services, namely EventService and KafkaService.   
The EventService provides service to REST controller and communicates with *EventRepository* for retrieving events.
The KafkaService listens to *message broker* in order to consume events. Any message published in the configured topic will be processed.
The Processing of events consists of validating and parsing the structure of an event and finally persisting to the configured database.
- Repository: Is responsible for persisting and retrieving an event or a list of events.
- Model: Contains the event entity.

Also, the application is configurable using *application.yml* file. For each profile, there exists a configuration file which are located under *resources* folder.

## Running
Followings are required to compile, run the application:

- Java 1.8 jre/jdk
- Maven
- Docker
- Postgres database
- Kafka message broker

Simplest way to run the application is as follows, otherwise the data source settings should be adjusted in the application.yml files for dev profile.

1) make sure a kafka instance is already running. If not, use following:  
```bash
docker run -d -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 –-name kafka -p 2181:2181 -p 9092:9092 -p 8000:8000 spotify/kafka
```

You can watch the events on Kafka using consumer-console:
```bash
docker exec -ti kafka /opt/kafka_2.11-0.10.1.0/bin/kafka-console-consumer.sh –bootstrap-server localhost:9092 –topic codechallenge
```

2) run a postgres database on port 7432:
```bash
docker run -d -–rm -p 7432:5432 -–name event-service-db -e POSTGRES_USER=eventservice -e POSTGRES_PASSWORD=eventservice postgres:alpine -d eventservice
```

3) From the event-service folder, package the application and then run it:
```bash
mvn clean package
java -jar event-service.jar

```
To build the docker-image: 
```bash
docker build . -t event-service:v1
```

## Clean up
To clean up all the created containers using following instructions:
```bash
docker rm event-service-db -f
docker rm event-service -f
docker rmi event-service:v1 -f
```