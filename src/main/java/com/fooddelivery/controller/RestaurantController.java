

package com.fooddelivery.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(
            RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // Get all restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    // Get one restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(
            @PathVariable Long id) {

        return restaurantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a restaurant
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(
            @RequestBody Restaurant restaurant) {

        Restaurant savedRestaurant =
                restaurantRepository.save(restaurant);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedRestaurant);
    }

    // Update a restaurant
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @RequestBody Restaurant updatedRestaurant) {

        return restaurantRepository.findById(id)
                .map(existingRestaurant -> {
                    existingRestaurant.setName(
                            updatedRestaurant.getName());

                    existingRestaurant.setAddress(
                            updatedRestaurant.getAddress());

                    existingRestaurant.setPhone(
                            updatedRestaurant.getPhone());

                    existingRestaurant.setActive(
                            updatedRestaurant.isActive());

                    Restaurant savedRestaurant =
                            restaurantRepository.save(existingRestaurant);

                    return ResponseEntity.ok(savedRestaurant);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long id) {

        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        restaurantRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}