package com.jessicaarf.springbootapiproducts.dtos;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductDto(@NotBlank(message = "Product name cannot be null")
                         String name,
                         @NotNull(message = "Product value cannot be null")
                         BigDecimal value,
                         @NotNull(message = "Product quantity must be greater than 0")
                         @Min(value = 1)
                         int quantity,
                         @NotBlank
                         String description) {
}
