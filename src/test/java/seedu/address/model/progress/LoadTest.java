package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LoadTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Load(null));
    }

    @Test
    public void constructor_outOfRange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Load.MESSAGE_RANGE_CONSTRAINTS, () -> new Load("1000.01"));
    }

    @Test
    public void constructor_tooManyDecimalPlaces_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Load.MESSAGE_DECIMAL_PLACES_CONSTRAINTS, () -> new Load("60.555"));
    }

    @Test
    public void isNumberWithinRange() {
        // null load
        assertThrows(NullPointerException.class, () -> Load.isNumberWithinRange(null));

        // not a plain number
        assertFalse(Load.isNumberWithinRange("")); // empty string
        assertFalse(Load.isNumberWithinRange(" ")); // spaces only
        assertFalse(Load.isNumberWithinRange("abc")); // letters
        assertFalse(Load.isNumberWithinRange("60kg")); // unit included
        assertFalse(Load.isNumberWithinRange("-1")); // negative sign
        assertFalse(Load.isNumberWithinRange("+5")); // positive sign
        assertFalse(Load.isNumberWithinRange("1e2")); // scientific notation
        assertFalse(Load.isNumberWithinRange(".5")); // no digit before decimal point
        assertFalse(Load.isNumberWithinRange("5.")); // no digit after decimal point

        // out of range
        assertFalse(Load.isNumberWithinRange("1000.01")); // just above maximum
        assertFalse(Load.isNumberWithinRange("1001"));

        // within range
        assertTrue(Load.isNumberWithinRange("0")); // minimum
        assertTrue(Load.isNumberWithinRange("0.00")); // minimum with decimal places
        assertTrue(Load.isNumberWithinRange("60"));
        assertTrue(Load.isNumberWithinRange("62.5"));
        assertTrue(Load.isNumberWithinRange("1000")); // maximum
        assertTrue(Load.isNumberWithinRange("1000.00")); // maximum with decimal places
    }

    @Test
    public void hasValidDecimalPlaces() {
        // null load
        assertThrows(NullPointerException.class, () -> Load.hasValidDecimalPlaces(null));

        // too many decimal places
        assertFalse(Load.hasValidDecimalPlaces("60.555"));
        assertFalse(Load.hasValidDecimalPlaces("60.500")); // trailing zeros still count

        // valid decimal places
        assertTrue(Load.hasValidDecimalPlaces("60")); // none
        assertTrue(Load.hasValidDecimalPlaces("60.5")); // one
        assertTrue(Load.hasValidDecimalPlaces("60.55")); // two
    }

    @Test
    public void isValidLoad() {
        assertFalse(Load.isValidLoad("abc"));
        assertFalse(Load.isValidLoad("1000.01"));
        assertFalse(Load.isValidLoad("60.555"));

        assertTrue(Load.isValidLoad("0"));
        assertTrue(Load.isValidLoad("60.25"));
    }

    @Test
    public void equals() {
        Load load = new Load("60");

        // same values -> returns true
        assertTrue(load.equals(new Load("60")));

        // same object -> returns true
        assertTrue(load.equals(load));

        // null -> returns false
        assertFalse(load.equals(null));

        // different types -> returns false
        assertFalse(load.equals(5.0f));

        // same number with different decimal places -> returns true
        assertTrue(load.equals(new Load("60.0")));
        assertEquals(load.hashCode(), new Load("60.00").hashCode());

        // different values -> returns false
        assertFalse(load.equals(new Load("60.5")));
    }

    @Test
    public void toString_removesTrailingZeros() {
        assertEquals("60", new Load("60.00").toString());
        assertEquals("62.5", new Load("62.50").toString());
        assertEquals("0", new Load("0.00").toString());
        assertEquals("1000", new Load("1000").toString());
    }
}
