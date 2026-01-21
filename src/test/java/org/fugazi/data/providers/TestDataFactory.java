package org.fugazi.data.providers;

import java.util.Locale;

import lombok.Getter;

import com.github.javafaker.Faker;

/**
 * Factory class for generating test data using JavaFaker.
 */
public class TestDataFactory {
    @Getter private static final Faker faker = new Faker(Locale.US);

    private TestDataFactory() {
    }

    /**
     * Generate a random search term.
     *
     * @return search term
     */
    public static String generateSearchTerm() {
        return faker.commerce().material();
    }

    /**
     * Generate a nonsense string for invalid search tests.
     *
     * @return random gibberish string
     */
    public static String generateInvalidSearchTerm() {
        return faker.regexify("[A-Z]{3}[0-9]{5}[a-z]{3}");
    }
}
