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
```

## Implementation Details


- "Order::onPostPersist" starts a saga with SagaInstanceFactory that is provided by Eventuate:
```
    @PostPersist
    public void onPostPersist(){
        SagaInstanceFactory sagaFactory = OrderApplication.applicationContext.getBean(SagaInstanceFactory.class);
        OrderProcess orderProcess = OrderApplication.applicationContext.getBean(OrderProcess.class);
        OrderProcessStatus processStatus = new OrderProcessStatus();
        
        processStatus.setOrderId(getId());
        processStatus.setQty(getQty());
        processStatus.setProductId(Long.valueOf(getProductId()));

        sagaFactory.create(orderProcess, processStatus);
    }
```

- "OrderProcess" defines the saga process definition while the "OrderProcessStatus" holds the status of saga instance:
```
@Component
public class OrderProcess implements SimpleSaga<OrderProcessStatus> {

  //private OrderService orderService;

  OrderRepository repository;

  public OrderProcess(OrderRepository repository) {
    this.repository = repository;
  }

  private SagaDefinition<OrderProcessStatus> sagaDefinition =
          step()
            .invokeLocal(this::create)
            .withCompensation(this::reject)
          .step()
            .invokeParticipant(this::decreaseStock)
            .onReply(OutOfStockException.class, this::handleOutOfStock)
          .step()
            .invokeLocal(this::approve)
          .build();

  private void handleOutOfStock(OrderProcessStatus status, OutOfStockException reply) {
    status.setRejectReason(OutOfStockException.class.getSimpleName());
  }

  @Override
  public SagaDefinition<OrderProcessStatus> getSagaDefinition() {
    return this.sagaDefinition;
  }

  private void create(OrderProcessStatus status) {
    // Order order = new Order();
    // repository.save(order);
  }

  private CommandWithDestination decreaseStock(OrderProcessStatus status) {
    return send(new DecreaseStockCommand(status.getOrderId(), status.getProductId(), status.getQty()))
            .to("inventory")
            .build();
  }

  private void reject(OrderProcessStatus status) {
    repository.findById(status.getOrderId()).ifPresent(order -> {
        order.setStatus("REJECTED");
    });
  }

  private void approve(OrderProcessStatus status) {
    repository.findById(status.getOrderId()).ifPresent(order -> {
        order.setStatus("APPROVED");
    });
  }
}

```

- As stated in "application.yaml", Eventuate Tram uses the configuration to connect to the database and for sending message and the Eventuate CDC pick up the message from the db log and send events to the kafka:
```
spring:
  profiles: default
  jpa:
    generate-ddl: true
    properties:
      hibernate:
        show_sql: true
        format_sql: true

  datasource:
    url: jdbc:mysql://${DOCKER_HOST_IP:localhost}/eventuate
    username: mysqluser
    password: mysqlpw
    driver-class-name: com.mysql.cj.jdbc.Driver


eventuatelocal:
  kafka:
    bootstrap.servers: ${DOCKER_HOST_IP:localhost}:9092


cdc:
  service:
    url: http://localhost:8099

```



- In inventory service, the commands are subscribed by the CommandHandler:
```
    # InventoryApplication.java

    @Bean
    public CommandDispatcher commandDispatcher(CommandHandler commandHanlder,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
  
      return sagaCommandDispatcherFactory.make("customerCommandDispatcher", SagaCommandHandlersBuilder
                  .fromChannel("inventory")
                  .onMessage(DecreaseStockCommand.class, CommandHandler::decreaseStock)
                  .build()
                );
    }
```

- CommandHanlder tries to call decreaseStock and if there's an exception, will return the error object and this will be published to the 'Reply' kafka topic:
```
  public static Message decreaseStock(CommandMessage<DecreaseStockCommand> cm) {
    DecreaseStockCommand cmd = cm.getCommand();
    try {
        Inventory.decreaseStock(cmd);

      return withSuccess("SUCCESS");
    } catch (Exception e) {
      return withFailure(e);
    }
  }
```

- In the aggagate, the 'decreaseStock' method throws the OutOfStockException when the stock is insufficient
```

    public static void decreaseStock(DecreaseStockCommand decreaseStockCommand){
        
        repository().findById(Long.valueOf(decreaseStockCommand.getProductId())).ifPresent(inventory->{
            
            if(inventory.getStock() < decreaseStockCommand.getQty()){
                throw new OutOfStockException();
            }

            inventory.setStock(inventory.getStock() - decreaseStockCommand.getQty());
            repository().save(inventory);

         });
        
    }

    
```


## Frequent Errors

If the oder service complain to there is no tables, you may create by yourself with following DDL:

https://github.com/eventuate-tram/eventuate-tram-sagas/blob/master/mssql/5.tram-saga-schema.sql


## Choreography version
- https://github.com/jinyoung/lab-shop-eventuate


## References
- https://eventuate.io/abouteventuatetram.html