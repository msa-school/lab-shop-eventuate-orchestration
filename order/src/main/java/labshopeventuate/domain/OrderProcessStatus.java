package labshopeventuate.domain;

import lombok.Data;

@Data
public class OrderProcessStatus {

    Long orderId;
    String rejectReason;
    int qty;
    Long productId;

}
