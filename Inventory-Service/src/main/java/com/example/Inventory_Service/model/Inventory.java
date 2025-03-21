package com.example.Inventory_Service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "t_inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
