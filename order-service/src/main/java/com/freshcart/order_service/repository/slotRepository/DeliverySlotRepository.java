package com.freshcart.order_service.repository.slotRepository;

import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import com.freshcart.order_service.entity.order.OrderStatus;

@Repository
public interface DeliverySlotRepository extends JpaRepository<DeliverySlotEntity,Long> {

     //For update in create order
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("""
    select ds
    from DeliverySlotEntity ds
    where ds.id = :slotId
      and ds.deleted = false
    """)
     DeliverySlotEntity findSlotForUpdate(@Param("slotId") long slotId);

     DeliverySlotEntity findByIdAndDeletedIsFalse(long slotId);
     List<DeliverySlotEntity> findAllByDeletedIsFalse();

     @Query("""
    select ds
    from DeliverySlotEntity ds
    where ds.deleted = false
      and ds.startTime > :now
      and ds.endTime > ds.startTime
      and (
          select count(o)
          from OrderEntity o
          where o.slot = ds
            and o.deleted = false
            and o.orderStatus <> :cancelled
      ) < ds.maxOrder
    order by ds.startTime
    """)
     List<DeliverySlotEntity> findAvailableDeliverySlots(
             @Param("now") LocalDateTime now,
             @Param("cancelled") OrderStatus cancelled
     );

}
