package com.jessicaarf.springbootapiproducts.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TB_PRODUCTS")
@Getter
@Setter
@EqualsAndHashCode(of = "idProduct")
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
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

    @CreationTimestamp
    private Instant creationTimestamp;

    @UpdateTimestamp
    private Instant updatedTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userModel;

}
