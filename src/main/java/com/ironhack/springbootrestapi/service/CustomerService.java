package com.ironhack.springbootrestapi.service;

import com.ironhack.springbootrestapi.exception.CustomerNotFoundException;
import com.ironhack.springbootrestapi.exception.DuplicateResourceException;
import com.ironhack.springbootrestapi.model.Customer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Keeps customers in an in-memory list. Emails are matched ignoring case,
 * because email addresses are case-insensitive in practice.
 *
 * <p>As in {@link ProductService}, every method is {@code synchronized} because
 * the singleton is shared by all request threads.
 */
@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();

    public CustomerService() {
        addCustomer(new Customer("Ana Garcia", "ana.garcia@example.com", 34, "Calle Mayor 1, Madrid"));
        addCustomer(new Customer("Luis Perez", "luis.perez@example.com", 27, "Passeig de Gracia 10, Barcelona"));
        addCustomer(new Customer("Marta Ruiz", "marta.ruiz@example.com", 45, "Avenida de la Constitucion 5, Sevilla"));
    }

    /**
     * @throws DuplicateResourceException if a customer with that email already exists.
     */
    public synchronized Customer addCustomer(Customer customer) {
        if (findByEmail(customer.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                    "A customer with email '%s' already exists.".formatted(customer.getEmail()));
        }
        customers.add(customer);
        return customer;
    }

    public synchronized List<Customer> getAllCustomers() {
        return List.copyOf(customers);
    }

    /**
     * @throws CustomerNotFoundException if no customer has that email.
     */
    public synchronized Customer getCustomerByEmail(String email) {
        return findByEmail(email).orElseThrow(() -> new CustomerNotFoundException(email));
    }

    /**
     * Replaces every field of the customer with {@code email} by the values in
     * {@code updated}. The email may change, as long as the new one is not
     * already used by a different customer.
     *
     * @throws CustomerNotFoundException  if no customer has that email.
     * @throws DuplicateResourceException if the new email belongs to another customer.
     */
    public synchronized Customer updateCustomer(String email, Customer updated) {
        Customer existing = getCustomerByEmail(email);
        Optional<Customer> sameEmail = findByEmail(updated.getEmail());
        if (sameEmail.isPresent() && sameEmail.get() != existing) {
            throw new DuplicateResourceException(
                    "A customer with email '%s' already exists.".formatted(updated.getEmail()));
        }
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setAge(updated.getAge());
        existing.setAddress(updated.getAddress());
        return existing;
    }

    /**
     * @throws CustomerNotFoundException if no customer has that email.
     */
    public synchronized void deleteCustomer(String email) {
        customers.remove(getCustomerByEmail(email));
    }

    private Optional<Customer> findByEmail(String email) {
        return customers.stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
