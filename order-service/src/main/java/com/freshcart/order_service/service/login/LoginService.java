package com.freshcart.order_service.service.login;

import com.freshcart.order_service.data.request.LoginRequest;
import com.freshcart.order_service.data.request.RegisterRequest;
import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.exception.CustomException;
import com.freshcart.order_service.exception.ResourceNotFoundException;
import com.freshcart.order_service.repository.user.UserRepository;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LoginService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtEncoder jwtEncoder;


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

    public String login(@Valid LoginRequest request) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication auth = authenticationManager.authenticate(authentication);
        List<@Nullable String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(auth.getName()).issuedAt(
                Instant.now()
        ).expiresAt(Instant.now().plusSeconds(2000L)).claim("roles",roles).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();


    }


}
