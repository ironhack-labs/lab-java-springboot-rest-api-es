package com.ironhack.springbootrestapi.controller;

import com.ironhack.springbootrestapi.exception.InvalidApiKeyException;
import com.ironhack.springbootrestapi.model.Product;
import com.ironhack.springbootrestapi.service.ProductService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST endpoints for the product catalogue under {@code /products}.
 *
 * <p>Every request must carry an {@code API-Key} header with the value set in
 * {@code products.api-key}. See {@link #requireApiKey(String)} for how that is
 * enforced once for the whole controller.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    static final String API_KEY_HEADER = "API-Key";

    private final ProductService productService;
    private final byte[] expectedApiKey;

    public ProductController(ProductService productService, @Value("${products.api-key}") String expectedApiKey) {
        this.productService = productService;
        this.expectedApiKey = expectedApiKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Spring runs {@code @ModelAttribute} methods before every handler method of
     * this controller, and before the handler's own arguments (such as a
     * {@code @Valid} body) are read. So this single method guards all endpoints,
     * and a request without a valid key is rejected before its body is validated.
     *
     * <p>A missing header makes Spring throw {@code MissingRequestHeaderException};
     * a wrong value throws {@link InvalidApiKeyException}. Both become 401 in the
     * global exception handler. The comparison is constant-time so response
     * timing does not reveal how much of a guessed key was right.
     */
    @ModelAttribute
    public void requireApiKey(@RequestHeader(API_KEY_HEADER) String apiKey) {
        if (!MessageDigest.isEqual(expectedApiKey, apiKey.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidApiKeyException();
        }
    }

    /** 201 Created with the new product and its URL in the Location header. */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product created = productService.addProduct(product);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{name}")
                .buildAndExpand(created.getName())
                .encode()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{name}")
    public Product getProductByName(@PathVariable String name) {
        return productService.getProductByName(name);
    }

    @PutMapping("/{name}")
    public Product updateProduct(@PathVariable String name, @Valid @RequestBody Product product) {
        return productService.updateProduct(name, product);
    }

    /** 204 No Content once the product is removed. */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String name) {
        productService.deleteProduct(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    /**
     * {@code /products/price?min=10&max=100}. A literal segment outranks the
     * {@code /{name}} pattern in Spring's matching, so this is not mistaken for
     * a product called "price".
     */
    @GetMapping("/price")
    public List<Product> getProductsByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return productService.getProductsByPriceRange(min, max);
    }
}
