#!/bin/bash
source ../docker.properties
echo '### Build dev frontend GQL image ###'
docker build --no-cache --build-arg NPM_COMMAND=${NPM_DOCKER_BUILD} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME_GQL}-"$1":${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME_GQL}-"$1":latest .


