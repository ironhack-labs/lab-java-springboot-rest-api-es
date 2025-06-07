package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    // Agregar producto
    public void addProduct(Product product) {
        products.add(product);
    }

    // Obtener todos
    public List<Product> getAllProducts() {
        return products;
    }

    // Buscar por nombre
    public Product getProductByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + name));
    }

    // Actualizar producto
    public void updateProduct(String name, Product newProduct) {
        Product existing = getProductByName(name);
        existing.setName(newProduct.getName());
        existing.setPrice(newProduct.getPrice());
        existing.setCategory(newProduct.getCategory());
        existing.setQuantity(newProduct.getQuantity());
    }

    // Eliminar producto
    public void deleteProduct(String name) {
        Product product = getProductByName(name);
        products.remove(product);
    }

    // Productos por categoría
    public List<Product> getProductsByCategory(String category) {
        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // Productos por rango de precio
    public List<Product> getProductsByPriceRange(double min, double max) {
        if(min > max) throw new IllegalArgumentException("El rango mínimo no puede ser mayor que el máximo");
        return products.stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .collect(Collectors.toList());
    }
}
