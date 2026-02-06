package prestashopCucumber.Helpers;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

import java.time.Duration;

public class WebDriverManager {

    private static WebDriver driver;

    public static final String BASE_URL = "http://localhost:8080";

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        Map<String, Object> prefs = new HashMap<String, Object>();

        prefs.put("autofill.profile_enabled", false);

        prefs.put("autofill.credit_card_enabled", false);

        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @After
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void safeThreadWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (Exception exception) {
            System.out.println("safeThreadWait Exception: " + exception);
        }
    }
}
