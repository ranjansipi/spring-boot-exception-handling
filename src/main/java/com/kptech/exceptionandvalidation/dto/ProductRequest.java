package com.kptech.exceptionandvalidation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name must not be blank") String name,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be a positive value") BigDecimal price,
        @Size(max = 50, message = "Description must not exceed 50 characters") String category
) {}

