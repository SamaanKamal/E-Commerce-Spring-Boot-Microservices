package com.example.Order_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderLineItemsDto {
    private Long id;
    private String skuCode;

    private BigDecimal price;

    private Integer quantity;
}
