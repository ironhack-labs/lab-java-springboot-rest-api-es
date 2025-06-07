package com.example.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Customer {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Email(message = "Email debe tener un formato válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Min(value = 18, message = "La edad mínima es 18 años")
    private int age;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    public Customer() {}

    public Customer(String name, String email, int age, String address) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.address = address;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
