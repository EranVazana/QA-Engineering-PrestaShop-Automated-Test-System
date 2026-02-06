Feature: PrestaShop Checkout and Pricing Validation
  As a user of the PrestaShop store
  I want to purchase different types of products
  So that I can verify the correct checkout flow and pricing calculations

  # GROUP A: Virtual Products (Skips Shipping Step)
  # Flow: Login -> Add Product -> Checkout (No Shipping) -> Payment
  Scenario Outline: Checkout flow for Virtual Products (No Shipping)
    Given I enter the store as a "<CustomerType>" user
    When I select the virtual product "<Product>"
    And I proceed to the checkout
    # Virtual products skip the physical shipping carrier selection step
    And I enter the discount code "<CouponCode>" if available
    Then I should be able to complete the order
    And the final price should match the expected calculation for "<Quantity>" items

    Examples:
      | CustomerType | Product    | CouponCode | Quantity | Country | TestID |
      | Registered   | CD_Virtual | None       | 2        | Germany | 1      |
      | VIP          | CD_Virtual | SAVE10     | 199      | Israel  | 7      |
      | Guest        | CD_Virtual | MINUS20    | 200      | France  | 13     |
      | VIP          | CD_Virtual | FREESHIP   | 1        | Germany | 19     |
      | VIP          | CD_Virtual | RES10      | 3        | France  | 25     |
      | Guest        | CD_Virtual | MIN200     | 200      | France  | 31     |
      | Registered   | CD_Virtual | OLD        | 1        | France  | 37     |

  # GROUP B: Configurable Products (Requires Size/Color)
  # Flow: Login -> Product Page -> Select Size/Color -> Add to Cart -> Checkout
  Scenario Outline: Checkout flow for Configurable Products
    Given I enter the store as a "<CustomerType>" user
    When I go to the product page for "<Product>"
    # Unique step for Group B: selecting variant
    And I select the size and color combination "<ShirtCombo>"
    And I add "<Quantity>" items to the cart
    And I proceed to checkout selecting "<Carrier>" shipping to "<Country>"
    And I enter the discount code "<CouponCode>" if available
    Then the final price should be correct

    Examples:
      | CustomerType | Product     | ShirtCombo | CouponCode | Carrier  | Quantity | Country | TestID |
      | VIP          | Shirt_Combo | S_R        | None       | Pickup   | 3        | Israel  | 2      |
      | Guest        | Shirt_Combo | S_B        | SAVE10     | Standard | 200      | Germany | 8      |
      | VIP          | Shirt_Combo | M_R        | MINUS20    | Standard | 1        | Israel  | 14     |
      | Registered   | Shirt_Combo | M_B        | FREESHIP   | Express  | 2        | France  | 20     |
      | VIP          | Shirt_Combo | S_R        | RES10      | Express  | 199      | Germany | 26     |
      | Registered   | Shirt_Combo | S_B        | MIN200     | Express  | 200      | Israel  | 32     |
      | Guest        | Shirt_Combo | M_R        | OLD        | Express  | 2        | Germany | 38     |
      | Guest        | Shirt_Combo | S_R        | SAVE10     | Standard | 1        | France  | 43     |
      | Registered   | Shirt_Combo | S_R        | MINUS20    | Standard | 2        | Germany | 44     |
      | Guest        | Shirt_Combo | S_R        | FREESHIP   | Pickup   | 200      | Israel  | 45     |
      | Guest        | Shirt_Combo | S_R        | MIN200     | Pickup   | 200      | Israel  | 46     |
      | Guest        | Shirt_Combo | S_R        | OLD        | Express  | 2        | France  | 47     |
      | VIP          | Shirt_Combo | S_B        | None       | Pickup   | 3        | France  | 48     |
      | VIP          | Shirt_Combo | S_B        | MINUS20    | Express  | 199      | Israel  | 49     |
      | Registered   | Shirt_Combo | S_B        | FREESHIP   | Pickup   | 1        | France  | 50     |
      | VIP          | Shirt_Combo | S_B        | RES10      | Express  | 2        | France  | 51     |
      | VIP          | Shirt_Combo | S_B        | OLD        | Pickup   | 3        | Israel  | 52     |
      | Registered   | Shirt_Combo | M_R        | None       | Pickup   | 3        | France  | 53     |
      | Registered   | Shirt_Combo | M_R        | SAVE10     | Express  | 199      | Germany | 54     |
      | Registered   | Shirt_Combo | M_R        | FREESHIP   | Standard | 200      | Germany | 55     |
      | VIP          | Shirt_Combo | M_R        | RES10      | Standard | 200      | Israel  | 56     |
      | Registered   | Shirt_Combo | M_R        | MIN200     | Pickup   | 200      | Israel  | 57     |
      | Guest        | Shirt_Combo | M_B        | None       | Standard | 1        | Germany | 58     |
      | VIP          | Shirt_Combo | M_B        | SAVE10     | Pickup   | 3        | Israel  | 59     |
      | Guest        | Shirt_Combo | M_B        | MINUS20    | Standard | 199      | Germany | 60     |
      | VIP          | Shirt_Combo | M_B        | RES10      | Express  | 200      | Germany | 61     |
      | Guest        | Shirt_Combo | M_B        | MIN200     | Standard | 200      | Germany | 62     |
      | VIP          | Shirt_Combo | M_B        | OLD        | Standard | 2        | France  | 63     |

  # GROUP C: Restricted Shipping Flow (No Express)
  # Flow: Login -> Add Product -> Checkout -> Verify "Express" is missing
  Scenario Outline: Checkout flow with Restricted Shipping Options
    Given I enter the store as a "<CustomerType>" user
    When I select the product "<Product>"
    And I add "<Quantity>" items to the cart
    And I proceed to the shipping method step
    # Verification step: Ensure Express is NOT an option for this product
    Then the "Express" shipping option should not be available
    When I select the "<Carrier>" shipping method
    And I complete the order with coupon "<CouponCode>"
    Then the final price should be correct

    Examples:
      | CustomerType | Product         | Carrier  | CouponCode | Quantity | Country | TestID |
      | Registered   | Shirt_NoExpress | Standard | None       | 3        | France  | 6      |
      | Registered   | Shirt_NoExpress | Pickup   | SAVE10     | 199      | Germany | 12     |
      | Registered   | Shirt_NoExpress | Pickup   | MINUS20    | 200      | Israel  | 18     |
      | VIP          | Shirt_NoExpress | Pickup   | FREESHIP   | 1        | Israel  | 24     |
      | VIP          | Shirt_NoExpress | Standard | RES10      | 2        | Germany | 30     |
      | Guest        | Shirt_NoExpress | Pickup   | MIN200     | 200      | Israel  | 36     |
      | VIP          | Shirt_NoExpress | Pickup   | OLD        | 1        | Germany | 42     |

  # GROUP D: VIP User Flow (Special Pricing Logic)
  # Flow: Login as VIP -> Add Product -> Checkout -> Verify Price (VIP or Regular)
  Scenario Outline: VIP User Purchase with Logic Verification
    Given I am logged in as a "VIP" user
    When I select the product "<Product>"
    And I add "<Quantity>" items to the cart
    And I proceed to checkout selecting "<Carrier>" shipping to "<Country>"
    And I enter the discount code "<CouponCode>" if available
    # Uses WatchPriceMode to decide if VIP discount applies or price is Regular
    Then the final price should be calculated according to the "<WatchPriceMode>" rule
    And any handling fee "<HandlingFee>" should be included

    Examples:
      | Product | WatchPriceMode | Carrier  | CouponCode | Quantity | HandlingFee | Country | TestID |
      | Watch   | VIP_Discount   | Express  | None       | 200      | None        | Germany | 4      |
      | Poster  | Regular        | Express  | MINUS20    | 2        | None        | Israel  | 15     |
      | Hat     | Regular        | Standard | MINUS20    | 199      | HatHandling | France  | 17     |
      | Watch   | VIP_Discount   | Standard | FREESHIP   | 199      | None        | Israel  | 22     |
      | Poster  | Regular        | Standard | RES10      | 200      | None        | Israel  | 27     |
      | Watch   | VIP_Discount   | Pickup   | RES10      | 1        | None        | France  | 28     |
      | Hat     | Regular        | Standard | RES10      | 2        | HatHandling | France  | 29     |
      | Poster  | Regular        | Pickup   | MIN200     | 200      | None        | Germany | 33     |
      | Watch   | VIP_Discount   | Standard | MIN200     | 200      | None        | Israel  | 34     |
      | Poster  | Regular        | Pickup   | OLD        | 3        | None        | Israel  | 39     |
      | Watch   | VIP_Discount   | Standard | OLD        | 199      | None        | Germany | 40     |
      | Watch   | VIP_Discount   | Express  | SAVE10     | 2        | None        | France  | 65     |
      | Watch   | VIP_Discount   | Pickup   | MINUS20    | 3        | None        | Israel  | 66     |

  # GROUP E: Standard Guest and Registered User Flow
  # Flow: Login/Guest -> Add Product -> Checkout -> Verify Standard Price
  Scenario Outline: Standard Purchase Flow for Guest and Registered Users
    Given I enter the store as a "<CustomerType>" user
    When I select the product "<Product>"
    And I add "<Quantity>" items to the cart
    And I proceed to checkout selecting "<Carrier>" shipping to "<Country>"
    And I enter the discount code "<CouponCode>" if available
    Then the final price should be calculated using "Regular" pricing rules
    And any handling fee "<HandlingFee>" should be included

    Examples:
      | CustomerType | Product | Carrier  | CouponCode | Quantity | HandlingFee | Country | TestID |
      | Guest        | Poster  | Standard | None       | 199      | None        | France  | 3      |
      | Guest        | Hat     | Pickup   | None       | 1        | HatHandling | Israel  | 5      |
      | Registered   | Poster  | Express  | SAVE10     | 1        | None        | France  | 9      |
      | Guest        | Watch   | Pickup   | SAVE10     | 2        | None        | France  | 10     |
      | Guest        | Hat     | Express  | SAVE10     | 3        | HatHandling | Germany | 11     |
      | Registered   | Watch   | Pickup   | MINUS20    | 3        | None        | Israel  | 16     |
      | Guest        | Poster  | Pickup   | FREESHIP   | 3        | None        | Germany | 21     |
      | Registered   | Hat     | Express  | FREESHIP   | 200      | HatHandling | Germany | 23     |
      | Guest        | Hat     | Express  | MIN200     | 200      | HatHandling | Germany | 35     |
      | Guest        | Hat     | Pickup   | OLD        | 200      | HatHandling | Germany | 41     |
      | Guest        | Hat     | Standard | FREESHIP   | 199      | None        | Israel  | 64     |