package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // too long

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // maximum length
        assertTrue(Name.isValidName(" " + "a".repeat(Name.MAX_LENGTH - 2) + "  b ")); // within limit once collapsed
    }

    @ParameterizedTest
    @ValueSource(strings = {"Raju s/o Kumar", "Mary-Jane O'Brien", "José Tan", "Dr. Lim", "O’Brien",
        "Jose\u0301 Tan", "R2 D2"})
    public void constructor_realisticName_acceptsName(String name) {
        assertEquals(name, new Name(name).fullName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-Bob", "'Bob", "/Bob", "\u0301Bob", "王明", "Bob@Tan"})
    public void constructor_invalidNameCharacters_rejectsName(String name) {
        assertFalse(Name.isValidName(name));
        assertThrows(IllegalArgumentException.class, () -> new Name(name));
    }

    @ParameterizedTest
    @ValueSource(ints = {99, 100, 101})
    public void isValidName_lengthBoundary_enforcesLimit(int length) {
        assertEquals(length <= Name.MAX_LENGTH, Name.isValidName("a".repeat(length)));
    }

    @Test
    public void isTooLong() {
        assertFalse(Name.isTooLong("a".repeat(Name.MAX_LENGTH))); // maximum length
        assertFalse(Name.isTooLong("a".repeat(Name.MAX_LENGTH) + "   ")); // within limit once trimmed
        assertTrue(Name.isTooLong("a".repeat(Name.MAX_LENGTH + 1)));
    }

    @Test
    public void constructor_extraSpaces_normalizesName() {
        assertEquals("John Tan", new Name("  John    Tan ").fullName);
    }

    @Test
    public void isSameName() {
        Name name = new Name("John Doe");

        // same value -> returns true
        assertTrue(name.isSameName(new Name("John Doe")));

        // different case -> returns true
        assertTrue(name.isSameName(new Name("john DOE")));

        // repeated internal spaces -> returns true
        assertTrue(name.isSameName(new Name("John   Doe")));

        // trailing spaces -> returns true
        assertTrue(name.isSameName(new Name("John Doe  ")));

        // similar but different name -> returns false
        assertFalse(name.isSameName(new Name("John Doey")));

        // null -> returns false
        assertFalse(name.isSameName(null));
    }

    @Test
    public void matches_rawNames_followDuplicateDetectionRules() {
        Name name = new Name("Sarah   Lim");
        assertTrue(name.matches("Sarah Lim"));
        assertTrue(name.matches("  sArAh\t lIm  "));
        assertFalse(name.matches("Sarah"));
        assertFalse(name.matches("Sarah Lime"));
        assertFalse(name.matches("Sarah/Lim"));
        assertFalse(name.matches(" "));
        assertFalse(name.matches(null));
    }

    @Test
    public void normalizeSpaces_preservesCaseAndCollapsesWhitespace() {
        assertEquals("Sarah Lim", Name.normalizeSpaces(" \tSarah   Lim\t "));
        assertEquals("", Name.normalizeSpaces(" \t "));
        assertThrows(NullPointerException.class, () -> Name.normalizeSpaces(null));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
