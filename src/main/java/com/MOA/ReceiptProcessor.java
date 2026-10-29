
package com.MOA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReceiptProcessor {

    public String createReceipt(
            String customer,
            List<String> itemLines,
            boolean member,
            String coupon) {

        String customerName = customer;
        if (customerName == null || customerName.isBlank()) {
            customerName = "Guest";
        } else {
            customerName = customerName.trim();
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        int itemCount = 0;

        if (itemLines != null) {
            for (String line : itemLines) {
                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length != 2) {
                    continue;
                }

                String itemName = parts[0].trim();
                String priceText = parts[1].trim();

                if (itemName.isEmpty() || priceText.isEmpty()) {
                    continue;
                }

                BigDecimal price;
                try {
                    price = new BigDecimal(priceText);
                } catch (NumberFormatException exception) {
                    continue;
                }

                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    continue;
                }

                subtotal = subtotal.add(price);
                itemCount++;
            }
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = BigDecimal.ZERO;

        if (member) {
            discount = subtotal.multiply(new BigDecimal("0.10"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal afterMemberDiscount = subtotal.subtract(discount);

        if ("SAVE5".equals(coupon)) {
            BigDecimal couponDiscount = afterMemberDiscount
                    .multiply(new BigDecimal("0.05"))
                    .setScale(2, RoundingMode.HALF_UP);

            discount = discount.add(couponDiscount);
        }

        BigDecimal taxableAmount = subtotal.subtract(discount);

        BigDecimal tax = taxableAmount
                .multiply(new BigDecimal("0.075"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = taxableAmount.add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        StringBuilder receipt = new StringBuilder();
        receipt.append("Customer: ").append(customerName).append("\n");
        receipt.append("Items: ").append(itemCount).append("\n");
        receipt.append("Subtotal: ").append(subtotal.toPlainString()).append("\n");
        receipt.append("Discount: ").append(discount.toPlainString()).append("\n");
        receipt.append("Tax: ").append(tax.toPlainString()).append("\n");
        receipt.append("Total: ").append(total.toPlainString());

        return receipt.toString();
    }
}
