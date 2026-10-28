package com.freshcart.order_service.service.login;

import com.freshcart.order_service.data.request.LoginRequest;
import com.freshcart.order_service.data.request.RegisterRequest;
import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.exception.CustomException;
import com.freshcart.order_service.exception.ResourceNotFoundException;
import com.freshcart.order_service.repository.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService  {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
   @Autowired
   AuthenticationManager authenticationManager;


    public void register(@Valid RegisterRequest req) {
        validationMethod(req);
        UserEntity user = new UserEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("CUSTOMER");
        userRepository.save(user);
    }

    private void validationMethod(RegisterRequest req) {

        if (req.getFirstName() == null || req.getLastName() == null) {
            throw new ResourceNotFoundException("First name or last name is null");
        }
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            throw new ResourceNotFoundException("Email is empty");
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new ResourceNotFoundException("Password is empty");
        }
        UserEntity duplicateEmailExist = userRepository.findFirstByEmailAndDeletedIsFalse(req.getEmail());
        if (duplicateEmailExist != null) {
            throw new DataIntegrityViolationException("the email already exists");
        }
    }

    public void login(@Valid LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new CustomException("email is not valid");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new CustomException("password is not valid");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication auth = authenticationManager.authenticate(authentication);


    }


}
