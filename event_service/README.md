# Takeaway.com  Event Service 
This folder hosts source code for the Microservice event-service. 
In addition, the dockerfile and instruction how to compile and build the project into a container is provided.
Moreover, important classes are documented using Javadocs. 

## Introduction
The event-service is responsible for persisting employess specific events published in the message broker and retrieving this event.
These events are produced by another Microservice, namely *employee-service*. 


## Endpoints
The event-service offers one REST API to retrieve events using GET method. As an input, the API accepts a valid UUID as *employeeId*.
For each emoployeeId, there can exist many events. Therefore, the API returns a JSONArray of events.
```http request
http://127.0.0.1:8085/events/{employeeId}
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
The event-service consists of following main packages:  

- Boundary: Is a rest controller which offers an endpoint for clients to access the servie using REST API.
- Service: Responsible for managing root aggregates (here events). It consists of two services, namely EventService and KafkaService.   
The EventService provides service to REST controller and communicates with *EventRepository* for retrieving events.
The KafkaService listens to *message broker* in order to consume events. Any message published in the configured topic will be processed.
The Processing of events consists of validating and parsing the structure of a event as a *BusinessEvent* and finally persisting to the configured database.
- Repository: Is responsible for persisting and retrieving an event or a list of events.
- Model: Is the main entity persisted into the database. In this service, the only entity is *BusinessEvent*.
- Configuration Files: Application is configurable using *application.yml* file. For each profile, there exists a configuraiton filie which are located under *resources* folder.

## Running
Followings are required to compile, run the application:

- Java 1.8 jre/jdk
- Maven
- Docker
- Postgres database
- Kafka message broker

Simplest way to run the application is as follows, otherwise the datasource settings should be adjusted in the application.yml files for dev profile.

1) make sure a kafka instance is already running. If not use following:  
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

3) From the event-service folder, package the application and then create a docker-image:
```bash
maven clean package
docker build . -t event-service:v1
```

4) Once the docker image is built, run the container 
```bash
docker run -d -–rm -p 8085:8085 -–name event-service event-service:v1
```

## Clean up
To clean up all the created containers using following instructions:
```bash
docker rm kafka -f
docker rm event-service-db -f
docker rm event-service -f
docker rmi event-service:v1 -f
```