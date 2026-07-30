@echo off
echo Starting all microservices in the correct dependency order...

REM Zipkin starts first — services send trace data to it once they are up
REM --rm: auto-remove the container when stopped (clean teardown)
REM -d: run in background so this script continues immediately
REM -p 9411:9411: expose the Zipkin UI and API on localhost:9411
docker rm -f zipkin 2>nul
start "Zipkin UI" cmd /k "docker run --rm --name zipkin -d -p 9411:9411 openzipkin/zipkin && echo Zipkin started at http://localhost:9411 && pause"

echo Waiting 5 seconds for Zipkin to start up...
timeout /t 5

REM Config Server starts second — all services fetch their config from it
start "Config Server (Port 8888)" cmd /k "mvnw.cmd -e -pl config-service spring-boot:run"

echo Waiting 8 seconds for Config Server to start up...
timeout /t 8

REM Eureka starts third — services register with it after fetching their config
start "Eureka Server (Port 8761)" cmd /k "mvnw.cmd -e -pl eureka-server spring-boot:run"

echo Waiting 8 seconds for Eureka Server to start up...
timeout /t 8

start "Code Tools Service (Port 8082)" cmd /k "mvnw.cmd -e -pl code-tools-service spring-boot:run"

echo Waiting 8 seconds for Code Tools Service to start up before starting AI Review Service...
timeout /t 8

start "AI Review Service (Port 8081)" cmd /k "mvnw.cmd -e -pl ai-review-service spring-boot:run"
start "Gateway Service (Port 8080)" cmd /k "mvnw.cmd -e -pl gateway-service spring-boot:run"

echo All services launched! Zipkin UI available at: http://localhost:9411
