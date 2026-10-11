
package com.fooddelivery;

import com.fooddelivery.entity.Food;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.repository.FoodRepository;
import com.fooddelivery.repository.RestaurantRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadSampleData(
            RestaurantRepository restaurantRepository,
            FoodRepository foodRepository) {

        return args -> {

            // Avoid inserting duplicate sample data on every restart
            if (restaurantRepository.count() > 0
                    || foodRepository.count() > 0) {
                return;
            }

            Restaurant paradise = new Restaurant();
            paradise.setName("Paradise Biryani");
            paradise.setAddress("Secunderabad, Hyderabad");
            paradise.setPhone("9876543210");
            paradise.setActive(true);
            paradise = restaurantRepository.save(paradise);

            Restaurant mehfil = new Restaurant();
            mehfil.setName("Mehfil Restaurant");
            mehfil.setAddress("Madhapur, Hyderabad");
            mehfil.setPhone("9876543211");
            mehfil.setActive(true);
            mehfil = restaurantRepository.save(mehfil);

            Restaurant pistaHouse = new Restaurant();
            pistaHouse.setName("Pista House");
            pistaHouse.setAddress("Charminar, Hyderabad");
            pistaHouse.setPhone("9876543212");
            pistaHouse.setActive(true);
            pistaHouse = restaurantRepository.save(pistaHouse);

            addFood(foodRepository, "Chicken Dum Biryani",
                    "Traditional Hyderabadi chicken dum biryani",
                    280.00, "Biryani", paradise);

            addFood(foodRepository, "Mutton Biryani",
                    "Aromatic mutton dum biryani",
                    350.00, "Biryani", paradise);

            addFood(foodRepository, "Chicken 65",
                    "Spicy fried chicken starter",
                    220.00, "Starters", mehfil);

            addFood(foodRepository, "Paneer Tikka",
                    "Grilled paneer with Indian spices",
                    200.00, "Starters", mehfil);

            addFood(foodRepository, "Haleem",
                    "Traditional Hyderabadi haleem",
                    250.00, "Specials", pistaHouse);

            addFood(foodRepository, "Double Ka Meetha",
                    "Hyderabadi bread pudding dessert",
                    120.00, "Desserts", pistaHouse);

            System.out.println(
                    "Sample restaurants and food items inserted successfully.");
        };
    }

    private void addFood(
            FoodRepository foodRepository,
            String name,
            String description,
            double price,
            String category,
            Restaurant restaurant) {

        Food food = new Food();
        food.setName(name);
        food.setDescription(description);
        food.setPrice(price);
        food.setCategory(category);
        food.setAvailable(true);
        food.setRestaurant(restaurant);

        foodRepository.save(food);
    }
}
