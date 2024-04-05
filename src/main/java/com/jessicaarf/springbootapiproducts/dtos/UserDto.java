package com.jessicaarf.springbootapiproducts.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank String username, @NotBlank String password) {
}
