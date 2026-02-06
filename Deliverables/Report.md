
# Deliverables

## How we prepared the SUT environment
To enable full coverage of the checkout and pricing user story, I prepared the SUT by configuring several entities and rules in the PrestaShop admin panel. These configurations were required to activate all relevant variants that participate in the CTD model.

### Products

I created multiple product types to cover different checkout and pricing behaviors:

-   A product with multiple combinations (e.g., size and color) to test variant-based pricing and selection.
    
-   A virtual product to verify checkout behavior when no shipping options are available.
    
-   A physical product with shipping fees to test how carrier costs are added during checkout.
    
-   A product restricted to in-store pickup only, ensuring no carrier selection is possible.
    
-   A product with a VIP-only discount to validate group-based price reductions.
    

### Customer Types

I configured the following customer profiles:

-   **Guest users**, who are required to register or log in before completing a purchase.
    
-   **Registered users**, who complete the checkout flow without additional discounts.
    
-   **VIP users**, who automatically receive a 10% discount on all eligible products.
    

### Shipping Options

I defined the following shipping methods:

-   **Pickup**, which is free of charge.
    
-   **Express shipping**, which incurs an additional cost.
    

### Cart Rules

I created cart-level discount rules to test conditional pricing behavior:

-   Free shipping for orders exceeding a predefined monetary threshold.
    
-   A percentage-based discount applied when the order total exceeds a specified amount.
    
-   A quantity-based percentage discount applied when purchasing a minimum number of items.
    

### Discount Coupons

I configured the following coupon codes:

-   **FREESHIP** – removes shipping costs from the order.
    
-   **RES10** – applies a 10% discount for VIP customers only.
    
-   **MINUS1** – subtracts 1€ from the total order price.
    
-   **SAVE10** – applies a 10% discount to the entire order.

## Explanation of the CTD model
To model the checkout and pricing user story of PrestaShop, I constructed a CTD model that captures all major factors influencing the checkout flow and final price calculation. The selected parameters and constraints directly reflect the configurations I introduced while preparing the SUT environment, including product types, customer roles, shipping options, discounts, and country-specific behavior.

### CTD Parameters

The following parameters were defined in the ACTS model:

-   **CustomerType**  
    Values: _Guest, Registered, VIP_  
    This parameter represents the different customer roles supported by the system. VIP customers automatically receive discounts, while guest and registered users follow the standard pricing flow.
    
-   **Product**  
    Values: _CD_Virtual, Shirt_Combo, Poster, Watch, Hat, Shirt_NoExpress_  
    These values correspond to the different product configurations created in the SUT, including virtual products, products with combinations, products with shipping restrictions, and products with VIP-specific pricing behavior.
    
-   **ShirtCombo**  
    Values: _NA, S_R, S_B, M_R, M_B_  
    This parameter models product combinations (size and color) and is only applicable when a configurable product is selected.
    
-   **CouponCode**  
    Values: _None, SAVE10, FREESHIP, RES10, MINUS1_  
    This parameter represents cart-level discount coupons that affect either the order total or shipping cost.
    
-   **Carrier**  
    Values: _NA, Pickup, Express_  
    This parameter captures shipping availability and cost differences, including cases where no carrier is available (e.g., virtual products).
    
-   **HandlingFee**  
    Values: _None, HatHandlingFee_  
    This parameter models additional product-specific fees applied during checkout.
    
-   **Quantity**  
    Values: _1, 2, 3, 200, 600_  
    Quantity was selected to represent normal purchases, bulk thresholds, and extreme values that trigger quantity-based cart rules or expose edge cases.
    
-   **WatchPriceMode**  
    Values: _Regular, VIP_Discount_  
    This parameter models special pricing logic that applies only to specific products and customer groups.
    
-   **Country**  
    Values: _Israel, Germany_  
    Country affects tax calculation and shipping behavior and allows validation of location-dependent pricing differences.
    

### Constraints

To ensure that only valid and realistic combinations are generated, I defined the following constraints in the CTD model:

