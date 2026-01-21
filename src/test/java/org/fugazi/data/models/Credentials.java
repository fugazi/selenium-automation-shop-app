package org.fugazi.data.models;

/**
 * Data model representing user credentials for authentication.
 * Uses record pattern for immutable data.
 */
public record Credentials(String email, String password, UserType userType) {

    /**
     * Predefined admin credentials for testing.
     */
    public static final Credentials ADMIN_CREDENTIALS = new Credentials(
            "admin@test.com",
            "admin123",
            UserType.ADMIN
    );

    /**
     * Predefined customer credentials for testing.
     */
    public static final Credentials CUSTOMER_CREDENTIALS = new Credentials(
            "user@test.com",
            "user123",
            UserType.CUSTOMER
    );

    /**
     * Enum representing user types.
     */
    public enum UserType {
        ADMIN,
        CUSTOMER
    }
}
