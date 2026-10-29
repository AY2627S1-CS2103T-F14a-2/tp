package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));
        assertThrows(NullPointerException.class, () -> Phone.isTooLong(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "91", "phone", "12 (HP)", "123/456", "123#456", "123\t456"})
    public void constructor_invalidPhone_throwsIllegalArgumentException(String phone) {
        assertFalse(Phone.isValidPhone(phone));
        assertThrows(IllegalArgumentException.class, () -> new Phone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"911", "93121534", "1242938420331234", "+65 9123 4567", "9123-4567",
        "1234 5678 (HP) 1111-3333 (Office)", "123.456 ext 7"})
    public void constructor_validPhone_preservesFormat(String phone) {
        assertTrue(Phone.isValidPhone(phone));
        assertEquals(phone, new Phone(phone).value);
    }

    @ParameterizedTest
    @ValueSource(ints = {49, 50, 51})
    public void isValidPhone_lengthBoundary_enforcesCharacterLimit(int length) {
        String phone = "123" + "a".repeat(length - 3);
        assertEquals(length <= Phone.MAX_LENGTH, Phone.isValidPhone(phone));
        assertEquals(length > Phone.MAX_LENGTH, Phone.isTooLong(phone));
    }

    @Test
    public void constructor_spacing_normalizesStoredValue() {
        assertEquals("93121534", new Phone("  9312  1534  ").value);
        assertEquals("+65 9312 1534 (HP)", new Phone("  +65  9312  1534 (HP)  ").value);
        assertEquals(new Phone("93121534"), new Phone("9312 1534"));
        assertEquals(new Phone("93121534").hashCode(), new Phone("9312 1534").hashCode());
        assertFalse(Phone.isTooLong(" " + "1".repeat(50) + " "));
    }

    @Test
    public void getDigits_formattedNumbers_returnsCompleteSequence() {
        assertEquals("6591234567", new Phone("+65 9123-4567 (HP)").getDigits());
        assertEquals("1234567811113333", new Phone("1234 5678 (HP) 1111-3333 (Office)").getDigits());
        assertEquals("911", new Phone("911").getDigits());
    }

    @Test
    public void equals_sameAndDifferentValues_comparesStoredValue() {
        Phone phone = new Phone("999");
        assertTrue(phone.equals(new Phone("999")));
        assertTrue(phone.equals(phone));
        assertFalse(phone.equals(null));
        assertFalse(phone.equals(5.0f));
        assertFalse(phone.equals(new Phone("995")));
        assertEquals("999", phone.toString());
    }
}
