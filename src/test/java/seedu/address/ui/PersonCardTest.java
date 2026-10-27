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
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;
import seedu.address.model.progress.PerformanceHistory;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the text shown on a client card independently of the JavaFX toolkit.
 */
public class PersonCardTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 25);

    @Test
    public void formatLatestMeasurement_empty_showsDash() {
        assertEquals("Latest measurement: -", PersonCard.formatLatestMeasurement(List.of(), TODAY));
    }

    @Test
    public void formatLatestMeasurement_newestDateWinsRegardlessOfInsertionOrder() {
        Measurement latest = record(DATE, MeasurementType.WEIGHT, "72.40");
        List<Measurement> records = List.of(record(DATE.minusDays(3), MeasurementType.WAIST, "81"), latest,
                record(DATE.minusDays(2), MeasurementType.BODYFAT, "18.4"),
                record(DATE.minusDays(1), MeasurementType.WEIGHT, "73"));
        assertEquals("Latest measurement: 12 Sep - weight 72.4 kg (4 records)",
                PersonCard.formatLatestMeasurement(records, TODAY));
    }

    @Test
    public void formatLatestMeasurement_unitsAndSingularCount() {
        assertEquals("Latest measurement: 12 Sep - bodyfat 18.4 % (1 record)",
                PersonCard.formatLatestMeasurement(List.of(record(DATE, MeasurementType.BODYFAT, "18.4")), TODAY));
        assertEquals("Latest measurement: 12 Sep - waist 81 cm (1 record)",
                PersonCard.formatLatestMeasurement(List.of(record(DATE, MeasurementType.WAIST, "81")), TODAY));
    }

    @Test
    public void formatLatestMeasurement_sameDateTie_isIndependentOfInsertionOrder() {
        Measurement weight = record(DATE, MeasurementType.WEIGHT, "72.4");
        Measurement bodyfat = record(DATE, MeasurementType.BODYFAT, "18.4");
        Measurement waist = record(DATE, MeasurementType.WAIST, "81");
        String expected = "Latest measurement: 12 Sep - weight 72.4 kg (3 records)";
        assertEquals(expected, PersonCard.formatLatestMeasurement(List.of(waist, bodyfat, weight), TODAY));
        assertEquals(expected, PersonCard.formatLatestMeasurement(List.of(weight, bodyfat, waist), TODAY));
        assertEquals("Latest measurement: 12 Sep - bodyfat 18.4 % (2 records)",
                PersonCard.formatLatestMeasurement(List.of(waist, bodyfat), TODAY));
    }

    @Test
    public void formatLatestMeasurement_replacementUpdatesValueWithoutChangingCount() {
        Person client = new PersonBuilder().withMeasurements(record(DATE, MeasurementType.WEIGHT, "72.4")).build();
        Person updated = client.withMeasurement(record(DATE, MeasurementType.WEIGHT, "71.8"));
        assertEquals("Latest measurement: 12 Sep - weight 71.8 kg (1 record)",
                PersonCard.formatLatestMeasurement(updated.getMeasurements(), TODAY));
    }

    @Test
    public void formatLatestPerformance_noRecords_showsDash() {
        assertEquals("Latest performance: -", PersonCard.formatLatestPerformance(new PerformanceHistory(), TODAY));
    }

    @Test
    public void formatLatestMeasurement_deletion_recalculatesLatestTieCountAndEmptyState() {
        Person original = new PersonBuilder().withMeasurements(
                record(DATE.minusDays(1), MeasurementType.WEIGHT, "73"),
                record(DATE, MeasurementType.WAIST, "81"),
                record(DATE, MeasurementType.BODYFAT, "18.4"),
                record(DATE, MeasurementType.WEIGHT, "72.4")).build();
        Person withoutOlder = original.withoutMeasurement(DATE.minusDays(1), MeasurementType.WEIGHT);
        assertEquals("Latest measurement: 12 Sep - weight 72.4 kg (3 records)",
                PersonCard.formatLatestMeasurement(withoutOlder.getMeasurements(), TODAY));
        Person withoutWeight = original.withoutMeasurement(DATE, MeasurementType.WEIGHT);
        assertEquals("Latest measurement: 12 Sep - bodyfat 18.4 % (3 records)",
                PersonCard.formatLatestMeasurement(withoutWeight.getMeasurements(), TODAY));
        Person withoutBodyfat = withoutWeight.withoutMeasurement(DATE, MeasurementType.BODYFAT);
        assertEquals("Latest measurement: 12 Sep - waist 81 cm (2 records)",
                PersonCard.formatLatestMeasurement(withoutBodyfat.getMeasurements(), TODAY));
        Person olderOnly = withoutBodyfat.withoutMeasurement(DATE, MeasurementType.WAIST);
        assertEquals("Latest measurement: 11 Sep - weight 73 kg (1 record)",
                PersonCard.formatLatestMeasurement(olderOnly.getMeasurements(), TODAY));
        assertEquals("Latest measurement: -", PersonCard.formatLatestMeasurement(
                olderOnly.withoutMeasurement(DATE.minusDays(1), MeasurementType.WEIGHT).getMeasurements(), TODAY));
    }

    @Test
    public void formatLatestMeasurement_otherYear_includesYear() {
        assertEquals("Latest measurement: 12 Sep 2024 - weight 80 kg (1 record)", PersonCard.formatLatestMeasurement(
                List.of(record(DATE.withYear(2024), MeasurementType.WEIGHT, "80")), TODAY));
    }

    @Test
    public void formatLatestPerformance_otherYear_includesYear() {
        assertEquals("Latest performance: 15 Sep 2026 - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT)), TODAY.plusYears(1)));
    }

    @Test
    public void formatLatestPerformance_pastYear_includesYear() {
        assertEquals("Latest performance: 15 Sep 2025 - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(squatOn(LocalDate.of(2025, 9, 15)), TODAY));
    }

    @Test
    public void formatLatestPerformance_yearBoundary_includesYearOnlyForPreviousYear() {
        LocalDate newYearsDay = LocalDate.of(2027, 1, 1);
        assertEquals("Latest performance: 31 Dec 2026 - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(squatOn(LocalDate.of(2026, 12, 31)), newYearsDay));
        assertEquals("Latest performance: 1 Jan - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(squatOn(newYearsDay), newYearsDay));
    }

    @Test
    public void formatLatestPerformance_oneRecord_usesSingularCount() {
        assertEquals("Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (1 record)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(SQUAT)), TODAY));
    }

    @Test
    public void formatLatestPerformance_severalRecords_showsLatestAndCount() {
        // the latest date is shown even though an older record was entered last
        assertEquals("Latest performance: 18 Sep - Bench Press, 3 sets of 8 reps at 60 kg (2 records)",
                PersonCard.formatLatestPerformance(new PerformanceHistory(List.of(BENCH_PRESS, SQUAT)), TODAY));

        // records on the same date: the one entered last is shown
        assertEquals("Latest performance: 18 Sep - Pull-up, 1 set of 1 rep at 0 kg (3 records)",
                PersonCard.formatLatestPerformance(
                        new PerformanceHistory(List.of(SQUAT, BENCH_PRESS, PULL_UP)), TODAY));
    }

    private static Measurement record(LocalDate date, MeasurementType type, String value) {
        return new Measurement(date, type, new BigDecimal(value));
    }

    private static PerformanceHistory squatOn(LocalDate date) {
        return new PerformanceHistory(List.of(
                new GymPerformance(date, new ExerciseName("Squat"), 3, 5, new Load("100"))));
    }
}
