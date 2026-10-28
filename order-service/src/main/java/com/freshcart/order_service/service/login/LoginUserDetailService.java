package com.freshcart.order_service.service.login;

import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findFirstByEmailAndDeletedIsFalse(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.getRole().isEmpty()) {
            throw new UsernameNotFoundException("User donot  have role");
        }
        return User.builder().username(email).password(user.getPassword()).roles(user.getRole()).build();
    }

}
