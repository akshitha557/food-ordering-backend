
package com.fooddelivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.entity.FoodOrder;

public interface FoodOrderRepository
        extends JpaRepository<FoodOrder, Long> {

    List<FoodOrder> findByCustomerId(Long customerId);
}
