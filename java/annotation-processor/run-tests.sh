#!/usr/bin/env bash
set -e

./gradlew akimbo-processor:clean akimbo-processor:test tests:akimbo-test-app:clean tests:akimbo-test-app:docker

docker kill akimbo-test-app > /dev/null 2>&1
docker rm akimbo-test-app > /dev/null 2>&1
docker run -dit -p 8070:8070 -e AKIMBO_HTTP_PORT=8070 -e AKIMBO_CASSANDRA_HOST=cassandra --network eighty4 --name akimbo-test-app akimbo/akimbo-test-app > /dev/null 2>&1

sleep 3

./gradlew tests:akimbo-test-suite:test

#docker kill akimbo-test-app > /dev/null 2>&1
#docker rm akimbo-test-app > /dev/null 2>&1
