package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests measurement validation and attachment to immutable client records.
 */
public class MeasurementTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);

    @Test
    public void constructor_validBoundaries_success() {
        for (String value : List.of("1", "500", "72.40")) {
            new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal(value));
        }
        for (String value : List.of("0.01", "100")) {
            new Measurement(DATE, MeasurementType.BODYFAT, new BigDecimal(value));
        }
        for (String value : List.of("1", "300")) {
            new Measurement(DATE, MeasurementType.WAIST, new BigDecimal(value));
        }
        new Measurement(LocalDate.now(), MeasurementType.WEIGHT, BigDecimal.ONE);
    }

    @Test
    public void constructor_invalidValues_throws() {
        for (MeasurementType type : MeasurementType.values()) {
            for (String value : List.of("-1", "0", "501", "1.001")) {
                assertThrows(IllegalArgumentException.class, () ->
                        new Measurement(DATE, type, new BigDecimal(value)));
            }
        }
        assertThrows(IllegalArgumentException.class, () ->
                        new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("0.99")));
        assertThrows(IllegalArgumentException.class, () ->
                        new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("0.99")));
        assertThrows(IllegalArgumentException.class, () ->
                        new Measurement(DATE, MeasurementType.BODYFAT, new BigDecimal("100.01")));
        assertThrows(IllegalArgumentException.class, () ->
                        new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("300.01")));
        assertThrows(NullPointerException.class, () ->
                        new Measurement(null, MeasurementType.WEIGHT, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> new Measurement(DATE, null, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> new Measurement(DATE, MeasurementType.WEIGHT, null));
    }

    @Test
    public void equals_decimalScaleIgnored() {
        Measurement measurement = new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("72.4"));
        Measurement copy = new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("72.40"));
        assertEquals(measurement, copy);
        assertEquals(measurement.hashCode(), copy.hashCode());
        assertNotEquals(measurement, new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("72.4")));
        assertEquals("72.4", measurement.getValue().toPlainString());
        assertEquals("kg", measurement.getType().getUnit());
        assertEquals(DATE, measurement.getDate());
    }

    @Test
    public void person_measurementsDefensivelyCopiedAndIncludedInEquality() {
        Measurement measurement = new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("72.4"));
        Person original = new PersonBuilder().build();
        List<Measurement> records = new ArrayList<>(List.of(measurement));
        Person person = new Person(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), original.getTags(), records);
        records.clear();
        assertEquals(List.of(measurement), person.getMeasurements());
        assertThrows(UnsupportedOperationException.class, () -> person.getMeasurements().clear());
        assertNotEquals(original, person);
        assertEquals(person, new PersonBuilder(person).build());
        assertEquals(List.of(), original.getMeasurements());
    }
}
