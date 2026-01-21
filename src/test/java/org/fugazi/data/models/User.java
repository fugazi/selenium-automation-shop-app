package org.fugazi.data.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Data model representing a User for testing.
 */
@Setter @Getter public class User {

    // Getters and Setters
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public User(String firstName, String lastName, String email, String password, String phone,
            String address, String city, String state, String zipCode, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserBuilder city(String city) {
            this.city = city;
            return this;
        }

        public UserBuilder state(String state) {
            this.state = state;
            return this;
        }

        public UserBuilder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public UserBuilder country(String country) {
            this.country = country;
            return this;
        }

        public User build() {
            return new User(firstName, lastName, email, password, phone, address, city, state, zipCode, country);
        }
    }
}

