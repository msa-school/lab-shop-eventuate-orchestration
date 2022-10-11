package labshopeventuate.domain;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import org.springframework.stereotype.Component;

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
