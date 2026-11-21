package com.ironhack.springbootrestapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * A product in the in-memory catalogue. The name is the product's identifier,
 * so it is unique (ignoring case) across the catalogue.
 *
 * <p>The price is a {@link BigDecimal} because it is money: a {@code double}
 * cannot hold values such as 0.10 exactly.
 */
public class Product {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, message = "Name must be at least 3 characters long.")
    private String name;

    @NotNull(message = "Price is required.")
    @Positive(message = "Price must be a positive number.")
    private BigDecimal price;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be a positive number.")
    private Integer quantity;

    public Product() {
    }

    public Product(String name, BigDecimal price, String category, Integer quantity) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
