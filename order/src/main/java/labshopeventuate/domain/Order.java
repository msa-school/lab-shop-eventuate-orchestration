package labshopeventuate.domain;

import labshopeventuate.domain.OrderPlaced;
import labshopeventuate.domain.OrderCancelled;
import labshopeventuate.OrderApplication;
import javax.persistence.*;

import io.eventuate.tram.events.publisher.DomainEventPublisher;

import java.util.List;
import lombok.Data;

import java.util.Collections;
import java.util.Date;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;


@Entity
@Table(name="Order_table")
@Data

public class Order  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String productId;
    
    private Integer qty;
    
    private String customerId;
    
    private Double amount;
    
    private String status;
    
    private String address;

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

    @PrePersist
    public void onPrePersist(){
        // Get request from Inventory
        //labshopeventuate.external.Inventory inventory =
        //    Application.applicationContext.getBean(labshopeventuate.external.InventoryService.class)
        //    .getInventory(/** mapping value needed */);

    }
    @PreRemove
    public void onPreRemove(){

        OrderCancelled orderCancelled = new OrderCancelled(this);

    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }






}
