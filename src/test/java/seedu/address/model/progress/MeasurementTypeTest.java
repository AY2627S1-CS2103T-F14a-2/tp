package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class MeasurementTypeTest {
    @Test
    public void fromString_validNames_returnsType() {
        for (MeasurementType type : MeasurementType.values()) {
            assertEquals(type, MeasurementType.fromString(type.toString()));
        }
        assertEquals(MeasurementType.WEIGHT, MeasurementType.fromString(" \tWeIgHt\n"));
        assertEquals(MeasurementType.BODYFAT, MeasurementType.fromString(" BODYFAT "));
        assertEquals(MeasurementType.WAIST, MeasurementType.fromString(" Waist "));
    }

    @Test
    public void fromString_invalidNames_throwsIllegalArgumentException() {
        for (String name : new String[]{"", " \t", "height", "body fat", "weight/kg"}) {
            assertThrows(IllegalArgumentException.class,
                    MeasurementType.MESSAGE_CONSTRAINTS, () -> MeasurementType.fromString(name));
        }
    }

    @Test
    public void nullInputs_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> MeasurementType.fromString(null));
        for (MeasurementType type : MeasurementType.values()) {
            assertThrows(NullPointerException.class, () -> type.isValidValue(null));
        }
    }

    @Test
    public void isValidValue_rangeBoundaries() {
        assertRange(MeasurementType.WEIGHT, "0.99", "1", "500", "500.01");
        assertRange(MeasurementType.BODYFAT, "0", "0.01", "100", "100.01");
        assertRange(MeasurementType.WAIST, "0.99", "1", "300", "300.01");
        for (MeasurementType type : MeasurementType.values()) {
            assertFalse(type.isValidValue(new BigDecimal("-1")));
        }
        // Range validation does not enforce the record's separate precision rule.
        assertTrue(MeasurementType.WEIGHT.isValidValue(new BigDecimal("72.555")));
    }

    @Test
    public void unitsAndConstraints_matchSpecification() {
        assertEquals("kg", MeasurementType.WEIGHT.getUnit());
        assertEquals("%", MeasurementType.BODYFAT.getUnit());
        assertEquals("cm", MeasurementType.WAIST.getUnit());
        assertEquals("Weight must be between 1 and 500 kg.", MeasurementType.WEIGHT.getValueConstraints());
        assertEquals("Body-fat percentage must be greater than 0 and no more than 100.",
                MeasurementType.BODYFAT.getValueConstraints());
        assertEquals("Waist measurement must be between 1 and 300 cm.", MeasurementType.WAIST.getValueConstraints());
    }

    private void assertRange(MeasurementType type, String below, String minimum, String maximum, String above) {
        assertFalse(type.isValidValue(new BigDecimal(below)));
        assertTrue(type.isValidValue(new BigDecimal(minimum)));
        assertTrue(type.isValidValue(new BigDecimal(maximum)));
        assertFalse(type.isValidValue(new BigDecimal(above)));
    }
}
