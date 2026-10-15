package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Tests creation and replacement of measurements without mutating existing clients.
 */
public class PersonMeasurementTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);
    private static final Measurement WEIGHT = new Measurement(DATE, MeasurementType.WEIGHT,
            new BigDecimal("72.4"));

    @Test
    public void withMeasurement_firstRecord_preservesClientDetails() {
        Person original = new PersonBuilder().withTags("fictional").build();
        Person recorded = original.withMeasurement(WEIGHT);
        assertEquals(new PersonBuilder(original).withMeasurements(WEIGHT).build(), recorded);
        assertTrue(recorded.isSamePerson(original));
        assertEquals(List.of(), original.getMeasurements());
        assertFalse(original.hasMeasurement(WEIGHT));
        assertTrue(recorded.hasMeasurement(WEIGHT));
    }

    @Test
    public void withMeasurement_differentDateOrType_addsRecord() {
        Measurement olderWeight = new Measurement(DATE.minusDays(1), MeasurementType.WEIGHT,
                new BigDecimal("73"));
        Measurement waist = new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("81"));
        Person original = new PersonBuilder().withMeasurements(WEIGHT).build();
        assertFalse(original.hasMeasurement(olderWeight));
        assertFalse(original.hasMeasurement(waist));
        Person recorded = original.withMeasurement(olderWeight).withMeasurement(waist);
        assertEquals(List.of(WEIGHT, olderWeight, waist), recorded.getMeasurements());
        assertEquals(List.of(WEIGHT), original.getMeasurements());
    }

    @Test
    public void withMeasurement_sameDateAndType_replacesValueOnly() {
        Measurement waist = new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("81"));
        Measurement replacement = new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("71"));
        Person original = new PersonBuilder().withMeasurements(waist, WEIGHT).build();
        assertTrue(WEIGHT.isSameMeasurement(replacement));
        assertTrue(original.hasMeasurement(replacement));
        Person updated = original.withMeasurement(replacement);
        assertEquals(new PersonBuilder(original).withMeasurements(waist, replacement).build(), updated);
        assertEquals(2, updated.getMeasurements().size());
        assertEquals(List.of(waist, WEIGHT), original.getMeasurements());
        assertEquals(updated, updated.withMeasurement(replacement));
    }

    @Test
    public void measurementIdentity_ignoresValueButRequiresDateAndType() {
        assertTrue(WEIGHT.isSameMeasurement(WEIGHT));
        assertFalse(WEIGHT.isSameMeasurement(null));
        assertFalse(WEIGHT.isSameMeasurement(new Measurement(DATE.minusDays(1), MeasurementType.WEIGHT,
                new BigDecimal("72.4"))));
        assertFalse(WEIGHT.isSameMeasurement(new Measurement(DATE, MeasurementType.WAIST,
                new BigDecimal("72.4"))));
    }

    @Test
    public void nullMeasurement_throwsWithoutChangingClient() {
        Person original = new PersonBuilder().withMeasurements(WEIGHT).build();
        assertThrows(NullPointerException.class, () -> original.withMeasurement(null));
        assertThrows(NullPointerException.class, () -> original.hasMeasurement(null));
        assertEquals(List.of(WEIGHT), original.getMeasurements());
    }
}
