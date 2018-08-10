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
