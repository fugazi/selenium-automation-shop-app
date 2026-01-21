package org.fugazi.pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import io.qameta.allure.Step;

import org.fugazi.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model for the products listing page.
 * Provides methods to search, filter, sort products and navigate pages.
 */
public class ProductsPage extends BasePage {

    // Components
    private final HeaderComponent header;

    // Main container
    private static final By MAIN_CONTENT = By.cssSelector("main.flex-1");

    // Search and filters
    private static final By SEARCH_INPUT = By.cssSelector("[data-testid='search-products-input']");

    // Product grid
    private static final By PRODUCT_CARDS = By.cssSelector("[data-testid^='product-card-']");
    private static final By PRODUCT_TITLE = By.cssSelector("[data-testid^='product-title-link-'] h3");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-testid^='product-price-']");
    private static final By PRODUCT_IMAGE_LINK = By.cssSelector("[data-testid^='product-image-link-']");

    // Skeleton loading indicator
    private static final By SKELETON_LOADER = By.cssSelector("[data-slot='skeleton']");

    // Pagination
    private static final By PAGINATION_CURRENT = By.cssSelector(
            "[data-testid='pagination-current'], [aria-current='page']");

    // Results info
    private static final By NO_RESULTS_MESSAGE = By.cssSelector("[data-testid='no-results'], .no-results");

    /**
     * Initializes the ProductsPage with a WebDriver instance.
     *
     * @param driver the WebDriver instance
     */
    public ProductsPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    /**
     * Gets the header component.
     *
     * @return the HeaderComponent instance
     */
    public HeaderComponent header() {
        return header;
    }

    /**
     * Verifies that the products page is loaded.
     *
     * @return true if the products page is loaded, false otherwise
     */
    @Override
    @Step("Verify products page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if products page is loaded");
        waitForPageLoad();
        waitForContentToLoad();
        return isDisplayed(MAIN_CONTENT) && (hasProducts() || isNoResultsDisplayed());
    }

    /**
     * Waits for content to fully load (skeletons disappear or products appear).
     */
    @Step("Wait for products to load")
    public void waitForContentToLoad() {
        log.debug("Waiting for products content to load");
        // Use a longer timeout for content loading due to parallel test execution
        var contentWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            contentWait.until(d -> {
                int skeletonCount = getElementCount(SKELETON_LOADER);
                int productCount = getElementCount(PRODUCT_CARDS);
                // Also check for no-results message
                boolean noResults = isElementPresent(NO_RESULTS_MESSAGE);
                return skeletonCount == 0 || productCount > 0 || noResults;
            });
        } catch (Exception e) {
            log.warn("Timeout waiting for content to load, continuing anyway");
        }
    }

    /**
     * Searches for products using the provided search term.
     *
     * @param searchTerm the term to search for
     */
    @Step("Search for products: {searchTerm}")
    public void searchProducts(String searchTerm) {
        log.info("Searching for products: {}", searchTerm);
        if (isElementPresent(SEARCH_INPUT)) {
            clear(SEARCH_INPUT);
            type(SEARCH_INPUT, searchTerm);
            waitForPageLoad();
            waitForContentToLoad();
        } else {
            header.searchProduct(searchTerm);
        }
    }

    /**
     * Filters products by the specified category.
     *
     * @param category the category name to filter by
     */
    @Step("Filter by category: {category}")
    public void filterByCategory(String category) {
        log.info("Filtering by category: {}", category);
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];
        driver.get(baseUrl + "?category=" + category);
        waitForPageLoad();
        waitForContentToLoad();
    }

    /**
     * Clears category filter and shows all products.
     */
    @Step("Clear category filter")
    public void clearCategoryFilter() {
        log.info("Clearing category filter");
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];
        driver.get(baseUrl);
        waitForPageLoad();
        waitForContentToLoad();
    }

    /**
     * Gets the current active category filter from URL.
     *
     * @return the active category, or empty string if none
     */
    @Step("Get active category filter")
    public String getActiveCategoryFilter() {
        var currentUrl = getCurrentUrl();
        var pattern = Pattern.compile("category=([^&]+)");
        var matcher = pattern.matcher(currentUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Checks if a specific category filter is active.
     *
     * @param category the category name to check
     * @return true if the category filter is active, false otherwise
     */
    @Step("Check if category filter is active: {category}")
    public boolean isCategoryFilterActive(String category) {
        return getActiveCategoryFilter().equalsIgnoreCase(category);
    }

    /**
     * Sorts products by the specified option.
     *
     * @param sortOption the sort option to apply
     */
    @Step("Sort by: {sortOption}")
    public void sortBy(String sortOption) {
        log.info("Sorting products by: {}", sortOption);
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];

        // Preserve existing category filter
        var category = getActiveCategoryFilter();
        var params = "sort=" + sortOption;
        if (!category.isEmpty()) {
            params = "category=" + category + "&" + params;
        }

        driver.get(baseUrl + "?" + params);
        waitForPageLoad();
        waitForContentToLoad();
    }

    /**
     * Gets the current sort option from URL.
     *
     * @return the sort option, or empty string if none
     */
    @Step("Get current sort option")
    public String getCurrentSortOption() {
        var currentUrl = getCurrentUrl();
        var pattern = Pattern.compile("sort=([^&]+)");
        var matcher = pattern.matcher(currentUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Checks if products are displayed.
     *
     * @return true if products exist, false otherwise
     */
    @Step("Check if products are displayed")
    public boolean hasProducts() {
        return getProductCount() > 0;
    }

    /**
     * Gets the number of products displayed.
     *
     * @return the count of products
     */
    @Step("Get product count")
    public int getProductCount() {
        var count = getElementCount(PRODUCT_CARDS);
        log.info("Found {} products in grid", count);
        return count;
    }

    /**
     * Gets all product cards as WebElements.
     *
     * @return list of product card WebElements
     */
    @Step("Get all product cards")
    public List<WebElement> getProductCards() {
        return getElements(PRODUCT_CARDS);
    }

    /**
     * Gets all product titles.
     *
     * @return list of product title strings
     */
    @Step("Get all product titles")
    public List<String> getProductTitles() {
        return getElements(PRODUCT_CARDS).stream()
                .map(card -> {
                    try {
                        return card.findElement(PRODUCT_TITLE).getText().trim();
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(title -> !title.isEmpty())
                .toList();
    }

    /**
     * Gets all product prices as strings.
     *
     * @return list of product price strings
     */
    @Step("Get all product prices")
    public List<String> getProductPrices() {
        return getElements(PRODUCT_CARDS).stream()
                .map(card -> {
                    try {
                        return card.findElement(PRODUCT_PRICE).getText().trim();
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(price -> !price.isEmpty())
                .toList();
    }

    /**
     * Gets all product prices as double values.
     *
     * @return list of numeric prices
     */
    @Step("Get product prices as numbers")
    public List<Double> getProductPricesAsNumbers() {
        return getProductPrices().stream()
                .map(this::extractPriceValue)
                .filter(price -> price > 0)
                .toList();
    }

    /**
     * Extracts numeric price value from price string.
     *
     * @param priceText the price text to parse
     * @return the numeric price value, or 0.0 if parsing fails
     */
    private double extractPriceValue(String priceText) {
        try {
            var pattern = Pattern.compile("[\\d,.]+");
            var matcher = pattern.matcher(priceText);
            if (matcher.find()) {
                var priceStr = matcher.group().replace(",", "");
                return Double.parseDouble(priceStr);
            }
        } catch (Exception e) {
            log.debug("Could not parse price: {}", priceText);
        }
        return 0.0;
    }

    /**
     * Clicks on product at specified index.
     *
     * @param index the index of the product to click
     * @throws IndexOutOfBoundsException if index is out of range
     */
    @Step("Click on product at index: {index}")
    public void clickProductByIndex(int index) {
        log.info("Clicking product at index: {}", index);
        var products = getProductCards();
        if (index >= 0 && index < products.size()) {
            var product = products.get(index);
            var initialUrl = getCurrentUrl();
            scrollToElement(PRODUCT_CARDS);

            try {
                var imageLink = product.findElement(PRODUCT_IMAGE_LINK);
                executeScript("arguments[0].click();", imageLink);
            } catch (Exception e) {
                executeScript("arguments[0].click();", product);
            }

            // Wait for URL to change (navigation to product detail)
            var urlWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            try {
                urlWait.until(d -> !Objects.equals(d.getCurrentUrl(), initialUrl));
            } catch (Exception ex) {
                log.warn("URL did not change after click on product index {}", index);
            }
            waitForPageLoad();
        } else {
            throw new IndexOutOfBoundsException(
                    "Product index " + index + " is out of bounds. Total products: " + products.size());
        }
    }

    /**
     * Clicks on the first product.
     */
    @Step("Click on first product")
    public void clickFirstProduct() {
        clickProductByIndex(0);
    }

    /**
     * Gets the product title at a specific index.
     *
     * @param index the index of the product
     * @return the product title, or empty string if not found
     */
    @Step("Get product title at index: {index}")
    public String getProductTitleAtIndex(int index) {
        var titles = getProductTitles();
        if (index >= 0 && index < titles.size()) {
            return titles.get(index);
        }
        return "";
    }

    /**
     * Checks if no results message is displayed.
     *
     * @return true if no results message is shown or product count is zero, false otherwise
     */
    @Step("Check if no results message is displayed")
    public boolean isNoResultsDisplayed() {
        return isElementPresent(NO_RESULTS_MESSAGE) || getProductCount() == 0;
    }

    /**
     * Gets the current page number.
     *
     * @return the page number, or 1 if not found
     */
    @Step("Get current page number")
    public int getCurrentPageNumber() {
        try {
            // Try from URL first
            var currentUrl = getCurrentUrl();
            var pattern = Pattern.compile("page=(\\d+)");
            var matcher = pattern.matcher(currentUrl);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }

            // Try from UI element
            if (isElementPresent(PAGINATION_CURRENT)) {
                var text = getText(PAGINATION_CURRENT);
                return Integer.parseInt(text.replaceAll("\\D", ""));
            }
        } catch (Exception e) {
            log.debug("Could not determine current page: {}", e.getMessage());
        }
        return 1;
    }

    /**
     * Navigates to a specific page number.
     *
     * @param pageNumber the page number to navigate to
     */
    @Step("Navigate to page: {pageNumber}")
    public void goToPage(int pageNumber) {
        log.info("Navigating to page: {}", pageNumber);
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];

        // Build URL with existing params
        var params = new StringBuilder();
        var category = getActiveCategoryFilter();
        var sort = getCurrentSortOption();

        if (!category.isEmpty()) {
            params.append("category=").append(category);
        }
        if (!sort.isEmpty()) {
            if (!params.isEmpty())
                params.append("&");
            params.append("sort=").append(sort);
        }
        if (!params.isEmpty())
            params.append("&");
        params.append("page=").append(pageNumber);

        driver.get(baseUrl + "?" + params);
        waitForPageLoad();
        waitForContentToLoad();
    }

    /**
     * Applies multiple filters at once (category, sort, and page).
     *
     * @param category the category to filter by, or null
     * @param sort the sort option to apply, or null
     * @param page the page number to navigate to, or null
     */
    @Step("Apply filters - category: {category}, sort: {sort}, page: {page}")
    public void applyFilters(String category, String sort, Integer page) {
        log.info("Applying filters - category: {}, sort: {}, page: {}", category, sort, page);
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];

        var params = new StringBuilder();
        if (category != null && !category.isEmpty()) {
            params.append("category=").append(category);
        }
        if (sort != null && !sort.isEmpty()) {
            if (!params.isEmpty())
                params.append("&");
            params.append("sort=").append(sort);
        }
        if (page != null && page > 1) {
            if (!params.isEmpty())
                params.append("&");
            params.append("page=").append(page);
        }

        var targetUrl = baseUrl;
        if (!params.isEmpty()) {
            targetUrl += "?" + params;
        }

        driver.get(targetUrl);
        waitForPageLoad();
        waitForContentToLoad();
    }

    /**
     * Removes all filters and resets to default product view.
     */
    @Step("Reset all filters")
    public void resetAllFilters() {
        log.info("Resetting all filters");
        var currentUrl = getCurrentUrl();
        var baseUrl = currentUrl.split("\\?")[0];
        driver.get(baseUrl);
        waitForPageLoad();
        waitForContentToLoad();
    }
}
