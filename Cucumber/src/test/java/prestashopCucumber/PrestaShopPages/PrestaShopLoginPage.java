package prestashopCucumber.PrestaShopPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import prestashopCucumber.Helpers.WebDriverManager;

import java.time.Duration;

public class PrestaShopLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ==================== User Credentials ====================

    public enum customerType { Guest, Registered, VIP }

    private static final String REGISTERED_EMAIL = "asherfrank420@walla.co.il";
    private static final String REGISTERED_PASSWORD = "asherfrank420";
    private static final String VIP_EMAIL = "eyalhoffman@walla.co.il";
    private static final String VIP_PASSWORD = "eyalhoffman6767";

    // ==================== Locators ====================

    private static final By LOGIN_LINK = By.xpath("/html/body/main/header/nav/div/div/div[1]/div[2]/div[2]/div/a");
    private static final By EMAIL_INPUT = By.id("field-email");
    private static final By PASSWORD_INPUT = By.id("field-password");
    private static final By SUBMIT_BTN = By.id("submit-login");
    private static final By LOGGED_IN_INDICATOR = By.xpath("/html/body/main/header/nav/div/div/div[1]/div[2]/div[2]/div/a[2]/span");

    // ==================== Constructor ====================

    public PrestaShopLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ==================== 1. Navigation ====================

    public void navigateToLoginPage() {
        driver.get(WebDriverManager.BASE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_LINK)).click();
    }

    // ==================== 2. Login ====================

    public void logInAsUser(customerType type) {
        switch (type) {
            case Registered:
                performLogin(REGISTERED_EMAIL, REGISTERED_PASSWORD);
                break;
            case VIP:
                performLogin(VIP_EMAIL, VIP_PASSWORD);
                break;
        }
    }

    private void performLogin(String email, String password) {
        navigateToLoginPage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT)).sendKeys(email);
        driver.findElement(PASSWORD_INPUT).sendKeys(password);
        driver.findElement(SUBMIT_BTN).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGGED_IN_INDICATOR));
    }

    // ==================== 3. Verification ====================

    public boolean isLoggedIn() {
        try {
            return driver.findElement(LOGGED_IN_INDICATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}