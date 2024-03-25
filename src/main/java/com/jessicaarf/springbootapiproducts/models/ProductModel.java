package com.jessicaarf.springbootapiproducts.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_PRODUCTS")
@Getter
@Setter
@EqualsAndHashCode
public class ProductModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private UUID idProduct;
    private String name;
    private BigDecimal value;
    private int quantity;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String imageUrl;

}
