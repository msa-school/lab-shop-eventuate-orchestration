package labshopeventuate.infra;


import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import labshopeventuate.domain.DecreaseStockCommand;
import labshopeventuate.domain.Inventory;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

import org.springframework.stereotype.Component;

@Component
public class CommandHandler {

//  private CustomerService customerService;

  public CommandHandler() {
    //this.customerService = customerService;
  }

//   public CommandHandlers commandHandlerDefinitions() {
//     return SagaCommandHandlersBuilder
//             .fromChannel("customerService")
//             .onMessage(ReserveCreditCommand.class, this::reserveCredit)
//             .build();
//   }

  public static Message decreaseStock(CommandMessage<DecreaseStockCommand> cm) {
    DecreaseStockCommand cmd = cm.getCommand();
    try {
        Inventory.decreaseStock(cmd);

      return withSuccess("SUCCESS");
    } catch (Exception e) {
      return withFailure(e);
    }
  }

}
