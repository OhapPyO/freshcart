package com.freshcart.order_service.controller;


import com.freshcart.order_service.TestcontainersConfigurationIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfigurationIT.class)
@AutoConfigureMockMvc
public class ProductApiTest {
    @Autowired
    MockMvc mvc;

    @Test
    void saveProduct_userIsNotLoginAndGet401() throws Exception {
        mvc.perform(post("/product"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveProduct_userIsLoginAndIsCustomerAndGet403() throws Exception {

        mvc.perform(post("/product")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveProduct_userIsLoginAndIsAdminAndGet201() throws Exception {

        mvc.perform(post("/product")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(
                                MediaType.APPLICATION_JSON)
                                .content("""
                                        { "name": "goodapple",   "stock": 2,  "price":2.3  }
                                        """)
                )
                .andExpect(status().isCreated());
    }

}
