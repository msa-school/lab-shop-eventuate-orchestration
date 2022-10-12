# Microservice Orchestration with Eventuate + Spring Data REST

## How to run

- firstly install and run infrastructure including the kafka, mysql, eventuate cdc
```
cd kafka
docker-compose up
```

- run each microservice
```
cd order
mvn spring-boot:run

#another terminal
cd inventory
mvn spring-boot:run
```

- test the service:

register a product with stock 10 and create an order for the product:
```
http :8082/inventories id=1 stock=10
http :8081/orders productId=1 qty=6
```

check the order status:
```
http :8081/orders    # status must be 'APPROVED'
```

check the stock remaining:
```
http :8082/inventories/1    #stock must be 4
```

create and order again:
```
http :8081/orders productId=1 qty=6
```

check the order status:
```
http :8081/orders    # status must be 'REJECTED' since there's no enough stock.
```

- check the database:

```
docker exec -it kafka-mysql-1 /bin/bash

mysql --user=root --password=$MYSQL_ROOT_PASSWORD
use eventuate;
select * from message;
```


you can find the kafka log:

```
docker exec -it kafka-kafka-1 /bin/bash
cd /bin

kafka-topics --bootstrap-server=localhost:9092 --list
kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Order --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Order --from-beginning
```


- When I face to such a compilation problem, to get the right (working) version of dependencies, I could use this command from working example of official eventuate site:

```
./gradlew :customer-service:dependencies | grep io.eventuate.tram.sagas
Picked up JAVA_TOOL_OPTIONS:  -Xmx3489m
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-bom:0.20.0.RELEASE
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE (c)
|    |    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (c)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-quarkus-bom:0.5.1.RELEASE
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter -> 0.20.0.RELEASE
|    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE
|         +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE
|         |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE
|         \--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE
|              \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (*)
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter (n)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-bom:0.20.0.RELEASE
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE (c)
|    |    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (c)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-quarkus-bom:0.5.1.RELEASE
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter -> 0.20.0.RELEASE
|    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE
|         +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE
|         |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE
|         \--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE
|              \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (*)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-bom:0.20.0.RELEASE
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE (c)
|    |    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (c)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-quarkus-bom:0.5.1.RELEASE
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter -> 0.20.0.RELEASE
|    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE
|         +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE
|         |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE
|         \--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE
|              \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (*)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-bom:0.20.0.RELEASE
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE (c)
|    |    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (c)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-quarkus-bom:0.5.1.RELEASE
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter -> 0.20.0.RELEASE
|    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE
|         +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE
|         |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE
|         \--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE
|              \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (*)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-bom:0.20.0.RELEASE
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE (c)
|    |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE (c)
|    |    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (c)
|    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-quarkus-bom:0.5.1.RELEASE
+--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant-starter -> 0.20.0.RELEASE
|    \--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-participant:0.20.0.RELEASE
|         +--- io.eventuate.tram.sagas:eventuate-tram-sagas-spring-common:0.20.0.RELEASE
|         |    +--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE
|         \--- io.eventuate.tram.sagas:eventuate-tram-sagas-participant:0.20.0.RELEASE
|              \--- io.eventuate.tram.sagas:eventuate-tram-sagas-common:0.20.0.RELEASE (*)
```




## Frequent Errors

If the oder service complain to there is no tables, you may create by yourself with following DDL:

https://github.com/eventuate-tram/eventuate-tram-sagas/blob/master/mssql/5.tram-saga-schema.sql


## Choreography version
https://github.com/jinyoung/lab-shop-eventuate
