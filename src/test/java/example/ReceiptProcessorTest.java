
package example;

import com.MOA.ReceiptProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiptProcessorTest {

    private final ReceiptProcessor processor = new ReceiptProcessor();

    @Test
    void calculatesReceiptForRegularCustomer() {
        assertEquals("""
                Customer: Alice
                Items: 2
                Subtotal: 30.00
                Discount: 0
                Tax: 2.25
                Total: 32.25""",
                processor.createReceipt(
                        "Alice",
                        List.of("Book|10.00", "Bag|20.00"),
                        false,
                        null));
    }

    @Test
    void appliesMemberDiscountBeforeTax() {
        assertEquals("""
                Customer: Bob
                Items: 1
                Subtotal: 100.00
                Discount: 10.00
                Tax: 6.75
                Total: 96.75""",
                processor.createReceipt(
                        "Bob",
                        List.of("Laptop|100.00"),
                        true,
                        null));
    }

    @Test
    void appliesCouponAfterMemberDiscount() {
        assertEquals("""
                Customer: Ada
                Items: 1
                Subtotal: 10.00
                Discount: 1.45
                Tax: 0.64
                Total: 9.19""",
                processor.createReceipt(
                        "Ada",
                        List.of("Pen|10.00"),
                        true,
                        "SAVE5"));
    }

    @Test
    void usesGuestWhenCustomerIsNullOrBlank() {
        String nullCustomer = processor.createReceipt(
                null, List.of("Pen|10.00"), false, null);

        String blankCustomer = processor.createReceipt(
                "   ", List.of("Pen|10.00"), false, null);

        assertEquals("Customer: Guest", nullCustomer.split("\n")[0]);
        assertEquals("Customer: Guest", blankCustomer.split("\n")[0]);
    }

    @Test
    void ignoresMalformedAndInvalidItemLines() {
        assertEquals("""
                Customer: Guest
                Items: 1
                Subtotal: 5.00
                Discount: 0
                Tax: 0.38
                Total: 5.38""",
                processor.createReceipt(
                        "",
                        List.of(
                                "Book|5.00",
                                "MissingSeparator",
                                "BadPrice|abc",
                                "Negative|-2.00",
                                "NoName|",
                                "  ",
                                null),
                        false,
                        null));
    }

    @Test
    void countsDuplicateItemsSeparately() {
        assertEquals("""
                Customer: Sam
                Items: 2
                Subtotal: 10.00
                Discount: 0
                Tax: 0.75
                Total: 10.75""",
                processor.createReceipt(
                        "Sam",
                        List.of("Pen|5.00", "Pen|5.00"),
                        false,
                        null));
    }

    @Test
    void acceptsZeroPriceAndRoundsTaxHalfUp() {
        assertEquals("""
                Customer: Zoe
                Items: 2
                Subtotal: 0.05
                Discount: 0
                Tax: 0.00
                Total: 0.05""",
                processor.createReceipt(
                        "Zoe",
                        List.of("Free|0", "SmallItem|0.05"),
                        false,
                        null));
    }

    @Test
    void nullItemListProducesAnEmptyReceiptTotal() {
        assertEquals("""
                Customer: Guest
                Items: 0
                Subtotal: 0.00
                Discount: 0
                Tax: 0.00
                Total: 0.00""",
                processor.createReceipt(null, null, false, null));
    }
}
