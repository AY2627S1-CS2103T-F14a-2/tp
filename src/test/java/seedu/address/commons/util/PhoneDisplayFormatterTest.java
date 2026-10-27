package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneDisplayFormatterTest {

    @Test
    public void format_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PhoneDisplayFormatter.format(null));
    }

    @Test
    public void format_eightDigits_groupsFourAndFour() {
        assertEquals("9927 2758", PhoneDisplayFormatter.format("99272758"));
    }

    @Test
    public void format_notEightDigits_unchanged() {
        assertEquals("9123-456", PhoneDisplayFormatter.format("9123-456"));
        assertEquals("+65 9123 4567", PhoneDisplayFormatter.format("+65 9123 4567"));
        assertEquals("911", PhoneDisplayFormatter.format("911")); // 3 digits, shortest valid phone
        assertEquals("9927275", PhoneDisplayFormatter.format("9927275")); // 7 digits
        assertEquals("992727581", PhoneDisplayFormatter.format("992727581")); // 9 digits
        assertEquals("123456789012345", PhoneDisplayFormatter.format("123456789012345")); // 15 digits
    }
}
