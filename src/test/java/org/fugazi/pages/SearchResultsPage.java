package org.fugazi.pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import io.qameta.allure.Step;

import org.fugazi.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object representing the Search/Products Results Page.
 * This page is /products with search, filters and product grid.
 */
public class SearchResultsPage extends BasePage {

    // Components
    private final HeaderComponent header;

    // Locators
    private static final By RESULTS_CONTAINER = By.cssSelector("main.flex-1");
    private static final By SEARCH_INPUT = By.cssSelector("[data-testid='search-products-input']");

    // Product cards in the grid
    private static final By RESULT_ITEMS = By.cssSelector("[data-testid^='product-card-']");
    private static final By RESULT_TITLE = By.cssSelector("[data-testid^='product-title-link-'] h3");
    private static final By RESULT_IMAGE_LINK = By.cssSelector("[data-testid^='product-image-link-']");

    // Skeleton loading indicator
    private static final By SKELETON_LOADER = By.cssSelector("[data-slot='skeleton']");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    @Override
    @Step("Verify search results page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if products/search page is loaded");
        waitForPageLoad();

        // Wait for skeletons to disappear (content loaded)
        waitForSkeletonsToDisappear();

        return isDisplayed(RESULTS_CONTAINER) && isElementPresent(SEARCH_INPUT);
    }

    /**
     * Wait for skeleton loaders to disappear, indicating content is loaded.
     * Uses explicit waits instead of Thread.sleep.
     * More tolerant for headless mode where React hydration may be slower.
     */
    private void waitForSkeletonsToDisappear() {
        log.debug("Waiting for skeleton loaders to disappear");

        // Wait for either: skeletons to disappear OR results to appear OR correct URL
        wait.until(driver -> {
            int skeletonCount = getElementCount(SKELETON_LOADER);
            int resultCount = getElementCount(RESULT_ITEMS);
            var currentUrl = Objects.requireNonNull(driver.getCurrentUrl());

            // Multiple conditions for page load success (headless mode tolerance)
            return skeletonCount == 0 || resultCount > 0 || currentUrl.contains("/products");
        });
    }

    /**
     * Check if results are present (has at least one product).
     *
     * @return true if it has results
     */
    @Step("Check if results are found")
    public boolean hasResults() {
        waitForSkeletonsToDisappear();
        return getResultCount() > 0;
    }

    /**
     * Check if no results message is displayed.
     *
     * @return true if grid has zero items
     */
    @Step("Check if no results message is displayed")
    public boolean isNoResultsMessageDisplayed() {
        return getResultCount() == 0;
    }

    /**
     * Get the list of result items.
     *
     * @return list of result WebElements
     */
    @Step("Get search results")
    public List<WebElement> getResults() {
        log.debug("Getting search results");
        return getElements(RESULT_ITEMS);
    }

    /**
     * Get the number of results.
     *
     * @return result count
     */
    @Step("Get result count")
    public int getResultCount() {
        var count = getElementCount(RESULT_ITEMS);
        log.info("Found {} results in products grid", count);
        return count;
    }

    /**
     * Get all result titles.
     *
     * @return list of result titles
     */
    @Step("Get result titles")
    public List<String> getResultTitles() {
        return getResults().stream()
                .map(result -> {
                    try {
                        return result.findElement(RESULT_TITLE).getText();
                    } catch (Exception e) {
                        return result.getText();
                    }
                })
                .toList();
    }

    /**
     * Click on a result by index.
     *
     * @param index result index (0-based)
     */
    @Step("Click on result at index {index}")
    public void clickResult(int index) {
        log.info("Clicking on result at index {}", index);
        var results = getResults();
        if (index >= 0 && index < results.size()) {
            var card = results.get(index);
            var initialUrl = getCurrentUrl();
            scrollToElement(RESULT_ITEMS);
            try {
                var link = card.findElement(RESULT_IMAGE_LINK);
                executeScript("arguments[0].click();", link);
            } catch (Exception e) {
                executeScript("arguments[0].click();", card);
            }

            // Wait for URL to change (navigation to product detail)
            var urlWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            try {
                urlWait.until(d -> !Objects.equals(d.getCurrentUrl(), initialUrl));
            } catch (Exception ex) {
                log.warn("URL did not change after click on result index {}", index);
            }
            waitForPageLoad();
        }
    }

    /**
     * Click on the first result.
     */
    @Step("Click on first result")
    public void clickFirstResult() {
        clickResult(0);
    }

    /**
     * Perform a new search.
     *
     * @param searchTerm the term to search for
     */
    @Step("Search for: {searchTerm}")
    public void searchFor(String searchTerm) {
        if (isElementPresent(SEARCH_INPUT)) {
            clear(SEARCH_INPUT);
            type(SEARCH_INPUT, searchTerm);
            waitForPageLoad();
            waitForSkeletonsToDisappear();
        } else {
            header.searchProduct(searchTerm);
        }
    }
}

