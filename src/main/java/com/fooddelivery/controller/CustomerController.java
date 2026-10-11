

package com.fooddelivery.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fooddelivery.entity.Customer;
import com.fooddelivery.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Add a new customer
    @PostMapping
    public Customer addCustomer(@Valid @RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    // Get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by ID
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found: " + id));
    }

    // Update an existing customer
    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody Customer updatedCustomer) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found: " + id));

        customer.setName(updatedCustomer.getName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPhone(updatedCustomer.getPhone());
        customer.setAddress(updatedCustomer.getAddress());

        return customerRepository.save(customer);
    }

    // Delete a customer
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found: " + id);
        }

        customerRepository.deleteById(id);
        return "Customer deleted successfully: " + id;
    }
}
