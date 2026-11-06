package com.freshcart.order_service;

import org.junit.jupiter.api.extension.ExtensionContext;

public class JunitVersionCheck {

    public static void main(String[] args) {
        Class<?> clazz = ExtensionContext.Store.class;

        System.out.println("JUnit loaded from:");
        System.out.println(
            clazz.getProtectionDomain()
                 .getCodeSource()
                 .getLocation()
        );

        System.out.println("JUnit version:");
        System.out.println(
            clazz.getPackage().getImplementationVersion()
        );
    }
}