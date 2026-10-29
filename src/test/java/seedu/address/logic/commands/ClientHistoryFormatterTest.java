package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TrainingRecords.measurement;
import static seedu.address.testutil.TrainingRecords.performance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.PerformanceHistory;
import seedu.address.testutil.PersonBuilder;

/** Tests the text used by the client detail panel's separately styled sections. */
public class ClientHistoryFormatterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 22, 10, 0);
    private static final LocalDate SEP_30 = LocalDate.of(2026, 9, 30);
    private static final LocalDate OCT_01 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_12 = LocalDate.of(2026, 10, 12);
    private static final LocalDate OCT_15 = LocalDate.of(2026, 10, 15);

    @Test
    public void formatDetails_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClientHistoryFormatter.formatDetails(null, NOW));
        assertThrows(NullPointerException.class, () ->
                ClientHistoryFormatter.formatDetails(new PersonBuilder().build(), null));
    }

    @Test
    public void formatDetails_fullClient_showsContactSessionAndSortedTags() {
        Person alex = new PersonBuilder().withName("Alex Tan").withPhone("91234567").withEmail("alex@example.com")
                .withNextSession("2026-10-23 18:00").withTags("rehab", "knee")
                .build();

        String expected = "Phone: 9123 4567\n"
                + "Email: alex@example.com\n"
                + "Next session: Fri 23 Oct, 6:00pm\n"
                + "Tags: knee, rehab";
        assertEquals(expected, ClientHistoryFormatter.formatDetails(alex, NOW));
    }

    @Test
    public void formatDetails_mixedCaseTags_sortsAlphabeticallyWithDeterministicCaseTies() {
        Person person = new PersonBuilder().withTags("Zebra", "apple", "Apple", "banana").build();
        String details = ClientHistoryFormatter.formatDetails(person, NOW);
        assertEquals("Tags: Apple, apple, banana, Zebra", details.substring(details.lastIndexOf("Tags: ")));
    }

    @Test
    public void formatDetails_noEmailNoTagsNoSession_showsPlaceholders() {
        Person person = new PersonBuilder().withName("Bea Lim").withPhone("911").withoutEmail().build();

        String expected = "Phone: 911\n"
                + "Email: -\n"
                + "Next session: - (none booked)\n"
                + "Tags: -";
        assertEquals(expected, ClientHistoryFormatter.formatDetails(person, NOW));
    }

    @Test
    public void formatDetails_pastSession_matchesCardText() {
        Person person = new PersonBuilder().withName("Bea Lim").withPhone("911").withoutEmail()
                .withNextSession("2026-10-01 09:00").build();

        String expected = "Phone: 911\n"
                + "Email: -\n"
                + "Next session: - (none upcoming)\n"
                + "Tags: -";
        assertEquals(expected, ClientHistoryFormatter.formatDetails(person, NOW));
    }

    @Test
    public void formatSectionHeading_recordCounts_usesSingularOnlyForOne() {
        assertEquals("Measurements · 0 records", ClientHistoryFormatter.formatSectionHeading("Measurements", 0));
        assertEquals("Measurements · 1 record", ClientHistoryFormatter.formatSectionHeading("Measurements", 1));
        assertEquals("Performance · 2 records", ClientHistoryFormatter.formatSectionHeading("Performance", 2));
    }

    @Test
    public void formatSectionHeading_nullTitle_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClientHistoryFormatter.formatSectionHeading(null, 1));
    }

    @Test
    public void formatMeasurementRecords_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClientHistoryFormatter.formatMeasurementRecords(null));
    }

    @Test
    public void formatMeasurementRecords_noRecords_showsEmptyState() {
        assertEquals("None recorded yet.", ClientHistoryFormatter.formatMeasurementRecords(List.of()));
    }

    @Test
    public void formatMeasurementRecords_oneRecord_showsDateTypeValueAndUnit() {
        assertEquals("2026-09-30  bodyfat 18.5 %",
                ClientHistoryFormatter.formatMeasurementRecords(
                        List.of(measurement(SEP_30, MeasurementType.BODYFAT, "18.5"))));
    }

    @Test
    public void formatMeasurementRecords_sameDate_ordersWeightBodyFatWaist() {
        List<Measurement> records = List.of(measurement(OCT_12, MeasurementType.WAIST, "81"),
                measurement(OCT_12, MeasurementType.BODYFAT, "18"),
                measurement(OCT_12, MeasurementType.WEIGHT, "72"));

        assertEquals("2026-10-12  weight 72 kg\n"
                + "2026-10-12  bodyfat 18 %\n"
                + "2026-10-12  waist 81 cm", ClientHistoryFormatter.formatMeasurementRecords(records));
    }

    @Test
    public void formatMeasurementRecords_differentDates_newestDateFirst() {
        List<Measurement> records = List.of(measurement(SEP_30, MeasurementType.WEIGHT, "73.0"),
                measurement(OCT_12, MeasurementType.WEIGHT, "72.4"));

        assertEquals("2026-10-12  weight 72.4 kg\n2026-09-30  weight 73 kg",
                ClientHistoryFormatter.formatMeasurementRecords(records));
    }

    @Test
    public void formatPerformanceRecords_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClientHistoryFormatter.formatPerformanceRecords(null));
    }

    @Test
    public void formatPerformanceRecords_noRecords_showsEmptyState() {
        assertEquals("None recorded yet.", ClientHistoryFormatter.formatPerformanceRecords(new PerformanceHistory()));
    }

    @Test
    public void formatPerformanceRecords_oneRecord_showsDateAndSummary() {
        assertEquals("2026-10-01  Pull-up, 1 set of 1 rep at 0 kg",
                ClientHistoryFormatter.formatPerformanceRecords(
                        new PerformanceHistory(List.of(performance(OCT_01, "Pull-up", 1, 1, "0")))));
    }

    @Test
    public void formatPerformanceRecords_manyRecordsOutOfOrder_newestDateFirst() {
        List<GymPerformance> records = List.of(performance(OCT_12, "Deadlift", 1, 5, "140"),
                performance(SEP_30, "Row", 3, 10, "40"),
                performance(OCT_15, "Squat", 3, 5, "100"),
                performance(OCT_01, "Bench Press", 3, 8, "60"));

        assertEquals("2026-10-15  Squat, 3 sets of 5 reps at 100 kg\n"
                + "2026-10-12  Deadlift, 1 set of 5 reps at 140 kg\n"
                + "2026-10-01  Bench Press, 3 sets of 8 reps at 60 kg\n"
                + "2026-09-30  Row, 3 sets of 10 reps at 40 kg",
                ClientHistoryFormatter.formatPerformanceRecords(new PerformanceHistory(records)));
    }

    @Test
    public void formatPerformanceRecords_sameDate_mostRecentlyRecordedFirst() {
        // Recorded order is deliberately not alphabetical, and an older record sits between same-date ones.
        List<GymPerformance> records = List.of(performance(OCT_15, "Bench Press", 3, 8, "60"),
                performance(OCT_01, "Row", 3, 10, "40"),
                performance(OCT_15, "squat", 3, 5, "100"),
                performance(OCT_15, "Arm Curl", 3, 12, "10"));

        assertEquals("2026-10-15  Arm Curl, 3 sets of 12 reps at 10 kg\n"
                + "2026-10-15  squat, 3 sets of 5 reps at 100 kg\n"
                + "2026-10-15  Bench Press, 3 sets of 8 reps at 60 kg\n"
                + "2026-10-01  Row, 3 sets of 10 reps at 40 kg",
                ClientHistoryFormatter.formatPerformanceRecords(new PerformanceHistory(records)));
    }

    @Test
    public void formatPerformanceRecords_sameDate_firstLineMatchesLatestPerformance() {
        Person person = new PersonBuilder().withGymPerformances(performance(OCT_15, "Squat", 3, 5, "100"),
                performance(OCT_15, "Bench Press", 3, 8, "60")).build();
        GymPerformance latest = person.getPerformanceHistory().getLatest().orElseThrow();

        String firstLine = ClientHistoryFormatter.formatPerformanceRecords(
                person.getPerformanceHistory()).split("\n")[0];
        assertEquals(latest.getDate() + "  " + latest.getSummary(), firstLine);
    }
}
