
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

import com.fooddelivery.entity.Food;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.repository.FoodRepository;
import com.fooddelivery.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class FoodController {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    public FoodController(
            FoodRepository foodRepository,
            RestaurantRepository restaurantRepository) {
        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // Get all food items
    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods() {
        return ResponseEntity.ok(foodRepository.findAll());
    }

    // Get food item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(
            @PathVariable Long id) {

        return foodRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new food item
    @PostMapping
    public ResponseEntity<?> addFood(@RequestBody Food food) {

        if (food.getRestaurant() == null
                || food.getRestaurant().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("Restaurant ID is required.");
        }

        Long restaurantId = food.getRestaurant().getId();

        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> {
                    food.setRestaurant(restaurant);
                    Food savedFood = foodRepository.save(food);

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body((Object) savedFood);
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body("Restaurant not found with ID: "
                                + restaurantId));
    }

    // Update an existing food item
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestBody Food updatedFood) {

        return foodRepository.findById(id)
                .map(existingFood -> {

                    existingFood.setName(updatedFood.getName());
                    existingFood.setPrice(updatedFood.getPrice());
                    existingFood.setCategory(updatedFood.getCategory());
                    existingFood.setDescription(
                            updatedFood.getDescription());
                    existingFood.setAvailable(
                            updatedFood.isAvailable());

                    if (updatedFood.getRestaurant() != null
                            && updatedFood.getRestaurant().getId() != null) {

                        Long restaurantId =
                                updatedFood.getRestaurant().getId();

                        Restaurant restaurant =
                                restaurantRepository.findById(restaurantId)
                                        .orElse(null);

                        if (restaurant == null) {
                            return ResponseEntity.badRequest()
                                    .body((Object) ("Restaurant not found with ID: "
                                            + restaurantId));
                        }

                        existingFood.setRestaurant(restaurant);
                    }

                    Food savedFood =
                            foodRepository.save(existingFood);

                    return ResponseEntity.ok((Object) savedFood);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a food item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(
            @PathVariable Long id) {

        if (!foodRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        foodRepository.deleteById(id);

        return ResponseEntity.ok("Food item deleted successfully.");
    }
}