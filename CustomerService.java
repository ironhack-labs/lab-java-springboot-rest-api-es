package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();

    // Agregar cliente
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    // Obtener todos clientes
    public List<Customer> getAllCustomers() {
        return customers;
    }

    // Obtener cliente por email
    public Customer getCustomerByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Cliente no encontrado: " + email));
    }

    // Actualizar cliente
    public void updateCustomer(String email, Customer newCustomer) {
        Customer existing = getCustomerByEmail(email);
        existing.setName(newCustomer.getName());
        existing.setEmail(newCustomer.getEmail());
        existing.setAge(newCustomer.getAge());
        existing.setAddress(newCustomer.getAddress());
    }

    // Eliminar cliente
    public void deleteCustomer(String email) {
        Customer customer = getCustomerByEmail(email);
        customers.remove(customer);
    }
}
