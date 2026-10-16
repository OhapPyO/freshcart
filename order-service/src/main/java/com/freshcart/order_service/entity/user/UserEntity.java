package com.freshcart.order_service.entity.user;

import com.freshcart.order_service.entity.BaseEntity;
import com.freshcart.order_service.entity.order.OrderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    // it is for now a string but will change to enum after
    private String role;


    @OneToMany(mappedBy = "user")
    private List<OrderEntity> userOrderEntities;

}