-   Product combinations are only applicable when the selected product supports them:  
    _If Product = Shirt_Combo, then ShirtCombo  != NA; otherwise ShirtCombo = NA._
    
-   Virtual products cannot be shipped:  
    _If Product = CD_Virtual, then Carrier = NA._
    
-   The VIP-only coupon is restricted to VIP customers:  
    _If CouponCode = RES10, then CustomerType = VIP._
    
-   Special watch pricing applies only to VIP customers purchasing a watch:  
    _If WatchPriceMode = VIP_Discount, then Product = Watch and CustomerType = VIP._
    
-   Certain products restrict shipping options:  
    _If Product = Shirt_NoExpress, then Carrier != Express._

    
-   Handling fees apply only to specific products:  
    _If HandlingFee  != None, then Product = Hat._
    

### CTD Coverage Rationale

By combining these parameters and constraints, I generated a minimal set of valid test cases that ensures full pairwise coverage of all meaningful interactions affecting checkout flow and price calculation. 
This approach allows systematic testing of complex scenarios such as VIP discounts combined with bulk purchases, coupon restrictions, shipping availability, and country specific pricing while avoiding redundant or invalid test cases.

## Grouping of the CTD test cases

| Group ID | Description                                                                 | Tests IDs   |
|----------|-----------------------------------------------------------------------------|-------------|
| A        | Virtual products: shipping step is skipped entirely         | 1–5         |
| B        | Configurable products: size and color selection required       | 6–27        |
| C        | Restricted shipping products: Express is not available     | 28–31       |
| D        | VIP watch purchases: special VIP pricing logic verification                 | 32–36       |
| E        | Standard products: regular checkout flow with standard pricing               | 37–49       |


## Explanation of the Cucumber tests
I implemented the checkout acceptance tests in a single Cucumber feature file (**`PrestaShop_Checkout.feature`**) automate all CTD-generated test cases. 
Each scenario represents a distinct checkout flow, while the examples iterate over the relevant CTD combinations.

The feature file is organized according to the CTD grouping:

-   **Virtual products** – verify that the shipping step is skipped (Carrier = NA).
    
-   **Configurable products** – require selecting a size and color before checkout.
    
-   **Restricted shipping products** – verify that Express shipping is not available.
    
-   **VIP watch purchases** – validate special VIP pricing logic.
    
-   **Standard products** – validate the regular checkout flow with optional discounts and fees.
    

All steps are implemented using **Selenium**, enabling full end-to-end browser automation. The tests cover login or guest checkout, product selection, coupon application, shipping behavior, checkout completion, and final price verification. 
Each executed example is linked to a CTD TestID, ensuring full traceability between the CTD model and the automated tests.

----------
## Detected Bugs

No system-breaking bugs were detected during the testing process. However, several behaviors were observed that may be considered **functional anomalies** or potential usability concerns:

1.  **Bulk discounts and VIP discounts interaction**  
    Bulk discount rules are not applied to products that already receive a VIP-only discount. While this behavior may be intentional, it is not clearly indicated in the system and may lead to unexpected pricing outcomes.
    
2.  **Percentage-based cart discounts affecting fixed-amount coupons**  
    When a percentage-based cart discount (e.g., “X% off your cart”) is applied together with the **MINUS1** coupon, the fixed discount is also affected by the percentage reduction. As a result, instead of subtracting a full 1€, the effective reduction becomes smaller (e.g., 0.9€ when a 10% discount is applied). This behavior may not align with the expected interpretation of a fixed-amount coupon.
    
3.  **Free shipping removing handling fees**  
    Applying free shipping also removes product handling fees. This may be unintended, as handling fees could represent costs unrelated to delivery (e.g., special packaging), and might reasonably apply even when shipping is free or pickup is selected.
    

**Additional observation:**  
The order in which multiple discounts and coupons are applied is not always intuitive and may be confusing for store owners configuring pricing rules in PrestaShop.
