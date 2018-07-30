# takeaway
Takeaway.com Employee &amp; Event Service Coding Challenge

## Database requirement:

docker run -d --rm -p 8432:5432 --name employee-service-db -e POSTGRES_USER=employeeservice -e POSTGRES_PASSWORD=employeeservice postgres:alpine -d employeeservice
docker rm -f employee-service-db


docker run -d -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 --name kafka -p 2181:2181 -p 9092:9092  -p 8000:8000 spotify/kafka


