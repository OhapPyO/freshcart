package com.freshcart.order_service.repository.user;

import com.freshcart.order_service.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

     UserEntity findFirstByIdAndDeletedIsFalse(long userId);
     UserEntity findFirstByEmailAndDeletedIsFalse(String email);
}
