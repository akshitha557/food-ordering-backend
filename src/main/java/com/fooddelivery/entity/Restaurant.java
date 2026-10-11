

package com.fooddelivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Restaurant address is required")
    @Size(max = 255, message = "Restaurant address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "Restaurant phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Restaurant phone number must contain exactly 10 digits"
    )
    private String phone;

    private boolean active = true;

    public Restaurant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
