package com.trantorinc.kafkaquickstart.models;

import lombok.Data;

@Data
public class Order {
    private String orderNumber;
    private int totalPrice;
    private String orderType;
}
