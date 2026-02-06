package prestashopCucumber.PrestaShopPages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import prestashopCucumber.Helpers.WebDriverManager;

import java.time.Duration;
import java.util.Map;

public class PrestaShopProductPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ==================== Product Data ====================

    private static final Map<String, String> PRODUCT_URLS = Map.of(
            "CD_Virtual", "/home/27-product-title-star-wars-the-rise-of-skywalker-original-motion-picture-soundtrack-digital-edition.html",
            "Shirt_Combo", "/home/26-40-star-wars-vintage-1977-retro-gradient-t-shirt.html#/1-size-s/10-color-red",
            "Poster", "/home/25-framed-classic-star-wars-a-new-hope-theatrical-poster-art.html",
            "Watch", "/home/24-men-s-star-wars-darth-vader-dark-side-skeleton-watch.html",
            "Hat", "/home/21-classic-black-star-wars-logo-baseball-cap.html",
            "Shirt_NoExpress", "/home/20-star-wars-shirt.html"
    );

    private static final Map<String, Integer> PRODUCT_PRICES = Map.of(
            "CD_Virtual", 50,
            "Shirt_Combo", 10,
            "Poster", 100,
            "Watch", 1000,
            "Hat", 20,
            "Shirt_NoExpress", 10
    );

    private static final Map<String, Integer> VIP_PRODUCT_PRICES = Map.of(
            "CD_Virtual", 50,
            "Shirt_Combo", 10,
            "Poster", 100,
            "Watch", 500,
            "Hat", 20,
            "Shirt_NoExpress", 10
    );

    private static final Map<String, Integer> HANDLING_FEES = Map.of(
            "HatHandlingFee", 10
    );

    private static final Map<String, Integer> SHIRT_PRICE_IMPACT = Map.of(
            "S_R", 0,
            "S_B", 0,
            "M_R", 10,
            "M_B", 15
    );

    // ==================== Locators ====================

    private static final By ADD_TO_CART_BTN = By.xpath("/html/body/main/section/div/div/div/section/div[1]/div[2]/div[2]/div[2]/form/div[2]/div/div[2]/button");
    private static final By QUANTITY_INPUT = By.id("quantity_wanted");
    private static final By SIZE_DROPDOWN = By.id("group_1");
    private static final By COLOR_BLUE = By.cssSelector("li.float-xs-left:nth-child(2) > label:nth-child(1) > input:nth-child(1)");
    private static final By CART_MODAL = By.xpath("/html/body/div[1]/div/div");
    private static final By CART_MODAL_CHECKOUT_BTN = By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div/div/a");
    private static final By SHOPPING_CART_TITLE = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/div/div[1]/h1");
    private static final By SHOPPING_CART_CHECKOUT_BTN = By.xpath("/html/body/main/section/div/div/div/section/div/div[2]/div[1]/div[2]/div/a");

    // ==================== Constructor ====================

    public PrestaShopProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ==================== 1. Navigation ====================

    public void navigateToProductPage(String productName) {
        String productUrl = PRODUCT_URLS.get(productName);
        if (productUrl == null)
            throw new IllegalArgumentException("Unknown product: " + productName);

        driver.get(WebDriverManager.BASE_URL + productUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART_BTN));
    }

    // ==================== 2. Product Configuration ====================

    public void setQuantity(int quantity) {
        WebElement quantityField = wait.until(ExpectedConditions.elementToBeClickable(QUANTITY_INPUT));
        quantityField.sendKeys(Keys.CONTROL + "A");
        quantityField.sendKeys(String.valueOf(quantity));
    }

    public void setSize(String size) {
        if (size.equalsIgnoreCase("M")) {
            WebElement sizeDropdown = wait.until(ExpectedConditions.elementToBeClickable(SIZE_DROPDOWN));
            new Select(sizeDropdown).selectByIndex(1);
        }
    }

    public void setColor(String color) {
        if (color.equalsIgnoreCase("B"))
            driver.findElement(COLOR_BLUE).click();
    }

    // ==================== 3. Cart Actions ====================

    public void addToCart() {
        driver.findElement(ADD_TO_CART_BTN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART_BTN));
    }

    public void proceedToCheckoutFromModal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(CART_MODAL));
        wait.until(ExpectedConditions.elementToBeClickable(CART_MODAL_CHECKOUT_BTN)).click();
    }

    public void proceedToCheckoutFromShoppingCart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SHOPPING_CART_TITLE));
        wait.until(ExpectedConditions.elementToBeClickable(SHOPPING_CART_CHECKOUT_BTN)).click();
    }

    // ==================== 4. Price Getters ====================

    public float getProductPrice(String productName, boolean isVIP) {
        return isVIP ? VIP_PRODUCT_PRICES.get(productName) : PRODUCT_PRICES.get(productName);
    }

    public int getShirtPriceImpact(String sizeAndColor) {
        return SHIRT_PRICE_IMPACT.get(sizeAndColor);
    }

    public float getHandlingFee(String feeType) {
        return HANDLING_FEES.getOrDefault(feeType, 0);
    }
}