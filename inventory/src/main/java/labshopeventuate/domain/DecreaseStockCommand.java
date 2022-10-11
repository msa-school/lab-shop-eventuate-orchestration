package labshopeventuate.domain;

import io.eventuate.tram.commands.common.Command;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecreaseStockCommand implements Command {

    Long orderId;
    Long productId;
    int qty;

}
