package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalGymPerformances.PULL_UP;
import static seedu.address.testutil.TypicalGymPerformances.SQUAT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.model.progress.PerformanceHistory;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the text shown on a client card independently of the JavaFX toolkit.
 */
public class PersonCardTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);

    @Test
    public void formatLatestMeasurement_empty_showsDash() {
        assertEquals("Latest measurement: -", PersonCard.formatLatestMeasurement(List.of()));
    }

    @Test
    public void formatLatestMeasurement_newestDateWinsRegardlessOfInsertionOrder() {
        Measurement latest = record(DATE, MeasurementType.WEIGHT, "72.40");
        List<Measurement> records = List.of(record(DATE.minusDays(3), MeasurementType.WAIST, "81"), latest,
                record(DATE.minusDays(2), MeasurementType.BODYFAT, "18.4"),
                record(DATE.minusDays(1), MeasurementType.WEIGHT, "73"));
        assertEquals("Latest measurement: 12 Sep - weight 72.4 kg (4 records)",
                PersonCard.formatLatestMeasurement(records));
    }

    @Test
    public void formatLatestMeasurement_unitsAndSingularCount() {
        assertEquals("Latest measurement: 12 Sep - bodyfat 18.4 % (1 record)",
                PersonCard.formatLatestMeasurement(List.of(record(DATE, MeasurementType.BODYFAT, "18.4"))));
        assertEquals("Latest measurement: 12 Sep - waist 81 cm (1 record)",
                PersonCard.formatLatestMeasurement(List.of(record(DATE, MeasurementType.WAIST, "81"))));
    }

    @Test
    public void formatLatestMeasurement_sameDateTie_isIndependentOfInsertionOrder() {
        Measurement weight = record(DATE, MeasurementType.WEIGHT, "72.4");
        Measurement bodyfat = record(DATE, MeasurementType.BODYFAT, "18.4");
        Measurement waist = record(DATE, MeasurementType.WAIST, "81");
        String expected = "Latest measurement: 12 Sep - weight 72.4 kg (3 records)";
        assertEquals(expected, PersonCard.formatLatestMeasurement(List.of(waist, bodyfat, weight)));
        assertEquals(expected, PersonCard.formatLatestMeasurement(List.of(weight, bodyfat, waist)));
        assertEquals("Latest measurement: 12 Sep - bodyfat 18.4 % (2 records)",
                PersonCard.formatLatestMeasurement(List.of(waist, bodyfat)));
    }

    @Test
    public void formatLatestMeasurement_replacementUpdatesValueWithoutChangingCount() {
        Person client = new PersonBuilder().withMeasurements(record(DATE, MeasurementType.WEIGHT, "72.4")).build();
        Person updated = client.withMeasurement(record(DATE, MeasurementType.WEIGHT, "71.8"));
        assertEquals("Latest measurement: 12 Sep - weight 71.8 kg (1 record)",
                PersonCard.formatLatestMeasurement(updated.getMeasurements()));
    }

    @Test
    public void formatLatestPerformance_noRecords_showsDash() {
        assertEquals("Latest performance: -", PersonCard.formatLatestPerformance(new PerformanceHistory()));
    }

    @Test
    public void formatLatestPerformance_oneRecord_usesSingularCount() {
        assertEquals("Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT))));
    }

    @Test
    public void formatLatestPerformance_severalRecords_showsLatestAndCount() {
        // the latest date is shown even though an older record was entered last
        assertEquals("Latest performance: 18 Sep - Bench Press, 3 sets of 8 reps at 60 kg (2 records)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(BENCH_PRESS, SQUAT))));

        // records on the same date: the one entered last is shown
        assertEquals("Latest performance: 18 Sep - Pull-up, 1 set of 1 rep at 0 kg (3 records)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT, BENCH_PRESS, PULL_UP))));
    }

    private static Measurement record(LocalDate date, MeasurementType type, String value) {
        return new Measurement(date, type, new BigDecimal(value));
    }
}
