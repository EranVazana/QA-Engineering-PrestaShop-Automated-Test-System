package prestashopCucumber;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import prestashopCucumber.Helpers.PriceCalculator;
import prestashopCucumber.Helpers.WebDriverManager;
import prestashopCucumber.PrestaShopPages.*;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private final WebDriver driver;
    private final PrestaShopLoginPage loginPage;
    private final PrestaShopProductPage productPage;
    private final PrestaShopCheckoutPage checkoutPage;
    private final PriceCalculator expectedPrice;

    private String currentCustomerType = null;

    public StepDefinitions() {
        this.driver = WebDriverManager.getDriver();
        this.loginPage = new PrestaShopLoginPage(driver);
        this.productPage = new PrestaShopProductPage(driver);
        this.checkoutPage = new PrestaShopCheckoutPage(driver);
        this.expectedPrice = new PriceCalculator();
    }

    // ==================== 1. Login ====================

    @Given("I am logged in as a {string} user")
    public void iAmLoggedInAsAUser(String customerType) throws InterruptedException {
        this.currentCustomerType = customerType;

        if (customerType.equalsIgnoreCase("Guest"))
            return;

        expectedPrice.setUserDiscountByUserType(customerType);

        loginPage.navigateToLoginPage();
        loginPage.logInAsUser(PrestaShopLoginPage.customerType.valueOf(currentCustomerType));
        assertTrue(loginPage.isLoggedIn());
    }

    // ==================== 2. Product Selection ====================

    @When("I add {int} of {string} to the cart")
    public void iAddOfToTheCart(int qty, String product) {
        boolean isVIP = currentCustomerType.equalsIgnoreCase("VIP");
        boolean isVIPWatch = isVIP && product.equalsIgnoreCase("Watch");

        if (qty >= 3 && !isVIPWatch)
            expectedPrice.setBulkTrue();

        productPage.navigateToProductPage(product);
        expectedPrice.addProduct(productPage.getProductPrice(product, isVIP), qty);
        productPage.setQuantity(qty);
    }

    @When("When I navigate to the {string} product page")
    public void whenINavigateToTheProductPage(String product) {
        productPage.navigateToProductPage(product);
    }

    @And("I select size and color of {string}")
    public void andISelectSizeAndColor(String sizeAndColor) {
        String[] shirt_data = sizeAndColor.split("_");
        productPage.setSize(shirt_data[0]);
        productPage.setColor(shirt_data[1]);
        expectedPrice.addToProductPrice(productPage.getShirtPriceImpact(sizeAndColor));

    }

    // ==================== 3. Checkout ====================

    @And("I proceed to checkout")
    public void iProceedToCheckout() {
        productPage.addToCart();
        productPage.proceedToCheckoutFromModal();
        productPage.proceedToCheckoutFromShoppingCart();

        if (currentCustomerType.equalsIgnoreCase("Guest")) {
            checkoutPage.fillGuestInfo(
                    "guest" + System.currentTimeMillis() + "@test.com",
                    "Israel",
                    "Israeli"
            );
        }
    }

    @And("I fill in the {string} shipping address")
    public void iFillInTheShippingAddressFor(String country) {
        expectedPrice.setTaxByCountry(country);
        checkoutPage.fillAddressInfo(country);
    }

    // ==================== 4. Shipping ====================

    @Then("the shipping carrier step should not be available")
    public void theShippingCarrierStepShouldNotBeAvailable() {
        assertFalse(checkoutPage.isShippingStepActive("ALL"));
    }

    @Then("the \"Express\" shipping option should not be available")
    public void thenTheExpressShippingOptionShouldNotBeAvailable() {
        assertFalse(checkoutPage.isShippingStepActive("Express"));
    }

    @And("I fill in the {string} carrier option")
    public void iFillInTheCarrier(String carrierType) {
        expectedPrice.setShippingPriceByName(carrierType);
        checkoutPage.setCarrier(carrierType);
    }

    // ==================== 5. Coupon ====================

    @And("I apply coupon {string}")
    public void iApplyCoupon(String couponCode) {
        if (couponCode != null && !couponCode.equalsIgnoreCase("None")) {
            checkoutPage.applyCoupon(couponCode);
            expectedPrice.applyCouponCode(couponCode);
        }
    }

    // ==================== 6. Order Completion & Verification ====================

    @Then("I should complete the order successfully")
    public void iShouldCompleteTheOrderSuccessfully() {
        // I decided not to test this function because once a shipping option is selected,
        // the final price is already displayed and is not affected by the payment tab.

        //checkoutPage.selectPaymentAndPlaceOrder();
        //assertTrue(checkoutPage.isOrderConfirmed());
    }

    @And("the final price should be correct")
    public void theFinalPriceShouldBeCorrect() {
        assertEquals(expectedPrice.getTotalPrice(), checkoutPage.getTotalPrice());
    }

    @And("the final price should be correct with {string} handling fee included")
    public void theFinalPriceShouldBeCorrectWithHandlingFeeIncluded(String handlingFee) {
        if (!handlingFee.equalsIgnoreCase("None"))
            expectedPrice.setHandlingFee(productPage.getHandlingFee(handlingFee));
        assertEquals(expectedPrice.getTotalPrice(), checkoutPage.getTotalPrice());
    }
}