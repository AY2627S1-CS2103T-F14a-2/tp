package seedu.address.model.measurement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BodyMeasurementTest {
    @Test
    public void constructor_validInputs_normalizesFields() {
        BodyMeasurement measurement = new BodyMeasurement(" \t2024-02-29\n", " WeIgHt ", " 72.50 ");
        assertEquals(LocalDate.of(2024, 2, 29), measurement.getDate());
        assertEquals(MeasurementType.WEIGHT, measurement.getType());
        assertEquals(new BigDecimal("72.5"), measurement.getValue());
        assertEquals(LocalDate.now(), new BodyMeasurement(LocalDate.now().toString(), "waist", "81").getDate());
        assertEquals(LocalDate.now().minusDays(1),
                new BodyMeasurement(LocalDate.now().minusDays(1).toString(), "bodyfat", "18.4").getDate());
    }

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BodyMeasurement(null, "weight", "72.5"));
        assertThrows(NullPointerException.class, () -> new BodyMeasurement("2024-02-29", null, "72.5"));
        assertThrows(NullPointerException.class, () -> new BodyMeasurement("2024-02-29", "weight", null));
    }

    @Test
    public void constructor_invalidDate_throwsIllegalArgumentException() {
        for (String date : new String[]{"", " ", "2023-02-29", "2024-04-31", "2024-13-01",
            "2024-01-00", "2024-2-29", "29/02/2024", "2024-02-29T12:00", "+2024-02-29"}) {
            assertThrows(IllegalArgumentException.class,
                    BodyMeasurement.MESSAGE_DATE_FORMAT, () -> new BodyMeasurement(date, "weight", "72.5"));
        }
        assertThrows(IllegalArgumentException.class, BodyMeasurement.MESSAGE_FUTURE_DATE, () ->
                new BodyMeasurement(LocalDate.now().plusDays(1).toString(), "weight", "72.5"));
    }

    @Test
    public void constructor_invalidType_throwsIllegalArgumentException() {
        for (String type : new String[]{"", "height", "body fat"}) {
            assertThrows(IllegalArgumentException.class,
                    MeasurementType.MESSAGE_CONSTRAINTS, () -> new BodyMeasurement("2024-02-29", type, "72.5"));
        }
    }

    @Test
    public void constructor_invalidNumber_throwsIllegalArgumentException() {
        for (String value : new String[]{"", " ", "abc", "NaN", "Infinity", "1e2", "72,5", "7 2", "72kg"}) {
            assertThrows(IllegalArgumentException.class,
                    BodyMeasurement.MESSAGE_NUMERIC_VALUE, () -> new BodyMeasurement("2024-02-29", "weight", value));
        }
    }

    @Test
    public void constructor_invalidPrecision_throwsIllegalArgumentException() {
        for (String value : new String[]{"72.555", "72.500"}) {
            assertThrows(IllegalArgumentException.class,
                    BodyMeasurement.MESSAGE_PRECISION, () -> new BodyMeasurement("2024-02-29", "weight", value));
        }
    }

    @Test
    public void constructor_rangeBoundaries() {
        assertRange(MeasurementType.WEIGHT, "1", "500", "0.99", "500.01");
        assertRange(MeasurementType.BODYFAT, "0.01", "100", "0", "100.01");
        assertRange(MeasurementType.WAIST, "1", "300", "0.99", "300.01");
        for (MeasurementType type : MeasurementType.values()) {
            assertThrows(IllegalArgumentException.class,
                    type.getValueConstraints(), () -> new BodyMeasurement("2024-02-29", type.toString(), "-1"));
        }
    }

    @Test
    public void equalsAndHashCode_useNormalizedValues() {
        BodyMeasurement measurement = new BodyMeasurement("2024-02-29", "weight", "72.5");
        BodyMeasurement equivalent = new BodyMeasurement(" 2024-02-29 ", "WEIGHT", "72.50");
        assertTrue(measurement.equals(measurement));
        assertTrue(measurement.equals(equivalent));
        assertTrue(equivalent.equals(measurement));
        assertEquals(measurement.hashCode(), equivalent.hashCode());
        assertFalse(measurement.equals(null));
        assertFalse(measurement.equals("72.5"));
        assertFalse(measurement.equals(new BodyMeasurement("2024-02-28", "weight", "72.5")));
        assertFalse(measurement.equals(new BodyMeasurement("2024-02-29", "waist", "72.5")));
        assertFalse(measurement.equals(new BodyMeasurement("2024-02-29", "weight", "73")));
        assertEquals(new BodyMeasurement("2024-02-29", "weight", "100").hashCode(),
                new BodyMeasurement("2024-02-29", "weight", "100.00").hashCode());
    }

    @Test
    public void toString_usesPlainDecimalNotation() {
        BodyMeasurement measurement = new BodyMeasurement("2024-02-29", "weight", "100.00");
        assertTrue(measurement.toString().contains("value=100}"));
    }

    private void assertRange(MeasurementType type, String minimum, String maximum, String below, String above) {
        assertEquals(type, new BodyMeasurement("2024-02-29", type.toString(), minimum).getType());
        assertEquals(type, new BodyMeasurement("2024-02-29", type.toString(), maximum).getType());
        assertThrows(IllegalArgumentException.class,
                type.getValueConstraints(), () -> new BodyMeasurement("2024-02-29", type.toString(), below));
        assertThrows(IllegalArgumentException.class,
                type.getValueConstraints(), () -> new BodyMeasurement("2024-02-29", type.toString(), above));
    }
}
