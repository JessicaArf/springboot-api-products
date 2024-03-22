package com.jessicaarf.springbootapiproducts.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;

public record ProductDto(@NotBlank String name, @NotNull BigDecimal value, @NotNull int quantity, @NotBlank String description) {
}
