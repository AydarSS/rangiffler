#!/bin/bash

echo "### Run java apps###"
cd rangiffler-grpc-common
gradle clean build

cd ..
cd rangiffler-auth
gradle clean build
java -jar -Dspring.profiles.active=local build/libs/rangiffler-auth-1.0.0.jar &
sleep 10
while ! curl -s http://localhost:9000/actuator/health; do sleep 1; done

cd ..
cd rangiffler-gateway
gradle clean build
java -jar -Dspring.profiles.active=local build/libs/rangiffler-gateway-1.0.0.jar &
sleep 10
while ! curl -s http://localhost:8082/actuator/health; do sleep 1; done

cd ..
cd rangiffler-geo
gradle clean build
java -jar -Dspring.profiles.active=local build/libs/rangiffler-geo-1.0.0.jar &
sleep 10
while ! curl -s http://localhost:8085/actuator/health; do sleep 1; done

cd ..
cd rangiffler-photo
gradle clean build
java -jar -Dspring.profiles.active=local build/libs/rangiffler-photo-1.0.0.jar &
sleep 10
while ! curl -s http://localhost:8095/actuator/health; do sleep 1; done

cd ..
cd rangiffler-userdata
gradle clean build
java -jar -Dspring.profiles.active=local build/libs/rangiffler-userdata-1.0.0.jar &
sleep 10
while ! curl -s http://localhost:8089/actuator/health; do sleep 1; done

echo "Готово"




