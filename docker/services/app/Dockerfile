FROM openjdk:11

ENV CASSANDRA_HOST="cassandra"

ENV CASSANDRA_PORT="9042"

ENV KEYSPACE="my_keyspace"

RUN apt-get update && \
    apt-get install -y ncat

RUN apt-get update && \
    apt-get install -y python3-pip && \
    pip3 install cqlsh

COPY wait-for-it.sh /wait-for-it.sh

COPY create-keyspace.cql /create-keyspace.cql

RUN chmod +x /wait-for-it.sh

COPY BankMicroservice-0.0.1-SNAPSHOT.jar backend.jar

CMD ["./wait-for-it.sh", "java", "-jar", "backend.jar"]