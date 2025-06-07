package com.example.demo.controller;

import com.example.demo.exception.ApiKeyMissingException;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private static final String API_KEY_HEADER = "API-Key";
    private static final String VALID_API_KEY = "123456";

    // Inyección por constructor
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Validar API-Key header en todas las solicitudes
    private void validateApiKey(String apiKey) {
        if(apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            throw new ApiKeyMissingException("Clave API inválida o ausente");
        }
    }

    // Crear cliente
    @PostMapping
    public ResponseEntity<String> addCustomer(@RequestHeader(API_KEY_HEADER) String apiKey,
                                              @Valid @RequestBody Customer customer) {
        validateApiKey(apiKey);
        customerService.addCustomer(customer);
        return new ResponseEntity<>("Cliente creado exitosamente", HttpStatus.CREATED);
    }

    // Obtener todos clientes
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Obtener cliente por email
    @GetMapping("/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                       @PathVariable String email) {
        validateApiKey(apiKey);
        Customer customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }

    // Actualizar cliente
    @PutMapping("/{email}")
    public ResponseEntity<String> updateCustomer(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                 @PathVariable String email,
                                                 @Valid @RequestBody Customer customer) {
        validateApiKey(apiKey);
        customerService.updateCustomer(email, customer);
        return ResponseEntity.ok("Cliente actualizado exitosamente");
    }

    // Eliminar cliente
    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteCustomer(@RequestHeader(API_KEY_HEADER) String apiKey,
                                                 @PathVariable String email) {
        validateApiKey(apiKey);
        customerService.deleteCustomer(email);
        return ResponseEntity.ok("Cliente eliminado exitosamente");
    }
}
