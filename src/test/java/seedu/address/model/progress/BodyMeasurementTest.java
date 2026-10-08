package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BodyMeasurementTest {
    private static final LocalDate DATE = LocalDate.of(2024, 2, 29);

    @Test
    public void constructor_validInputs_normalizesValue() {
        BodyMeasurement measurement = new BodyMeasurement(DATE, MeasurementType.WEIGHT, new BigDecimal("72.500"));
        assertEquals(DATE, measurement.getDate());
        assertEquals(MeasurementType.WEIGHT, measurement.getType());
        assertEquals(new BigDecimal("72.5"), measurement.getValue());
    }

    @Test
    public void constructor_futureDate_preservesStoredRecord() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        BodyMeasurement measurement = new BodyMeasurement(futureDate, MeasurementType.WEIGHT, new BigDecimal("72.5"));
        assertEquals(futureDate, measurement.getDate());
    }

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BodyMeasurement(null, MeasurementType.WEIGHT,
                new BigDecimal("72.5")));
        assertThrows(NullPointerException.class, () -> new BodyMeasurement(DATE, null, new BigDecimal("72.5")));
        assertThrows(NullPointerException.class, () -> new BodyMeasurement(DATE, MeasurementType.WEIGHT, null));
    }

    @Test
    public void constructor_invalidPrecision_throwsIllegalArgumentException() {
        for (String value : new String[]{"72.555", "72.5010"}) {
            assertThrows(IllegalArgumentException.class, BodyMeasurement.MESSAGE_PRECISION, () ->
                    measurement(MeasurementType.WEIGHT, value));
        }
    }

    @Test
    public void constructor_rangeBoundaries() {
        assertRange(MeasurementType.WEIGHT, "1", "500", "0.99", "500.01");
        assertRange(MeasurementType.BODYFAT, "0.01", "100", "0", "100.01");
        assertRange(MeasurementType.WAIST, "1", "300", "0.99", "300.01");
        for (MeasurementType type : MeasurementType.values()) {
            assertThrows(IllegalArgumentException.class, type.getValueConstraints(), () -> measurement(type, "-1"));
        }
    }

    @Test
    public void equalsAndHashCode_useNormalizedValues() {
        BodyMeasurement measurement = measurement(MeasurementType.WEIGHT, "72.5");
        BodyMeasurement equivalent = measurement(MeasurementType.WEIGHT, "72.500");
        assertTrue(measurement.equals(measurement));
        assertTrue(measurement.equals(equivalent));
        assertTrue(equivalent.equals(measurement));
        assertEquals(measurement.hashCode(), equivalent.hashCode());
        assertFalse(measurement.equals(null));
        assertFalse(measurement.equals("72.5"));
        assertFalse(measurement.equals(new BodyMeasurement(DATE.minusDays(1), MeasurementType.WEIGHT,
                new BigDecimal("72.5"))));
        assertFalse(measurement.equals(measurement(MeasurementType.WAIST, "72.5")));
        assertFalse(measurement.equals(measurement(MeasurementType.WEIGHT, "73")));
        assertEquals(measurement(MeasurementType.WEIGHT, "100").hashCode(),
                measurement(MeasurementType.WEIGHT, "100.000").hashCode());
    }

    @Test
    public void toString_usesPlainDecimalNotation() {
        assertTrue(measurement(MeasurementType.WEIGHT, "100.00").toString().contains("value=100}"));
    }

    private BodyMeasurement measurement(MeasurementType type, String value) {
        return new BodyMeasurement(DATE, type, new BigDecimal(value));
    }

    private void assertRange(MeasurementType type, String minimum, String maximum, String below, String above) {
        assertEquals(type, measurement(type, minimum).getType());
        assertEquals(type, measurement(type, maximum).getType());
        assertThrows(IllegalArgumentException.class, type.getValueConstraints(), () -> measurement(type, below));
        assertThrows(IllegalArgumentException.class, type.getValueConstraints(), () -> measurement(type, above));
    }
}
