
# PrestaShop Automated Test System

End-to-end automated system testing for the PrestaShop checkout flow, combining  
**combinatorial test design (CTD)** with **BDD-style UI automation** using  
Cucumber  and Selenium .


---

## 🎥 Example Run

![prestashop_testing_demo](https://image2url.com/r2/default/gifs/1770343310981-02622fd1-2f98-41ce-afbe-fd8a5d7579e4.gif)

You can find a video of a full run in `Deliverables/prestashop_testing_demo.mp4`

---

## 🔍 Overview

The system under test is the PrestaShop checkout process, including:

- 🧺 Adding products to the cart  
- 💳 Checkout flow execution  
- 💰 Verification of final price and applied discounts  

The primary goal is to demonstrate a **structured approach to system-level test
design** and its translation into maintainable automated tests.

---

## 🧠 Test Design

### 🔢 Combinatorial Test Design (CTD)

The checkout logic was modeled using parameters that influence pricing behavior,
such as:

- 👤 Customer type (guest / registered / VIP)
- 📦 Product configuration
- 🚚 Shipping method
- 🏷️ Discount mechanism
- 🌍 Country and tax rules
- 🔢 Quantity and bulk pricing rules

A **pairwise (2-way) coverage** strategy was used to reduce the number of test
cases while preserving interactions between parameters that are most likely to
expose defects.

Pairwise combinations were generated using **ACTS (NIST)**.

You can find the full report about the test design and parameters in `Deliverables/Report.md`

---

### 🧪 Automation with Cucumber & Selenium

Test scenarios are written in **Gherkin** using **Cucumber**, providing a clear  
and readable description of system behavior at the acceptance level.

Each scenario is backed by **Selenium WebDriver** step definitions that:

-   Interact with the PrestaShop UI through the browser
    
-   Perform user actions (navigation, form input, checkout)
    
-   Assert observable system behavior (prices, discounts, UI state)
    

The automation layer is intentionally kept thin:

-   Business logic is expressed in Gherkin
    
-   Selenium steps focus on _how_ actions are executed, not _what_ is being tested
    

This separation helps keep the test suite readable, easier to extend, and aligned  
with the original test design.

---



### 🧩 Scenario Structure

Generated combinations were grouped by **identical execution flow** and mapped
to **Cucumber scenarios**.

Each scenario:
- ▶️ Executes a single checkout flow  
- ✅ Validates different combinations through assertions on price, discount
  visibility, and UI state  

This approach limits scenario duplication and keeps the test suite readable and
maintainable.

---

## 📁 Deliverables

The `Deliverables/` directory contains the full test design artifacts, including:

- 📐 CTD models and parameter definitions
- 🔀 Generated pairwise combinations
- 🧭 Grouping rationale
- 🗺️ Scenario mapping
- 📄 Assignment documentation

These files document the reasoning behind the automated tests and complement the
implementation found in the code.

---

## 🗂️ Repository Structure

- `ACTS/`  
  🔢 CTD models, constraints, and generated test sets

- `Cucumber/`  
  🥒 Gherkin feature files and Selenium step definitions

- `SUT/`  
  🧪 PrestaShop setup used during testing

- `Deliverables/`  
  📁 Test design documentation and supporting materials

---

## 📝 Notes

- 🛍️ PrestaShop is used as the system under test
- 🎯 The automation focuses on correctness of checkout behavior rather than full
  UI coverage
- 🧱 The structure reflects typical QA system testing workflows

---

## 📜 License
PrestaShop is an open-source platform; see its license for details.
