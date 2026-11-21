package com.ironhack.springbootrestapi.service;

import com.ironhack.springbootrestapi.exception.DuplicateResourceException;
import com.ironhack.springbootrestapi.exception.InvalidPriceRangeException;
import com.ironhack.springbootrestapi.exception.ProductNotFoundException;
import com.ironhack.springbootrestapi.model.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Keeps the product catalogue in an in-memory list. Names and categories are
 * matched ignoring case, so "Laptop" and "laptop" are the same product.
 *
 * <p>The service is a singleton shared by every request thread, so each method
 * that reads or changes the list is {@code synchronized}, and callers only ever
 * receive copies of the list.
 */
@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        addProduct(new Product("Laptop", new BigDecimal("999.99"), "Electronics", 10));
        addProduct(new Product("Headphones", new BigDecimal("59.90"), "Electronics", 25));
        addProduct(new Product("Coffee Mug", new BigDecimal("8.50"), "Kitchen", 40));
        addProduct(new Product("Chef Knife", new BigDecimal("45.00"), "Kitchen", 12));
        addProduct(new Product("Running Shoes", new BigDecimal("120.00"), "Sports", 8));
    }

    /**
     * @throws DuplicateResourceException if a product with that name already exists.
     */
    public synchronized Product addProduct(Product product) {
        if (findByName(product.getName()).isPresent()) {
            throw new DuplicateResourceException(
                    "A product named '%s' already exists.".formatted(product.getName()));
        }
        products.add(product);
        return product;
    }

    public synchronized List<Product> getAllProducts() {
        return List.copyOf(products);
    }

    /**
     * @throws ProductNotFoundException if no product has that name.
     */
    public synchronized Product getProductByName(String name) {
        return findByName(name).orElseThrow(() -> new ProductNotFoundException(name));
    }

    /**
     * Replaces every field of the product called {@code name} with the values in
     * {@code updated}. The product may be renamed, as long as the new name is not
     * already taken by a different product.
     *
     * @throws ProductNotFoundException   if no product has that name.
     * @throws DuplicateResourceException if the new name belongs to another product.
     */
    public synchronized Product updateProduct(String name, Product updated) {
        Product existing = getProductByName(name);
        Optional<Product> sameName = findByName(updated.getName());
        if (sameName.isPresent() && sameName.get() != existing) {
            throw new DuplicateResourceException(
                    "A product named '%s' already exists.".formatted(updated.getName()));
        }
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setQuantity(updated.getQuantity());
        return existing;
    }

    /**
     * @throws ProductNotFoundException if no product has that name.
     */
    public synchronized void deleteProduct(String name) {
        products.remove(getProductByName(name));
    }

    /**
     * @return the products in that category; an empty list when there are none.
     */
    public synchronized List<Product> getProductsByCategory(String category) {
        return products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    /**
     * @return the products whose price is between {@code min} and {@code max},
     *         both inclusive; an empty list when there are none.
     * @throws InvalidPriceRangeException if a bound is negative or {@code min > max}.
     */
    public synchronized List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        if (min.signum() < 0 || max.signum() < 0) {
            throw new InvalidPriceRangeException("Price range bounds must not be negative.");
        }
        if (min.compareTo(max) > 0) {
            throw new InvalidPriceRangeException(
                    "Minimum price (%s) must not be greater than maximum price (%s).".formatted(min, max));
        }
        return products.stream()
                .filter(product -> product.getPrice().compareTo(min) >= 0
                        && product.getPrice().compareTo(max) <= 0)
                .toList();
    }

    private Optional<Product> findByName(String name) {
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
