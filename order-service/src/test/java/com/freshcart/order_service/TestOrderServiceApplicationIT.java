package com.freshcart.order_service;

import org.springframework.boot.SpringApplication;

public class TestOrderServiceApplicationIT {

	public static void main(String[] args) {
		SpringApplication.from(OrderServiceApplication::main).with(TestcontainersConfigurationIT.class).run(args);
	}

}
