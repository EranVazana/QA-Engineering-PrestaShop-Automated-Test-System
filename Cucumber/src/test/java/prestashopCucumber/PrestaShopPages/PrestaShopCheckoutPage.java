package prestashopCucumber.PrestaShopPages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import prestashopCucumber.Helpers.WebDriverManager;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PrestaShopCheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ==================== Address Data ====================

    private static final Map<String, String[]> COUNTRY_ADDRESS = Map.of(
            "Israel", new String[]{"Omer", "8496501", "Sheizaf 99"},
            "Germany", new String[]{"Torgau", "04860", "Aufbauweg 99"}
    );

    // ==================== Locators ====================

    private static final By FIRSTNAME_INPUT = By.id("field-firstname");
    private static final By EMAIL_INPUT = By.id("field-email");
    private static final By LASTNAME_INPUT = By.id("field-lastname");
    private static final By GDPR_CHECKBOX_1 = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[1]/div/div/div[1]/form/div/div[8]/div[1]/span/label");
    private static final By GDPR_CHECKBOX_2 = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[1]/div/div/div[1]/form/div/div[10]/div[1]/span/label");
    private static final By GUEST_CONTINUE_BTN = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[1]/div/div/div[1]/form/footer/button");

    private static final By ADDRESS_HEADER = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[2]/h1");
    private static final By DELIVERY_ADDRESSES = By.id("delivery-addresses");
    private static final By EDIT_ADDRESS_BTN = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[2]/div/div/form/div[1]/article/footer/a[2]");
    private static final By ADDRESS_INPUT = By.id("field-address1");
    private static final By CITY_INPUT = By.id("field-city");
    private static final By POSTCODE_INPUT = By.id("field-postcode");
    private static final By COUNTRY_DROPDOWN = By.id("field-id_country");
    private static final By ADDRESS_CONTINUE_BTN = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[2]/div/div/form/div/div/footer/button");

    private static final By SHIPPING_FORM = By.id("js-delivery");
    private static final By EXPRESS_OPTION = By.id("delivery_option_34");
    private static final By SHIPPING_CONTAINER = By.xpath("//*[@id=\"js-delivery\"]/div/div[1]");

    private static final By PROMO_LINK = By.xpath("//*[@id=\"js-checkout-summary\"]/div[3]/div/p/a");
    private static final By PROMO_INPUT = By.xpath("/html/body/main/section/div/div/div/section/div/div[2]/section/div[3]/div/div/div/form/input[3]");

    private static final By PAYMENT_LABEL = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[3]/div/div[2]/div[1]/div/label");
    private static final By PAYMENT_RADIO = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[3]/div/div[2]/div[1]/div/span");
    private static final By TERMS_CHECKBOX = By.id("conditions_to_approve[terms-and-conditions]");
    private static final By PLACE_ORDER_BTN = By.xpath("/html/body/main/section/div/div/div/section/div/div[1]/section[3]/div/div[3]/div[1]/button");

    private static final By TOTAL_PRICE = By.xpath("/html/body/main/section/div/div/div/section/div/div[2]/section/div[2]/div[1]/span[2]");

    // ==================== Constructor ====================

    public PrestaShopCheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ==================== 1. Guest Info ====================

    public void fillGuestInfo(String email, String firstName, String lastName) {
        WebDriverManager.safeThreadWait(2);

        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRSTNAME_INPUT));

        driver.findElement(EMAIL_INPUT).sendKeys(email);
        driver.findElement(FIRSTNAME_INPUT).sendKeys(firstName);
        driver.findElement(LASTNAME_INPUT).sendKeys(lastName);

        clickIfPresent(GDPR_CHECKBOX_1);
        clickIfPresent(GDPR_CHECKBOX_2);

        driver.findElement(GUEST_CONTINUE_BTN).click();
    }

    // ==================== 2. Address ====================

    public void fillAddressInfo(String country) {
        String[] addressData = COUNTRY_ADDRESS.get(country);
        performFillAddressInfo(country, addressData[0], addressData[1], addressData[2]);
    }

    private void performFillAddressInfo(String country, String city, String zipCode, String address) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADDRESS_HEADER));

        try {
            driver.findElement(DELIVERY_ADDRESSES);
            driver.findElement(EDIT_ADDRESS_BTN).click();
        } catch (NoSuchElementException ignored) {}

        driver.findElement(ADDRESS_INPUT).sendKeys(address);
        driver.findElement(CITY_INPUT).sendKeys(city);
        driver.findElement(POSTCODE_INPUT).sendKeys(zipCode);

        WebElement countryDropdown = driver.findElement(COUNTRY_DROPDOWN);
        countryDropdown.click();
        countryDropdown.findElement(By.xpath("//option[contains(text(),'" + country + "')]")).click();

        driver.findElement(ADDRESS_CONTINUE_BTN).click();
    }

    // ==================== 3. Shipping ====================

    public boolean isShippingStepActive(String shippingOption) {
        try {
            WebElement shippingForm = wait.until(ExpectedConditions.visibilityOfElementLocated(SHIPPING_FORM));

            List<WebElement> labels = shippingForm.findElements(By.tagName("label"));

            if (shippingOption.equalsIgnoreCase("ALL"))
                return !labels.isEmpty();

            for (WebElement label : labels)
                if (label.getText().contains(shippingOption))
                    return true;
        }
        catch (TimeoutException exception) {
            return false;
        }
        return false;
    }

    public void setCarrier(String carrier) {
        if (carrier.equalsIgnoreCase("Pickup"))
            return;

        if (carrier.equalsIgnoreCase("Express")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SHIPPING_CONTAINER));
            driver.findElement(EXPRESS_OPTION).click();
        }

        WebDriverManager.safeThreadWait(2);
    }

    // ==================== 4. Coupon ====================

    public void applyCoupon(String couponCode) {
        WebDriverManager.safeThreadWait(2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(PROMO_LINK)).click();

        WebDriverManager.safeThreadWait(2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(PROMO_INPUT)).sendKeys(couponCode + Keys.ENTER);

        WebDriverManager.safeThreadWait(2);
    }

    // ==================== 5. Payment & Order ====================

    public void selectPaymentAndPlaceOrder() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(PAYMENT_LABEL));
        driver.findElement(PAYMENT_RADIO).click();
        driver.findElement(TERMS_CHECKBOX).click();
    }

    public boolean isOrderConfirmed() {
        WebDriverManager.safeThreadWait(2);
        return driver.findElement(PLACE_ORDER_BTN).isEnabled();
    }

    // ==================== 6. Price ====================

    public float getTotalPrice() {
        String priceText = wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL_PRICE)).getText();
        float price = Float.parseFloat(priceText.replaceAll("[^0-9.]", ""));
        return Math.round(price * 10f) / 10f;
    }

    // ==================== Helper ====================

    private void clickIfPresent(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            if (!element.isSelected())
                element.click();
        } catch (Exception ignored) {}
    }
}