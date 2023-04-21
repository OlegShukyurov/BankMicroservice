#!/bin/bash

set -e

cmd="$@"

until ncat -z -v -w30 "$CASSANDRA_HOST" "$CASSANDRA_PORT"; do
  echo "Waiting to connect '${CASSANDRA_HOST}:${CASSANDRA_PORT}' ..."
  sleep 1
done

>&2 echo "'${CASSANDRA_HOST}:${CASSANDRA_PORT}' is available"

echo "Creating keyspace '${KEYSPACE}' ..."

cqlsh "$CASSANDRA_HOST" "$CASSANDRA_PORT" -f /create-keyspace.cql

echo "Keyspace '${KEYSPACE}' created successfully"

exec $cmd