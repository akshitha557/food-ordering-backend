
package com.fooddelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fooddelivery.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
