

package com.fooddelivery.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fooddelivery.entity.Customer;
import com.fooddelivery.entity.Food;
import com.fooddelivery.entity.FoodOrder;
import com.fooddelivery.repository.CustomerRepository;
import com.fooddelivery.repository.FoodOrderRepository;
import com.fooddelivery.repository.FoodRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class FoodOrderController {

    private final FoodOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final FoodRepository foodRepository;

    public FoodOrderController(
            FoodOrderRepository orderRepository,
            CustomerRepository customerRepository,
            FoodRepository foodRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.foodRepository = foodRepository;
    }

    // 1. Place a new order
    @PostMapping
    public FoodOrder placeOrder(@RequestBody FoodOrder order) {

        if (order.getCustomerId() == null
                || order.getCustomerId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID must be a positive number");
        }

        if (order.getFoodId() == null
                || order.getFoodId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Food ID must be a positive number");
        }

        validateQuantity(order.getQuantity());

        customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"));

        Food food = foodRepository.findById(order.getFoodId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Food item not found"));

        if (!food.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Food item is not available");
        }

        if (food.getPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Food price must be greater than zero");
        }

        double total = food.getPrice() * order.getQuantity();

        // Calculate order details on the server
        order.setTotalAmount(total);
        order.setStatus("PLACED");

        return orderRepository.save(order);
    }

    // 2. Get all orders
    @GetMapping
    public List<FoodOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    // 3. Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    public List<FoodOrder> getOrdersByCustomer(
            @PathVariable("customerId") Long customerId) {

        if (customerId == null || customerId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID must be a positive number");
        }

        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found");
        }

        return orderRepository.findByCustomerId(customerId);
    }

    // 4. Get an order by ID
    @GetMapping("/{id}")
    public FoodOrder getOrderById(@PathVariable("id") Long id) {

        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID must be positive");
        }

        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"));
    }

    // 5. Update order status
    @PutMapping("/{id}/status")
    public FoodOrder updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID must be positive");
        }

        FoodOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"));

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order status is required");
        }

        String currentStatus = order.getStatus();

        if (currentStatus == null || currentStatus.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current order status is missing");
        }

        currentStatus = currentStatus.trim().toUpperCase();
        String newStatus = status.trim().toUpperCase();

        List<String> validStatuses = List.of(
                "PLACED",
                "CONFIRMED",
                "PREPARING",
                "OUT_FOR_DELIVERY",
                "DELIVERED",
                "CANCELLED");

        if (!validStatuses.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order status");
        }

        boolean allowed = switch (currentStatus) {
            case "PLACED" ->
                    List.of("CONFIRMED", "CANCELLED")
                            .contains(newStatus);

            case "CONFIRMED" ->
                    List.of("PREPARING", "CANCELLED")
                            .contains(newStatus);

            case "PREPARING" ->
                    List.of("OUT_FOR_DELIVERY", "CANCELLED")
                            .contains(newStatus);

            case "OUT_FOR_DELIVERY" ->
                    "DELIVERED".equals(newStatus);

            case "DELIVERED", "CANCELLED" -> false;

            default -> false;
        };

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change order status from "
                            + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    // 6. Delete an order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {

        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID must be positive");
        }

        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found");
        }

        orderRepository.deleteById(id);

        return "Order deleted successfully: " + id;
    }

    // Validate order quantity
    private void validateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero");
        }
    }
}