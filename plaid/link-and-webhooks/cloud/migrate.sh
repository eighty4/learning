#/bin/sh

SRC_DIR="$(pwd)/$(dirname "$0")"

/usr/local/bin/docker run --rm --network platform_default \
  -e FLYWAY_HOST=eighty4-postgres \
  -v $SRC_DIR/flyway.conf:/flyway/conf/flyway.conf \
  -v $SRC_DIR/sql:/flyway/sql:ro \
  flyway/flyway:latest-alpine migrate
