
# Java Long Method Refactoring

## Overview

This project demonstrates characterization testing and
incremental refactoring of a long Java method.

The original `createReceipt()` method handled customer
normalization, item parsing, validation, discount calculation,
tax calculation, and receipt formatting in one place.

## Technologies

- Java 17
- Maven
- JUnit 5
- Git

## Run the tests

```bash
mvn clean test
```

## Refactoring decisions

1. Customer normalization was extracted to isolate fallback
   and whitespace handling.
2. Item parsing was extracted to centralize validation and
   preserve the handling of malformed records.
3. Discount calculation was extracted to make the order of
   member and coupon discounts explicit.
4. Tax calculation and money rounding were extracted to
   isolate financial calculations.
5. Receipt formatting was extracted to separate presentation
   from business logic.

## Behavior preservation

The characterization tests cover normal receipts, member
discounts, coupon discounts, blank customer names, malformed
items, duplicate items, zero-priced items, rounding, and
null input.

Existing behavior was preserved, including case-sensitive
coupon matching and the original output formatting.

## Commit strategy

The test baseline was committed before refactoring.
Each extraction was committed separately after running
the test suite.

## Lessons learned

Characterization tests help make legacy code safer to change.
Small refactoring steps make regressions easier to identify
and code review easier to understand.
