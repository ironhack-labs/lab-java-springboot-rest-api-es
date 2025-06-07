package com.example.demo.controller;

import com.example.demo.exception.ApiKeyMissingException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private static final String API_KEY_HEADER = "API-Key";
    private static final String VALID_API_KEY = "123456";

    // Inyección por constructor
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Validar API-Key header en todas las solicitudes
    private void validateApiKey(String apiKey) {
        if(apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            throw new ApiKeyMissingException("Clave API inválida o ausente");
        }
    }

    // Crear producto
    @PostMapping
    public ResponseEntity<String> addProduct(@RequestHeader(API_KEY_HEADER) String apiKey,
                                             @Valid @RequestBody Product product) {
        validateApiKey(apiKey);
        productService.addProduct(product);
        return new ResponseEntity<>("Producto creado exitosamente", HttpStatus.CREATED);
    }

    // Obtener todos productos
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Obtener producto por nombre
    @GetMapping("/{name}")
    public ResponseEntity<Product> getProductByName(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                    @PathVariable String name) {
        validateApiKey(apiKey);
        Product product = productService.getProductByName(name);
        return ResponseEntity.ok(product);
    }

    // Actualizar producto por nombre
    @PutMapping("/{name}")
    public ResponseEntity<String> updateProduct(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                @PathVariable String name,
                                                @Valid @RequestBody Product product) {
        validateApiKey(apiKey);
        productService.updateProduct(name, product);
        return ResponseEntity.ok("Producto actualizado exitosamente");
    }

    // Eliminar producto por nombre
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteProduct(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                @PathVariable String name) {
        validateApiKey(apiKey);
        productService.deleteProduct(name);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }

    // Obtener productos por categoría
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                               @PathVariable String category) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // Obtener productos por rango de precios
    @GetMapping("/price")
    public ResponseEntity<List<Product>> getProductsByPriceRange(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                                 @RequestParam double min,
                                                                 @RequestParam double max) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(productService.getProductsByPriceRange(min, max));
    }
}
