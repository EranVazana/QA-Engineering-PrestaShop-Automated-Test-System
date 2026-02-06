package prestashopCucumber.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceCalculator {
    private float productPrice = 0;
    private int productAmount = 0;
    private float shippingPrice = 0;
    private float handlingFee = 0;
    private float VAT = 0;
    private float discountAmount = 1;
    private float userDiscountPercent = 1;
    private boolean isBulk = false;

    private final List<String> coupons = new ArrayList<>();

    private static final Map<String, Float> USER_DISCOUNTS = Map.of(
            "VIP", 0.9f,
            "Registered", 1f,
            "Guest", 1f
    );

    private static final Map<String, Float> COUNTRY_TAX = Map.of(
            "Israel", 1f,
            "Germany", 1.1f
    );

    private static final Map<String, Float> SHIPPING_PRICES = Map.of(
            "Pickup", 0f,
            "Express", 25f
    );

    // ==================== 1. Product Setup ====================

    public void addProduct(float price, int amount) {
        this.productPrice = price;
        this.productAmount = amount;
    }

    public void addToProductPrice(float amount) {
        this.productPrice += amount;
    }

    public void setBulkTrue() {
        this.isBulk = true;
    }

    // ==================== 2. Shipping & Fees ====================

    public void setShippingPrice(float price) {
        this.shippingPrice = price;
    }

    public void setShippingPriceByName(String shippingName) {
        this.setShippingPrice(SHIPPING_PRICES.get(shippingName));
    }

    public void setHandlingFee(float fee) {
        this.handlingFee = fee;
    }

    // ==================== 3. Tax & Discounts ====================

    public void setVAT(float vat) {
        this.VAT = vat;
    }

    public void setTaxByCountry(String country) {
        this.setVAT(COUNTRY_TAX.get(country));
    }

    public void setUserDiscountPercent(float percent) {
        this.userDiscountPercent = percent;
    }

    public void setUserDiscountByUserType(String userType) {
        this.setUserDiscountPercent(USER_DISCOUNTS.get(userType));
    }

    public void applyCouponCode(String couponCode) {
        coupons.add(couponCode);
    }

    // ==================== 4. Calculate Total ====================

    public float getTotalPrice() {
        float total = productPrice * productAmount * VAT * userDiscountPercent;

        if (isBulk)
            total /= 2;

        if (total >= 600)
            shippingPrice = 0;

        if (total >= 200) {
            total *= 0.9f;
            discountAmount = 0.9f;
        }

        for (String coupon : coupons) {
            switch (coupon) {
                case "FREESHIP":
                    shippingPrice = 0;
                    break;
                case "RES10":
                case "SAVE10":
                    total *= 0.9f;
                    break;
                case "MINUS1":
                    total -= discountAmount;
                    break;
            }
        }

        if (shippingPrice > 0)
            total += (handlingFee * productAmount) + shippingPrice;

        return Math.round(total * 10f) / 10f;
    }
}