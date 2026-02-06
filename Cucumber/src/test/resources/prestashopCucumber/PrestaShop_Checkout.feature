Feature: PrestaShop Checkout and Pricing Validation
  As a user of the PrestaShop store
  I want to purchase different types of products
  So that I can verify the correct checkout flow and pricing calculations

  # =============================================================================
  # GROUP A: Virtual Products (No Shipping Step)
  # Flow: Login → Add Virtual Product → Checkout (skip shipping) → Coupon → Payment
  # Distinct: Carrier = NA, shipping step is skipped entirely
  # =============================================================================
  Scenario Outline: Checkout flow for Virtual Products (No Shipping)
    Given I am logged in as a "<CustomerType>" user
    When I add <Quantity> of "<Product>" to the cart
    And I proceed to checkout
    And I fill in the "<Country>" shipping address
    Then the shipping carrier step should not be available
    And I apply coupon "<CouponCode>"
    Then I should complete the order successfully
    And the final price should be correct

    Examples:
      | CustomerType | Product    | CouponCode | Country | Quantity | TestID |
      | VIP          | CD_Virtual | SAVE10     | Germany | 2        | 1      |
      | Guest        | CD_Virtual | None       | Israel  | 600      | 26     |
      | Registered   | CD_Virtual | FREESHIP   | Israel  | 3        | 34     |
      | VIP          | CD_Virtual | RES10      | Israel  | 1        | 37     |
      | Registered   | CD_Virtual | MINUS1     | Israel  | 200      | 41     |


  # =============================================================================
  # GROUP B: Configurable Products (Size/Color Selection Required)
  # Flow: Login → Product Page → Select Size/Color → Add to Cart → Checkout → Shipping → Coupon → Payment
  # Distinct: Requires selecting ShirtCombo variant before adding to cart
  # =============================================================================
  Scenario Outline: Checkout flow for Configurable Products
    Given I am logged in as a "<CustomerType>" user
    When I add <Quantity> of "<Product>" to the cart
    And I select size and color of "<ShirtCombo>"
    And I proceed to checkout
    And I fill in the "<Country>" shipping address
    And I fill in the "<Carrier>" carrier option
    And I apply coupon "<CouponCode>"
    Then the final price should be correct

    Examples:
      | CustomerType | Product     | ShirtCombo | CouponCode | Carrier | Country | Quantity | TestID |
      | Guest        | Shirt_Combo | S_R        | FREESHIP   | Express | Israel  | 3        | 2      |
      | VIP          | Shirt_Combo | S_B        | RES10      | Pickup  | Germany | 200      | 3      |
      | Registered   | Shirt_Combo | M_R        | MINUS1     | Express | Germany | 600      | 4      |
      | Guest        | Shirt_Combo | M_B        | None       | Pickup  | Israel  | 1        | 5      |
      | VIP          | Shirt_Combo | S_R        | None       | Express | Israel  | 2        | 10     |
      | Guest        | Shirt_Combo | S_B        | None       | Express | Israel  | 600      | 11     |
      | Guest        | Shirt_Combo | M_R        | None       | Pickup  | Israel  | 200      | 12     |
      | Registered   | Shirt_Combo | S_R        | SAVE10     | Pickup  | Germany | 1        | 13     |
      | VIP          | Shirt_Combo | S_B        | SAVE10     | Express | Israel  | 3        | 14     |
      | Guest        | Shirt_Combo | M_R        | SAVE10     | Pickup  | Israel  | 2        | 15     |
      | Registered   | Shirt_Combo | M_B        | SAVE10     | Express | Germany | 200      | 16     |
      | VIP          | Shirt_Combo | S_B        | FREESHIP   | Express | Germany | 1        | 17     |
      | Registered   | Shirt_Combo | M_R        | FREESHIP   | Express | Israel  | 2        | 18     |
      | VIP          | Shirt_Combo | M_B        | FREESHIP   | Express | Germany | 600      | 19     |
      | VIP          | Shirt_Combo | S_R        | RES10      | Pickup  | Germany | 3        | 20     |
      | VIP          | Shirt_Combo | M_R        | RES10      | Pickup  | Germany | 1        | 21     |
      | VIP          | Shirt_Combo | M_B        | RES10      | Pickup  | Germany | 2        | 22     |
      | VIP          | Shirt_Combo | S_R        | MINUS1     | Pickup  | Israel  | 200      | 23     |
      | Registered   | Shirt_Combo | S_B        | MINUS1     | Pickup  | Israel  | 2        | 24     |
      | VIP          | Shirt_Combo | M_B        | MINUS1     | Express | Israel  | 3        | 25     |
      | VIP          | Shirt_Combo | M_R        | RES10      | Express | Israel  | 3        | 45     |
      | VIP          | Shirt_Combo | S_R        | None       | Express | Germany | 600      | 46     |


  # =============================================================================
  # GROUP C: Restricted Shipping (No Express Option Available)
  # Flow: Login → Add Product → Checkout → Verify Express NOT available → Select Carrier → Coupon → Payment
  # Distinct: Must verify that "Express" shipping option is NOT displayed
  # =============================================================================
  Scenario Outline: Checkout flow with Restricted Shipping Options
    Given I am logged in as a "<CustomerType>" user
    When I add <Quantity> of "<Product>" to the cart
    And I proceed to checkout
    And I fill in the "<Country>" shipping address
    Then the "Express" shipping option should not be available
    When I fill in the "<Carrier>" carrier option
    And I apply coupon "<CouponCode>"
    And the final price should be correct

    Examples:
      | CustomerType | Product         | CouponCode | Carrier | Country | Quantity | TestID |
      | Registered   | Shirt_NoExpress | None       | Pickup  | Germany | 3        | 9      |
      | Guest        | Shirt_NoExpress | SAVE10     | Pickup  | Israel  | 200      | 33     |
      | VIP          | Shirt_NoExpress | RES10      | Pickup  | Israel  | 1        | 40     |
      | Guest        | Shirt_NoExpress | MINUS1     | Pickup  | Germany | 2        | 44     |


  # =============================================================================
  # GROUP D: VIP Watch with Special Pricing (VIP_Discount Mode)
  # Flow: Login as VIP → Add Watch → Checkout → Shipping → Coupon → Verify VIP Discount
  # Distinct: WatchPriceMode = VIP_Discount, requires VIP discount verification
  # =============================================================================
  Scenario Outline: VIP Watch Purchase with Special Pricing
    Given I am logged in as a "VIP" user
    When I add <Quantity> of "<Product>" to the cart
    And I proceed to checkout
    And I fill in the "<Country>" shipping address
    And I fill in the "<Carrier>" carrier option
    And I apply coupon "<CouponCode>"
    And the final price should be correct

    Examples:
      | Product | CouponCode | Carrier | Country | Quantity | TestID |
      | Watch   | RES10      | Express | Israel  | 600      | 7      |
      | Watch   | FREESHIP   | Express | Germany | 200      | 35     |
      | Watch   | MINUS1     | Pickup  | Germany | 2        | 43     |
      | Watch   | None       | Express | Germany | 1        | 48     |
      | Watch   | SAVE10     | Pickup  | Israel  | 3        | 49     |


  # =============================================================================
  # GROUP E: Standard Products (Regular Checkout Flow)
  # Flow: Login → Add Product → Checkout → Shipping → Coupon → Payment → Verify Regular Pricing
  # Includes: Poster, Hat, Watch (non-VIP) with standard checkout and regular pricing
  # =============================================================================
  Scenario Outline: Standard Product Purchase Flow
    Given I am logged in as a "<CustomerType>" user
    When I add <Quantity> of "<Product>" to the cart
    And I proceed to checkout
    And I fill in the "<Country>" shipping address
    And I fill in the "<Carrier>" carrier option
    And I apply coupon "<CouponCode>"
    And the final price should be correct with "<HandlingFee>" handling fee included

    Examples:
      | CustomerType | Product | CouponCode | Carrier | HandlingFee    | Country | Quantity | TestID |
      | Registered   | Poster  | FREESHIP   | Express | None           | Israel  | 200      | 6      |
      | Guest        | Hat     | MINUS1     | Pickup  | HatHandlingFee | Germany | 1        | 8      |
      | Guest        | Poster  | None       | Pickup  | None           | Germany | 1        | 27     |
      | Registered   | Watch   | None       | Pickup  | None           | Germany | 3        | 28     |
      | Registered   | Hat     | None       | Express | HatHandlingFee | Israel  | 200      | 29     |
      | VIP          | Poster  | SAVE10     | Express | None           | Israel  | 600      | 30     |
      | Guest        | Watch   | SAVE10     | Pickup  | None           | Israel  | 1        | 31     |
      | VIP          | Hat     | SAVE10     | Express | HatHandlingFee | Germany | 2        | 32     |
      | Registered   | Hat     | FREESHIP   | Express | HatHandlingFee | Israel  | 600      | 36     |
      | VIP          | Poster  | RES10      | Express | None           | Germany | 2        | 38     |
      | VIP          | Hat     | RES10      | Express | HatHandlingFee | Israel  | 3        | 39     |
      | VIP          | Poster  | MINUS1     | Pickup  | None           | Israel  | 3        | 42     |
      | Guest        | Hat     | MINUS1     | Pickup  | None           | Israel  | 3        | 47     |