
package com.MOA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReceiptProcessor {

    private static final BigDecimal MEMBER_RATE =
            new BigDecimal("0.10");
    private static final BigDecimal COUPON_RATE =
            new BigDecimal("0.05");
    private static final BigDecimal TAX_RATE =
            new BigDecimal("0.075");

    public String createReceipt(
            String customer,
            List<String> itemLines,
            boolean member,
            String coupon) {

        String customerName = normalizeCustomer(customer);
        BigDecimal subtotal = BigDecimal.ZERO;
        int itemCount = 0;

        if (itemLines != null) {
            for (String line : itemLines) {
                BigDecimal price = parseItemPrice(line);

                if (price != null) {
                    subtotal = subtotal.add(price);
                    itemCount++;
                }
            }
        }

        subtotal = money(subtotal);

        BigDecimal discount =
                calculateDiscount(subtotal, member, coupon);

        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal tax = calculateTax(taxableAmount);
        BigDecimal total = money(taxableAmount.add(tax));

        return formatReceipt(
                customerName, itemCount, subtotal, discount, tax, total);
    }

    private String normalizeCustomer(String customer) {
        if (customer == null || customer.isBlank()) {
            return "Guest";
        }
        return customer.trim();
    }

    private BigDecimal parseItemPrice(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 2) {
            return null;
        }

        String itemName = parts[0].trim();
        String priceText = parts[1].trim();

        if (itemName.isEmpty() || priceText.isEmpty()) {
            return null;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            return price.compareTo(BigDecimal.ZERO) < 0
                    ? null : price;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private BigDecimal calculateDiscount(
            BigDecimal subtotal, boolean member, String coupon) {

        BigDecimal discount = BigDecimal.ZERO;

        if (member) {
            discount = money(subtotal.multiply(MEMBER_RATE));
        }

        BigDecimal remaining = subtotal.subtract(discount);

        if ("SAVE5".equals(coupon)) {
            BigDecimal couponDiscount =
                    money(remaining.multiply(COUPON_RATE));
            discount = discount.add(couponDiscount);
        }

        return discount;
    }

    private BigDecimal calculateTax(BigDecimal taxableAmount) {
        return money(taxableAmount.multiply(TAX_RATE));
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatReceipt(
            String customer, int itemCount,
            BigDecimal subtotal, BigDecimal discount,
            BigDecimal tax, BigDecimal total) {

        return new StringBuilder()
                .append("Customer: ").append(customer).append("\n")
                .append("Items: ").append(itemCount).append("\n")
                .append("Subtotal: ").append(subtotal.toPlainString()).append("\n")
                .append("Discount: ").append(discount.toPlainString()).append("\n")
                .append("Tax: ").append(tax.toPlainString()).append("\n")
                .append("Total: ").append(total.toPlainString())
                .toString();
    }
}
