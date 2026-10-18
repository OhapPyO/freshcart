package com.freshcart.order_service.data.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdName {
    private long id;
    private String name;

    public IdName() {
    }

    public IdName(long id, String name) {
        this.id = id;
        this.name = name;
    }


}
