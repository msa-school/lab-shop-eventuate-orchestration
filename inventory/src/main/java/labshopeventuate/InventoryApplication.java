package labshopeventuate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import labshopeventuate.domain.DecreaseStockCommand;
import labshopeventuate.domain.OrderPlaced;
import labshopeventuate.infra.CommandHandler;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;

import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class InventoryApplication {
    public static ApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(InventoryApplication.class, args);
    }

    // @Bean
    // public DomainEventDispatcher domainEventDispatcher(DomainEventDispatcherFactory domainEventDispatcherFactory) {
    //   return domainEventDispatcherFactory.make("orderServiceEvents", DomainEventHandlersBuilder
    //   .forAggregateType("labshopeventuate.domain.Order")
    //   .onEvent(OrderPlaced.class, PolicyHandler::wheneverOrderPlaced_DecreaseStock)
    //   //.onEvent(OrderCancelledEvent.class, this::handleOrderCancelledEvent)
    //   .build());
    // }

    @Bean
    public CommandDispatcher commandDispatcher(CommandHandler commandHanlder,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
  
      return sagaCommandDispatcherFactory.make("customerCommandDispatcher", SagaCommandHandlersBuilder
                  .fromChannel("inventory")
                  .onMessage(DecreaseStockCommand.class, CommandHandler::decreaseStock)
                  .build()
                );
    }
  
  
}