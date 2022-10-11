package labshopeventuate.domain;

import labshopeventuate.InventoryApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Inventory_table")
@Data

public class Inventory  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private Long stock;

    @PostPersist
    public void onPostPersist(){
    }

    public static InventoryRepository repository(){
        InventoryRepository inventoryRepository = InventoryApplication.applicationContext.getBean(InventoryRepository.class);
        return inventoryRepository;
    }




    public static void decreaseStock(DecreaseStockCommand decreaseStockCommand){

        /** Example 1:  new item 
        Inventory inventory = new Inventory();
        repository().save(inventory);

        */

        /** Example 2:  finding and process */
        
        repository().findById(Long.valueOf(decreaseStockCommand.getProductId())).ifPresent(inventory->{
            
            if(inventory.getStock() < decreaseStockCommand.getQty()){
                throw new OutOfStockException();
            }

            inventory.setStock(inventory.getStock() - decreaseStockCommand.getQty()); // do something
            repository().save(inventory);


         });
       

        
    }
    public static void increaseStock(OrderCancelled orderCancelled){

        /** Example 1:  new item 
        Inventory inventory = new Inventory();
        repository().save(inventory);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderCancelled.get???()).ifPresent(inventory->{
            
            inventory // do something
            repository().save(inventory);


         });
        */

        
    }


}
