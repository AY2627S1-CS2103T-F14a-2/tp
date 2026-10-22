package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.ClientHistoryFormatter.NO_MEASUREMENTS;
import static seedu.address.logic.commands.ClientHistoryFormatter.NO_PERFORMANCES;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

public class ClientHistoryFormatterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 22, 10, 0);
    private static final LocalDate SEP_30 = LocalDate.of(2026, 9, 30);
    private static final LocalDate OCT_01 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_12 = LocalDate.of(2026, 10, 12);
    private static final LocalDate OCT_15 = LocalDate.of(2026, 10, 15);

    private static final String NO_RECORDS_SUFFIX = "\n\n" + NO_MEASUREMENTS + "\n\n" + NO_PERFORMANCES;

    @Test
    public void format_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ClientHistoryFormatter.format(null, NOW));
        assertThrows(NullPointerException.class, () ->
                ClientHistoryFormatter.format(new PersonBuilder().build(), null));
    }

    @Test
    public void format_fullClient_showsDetailsAndHistoryNewestFirst() {
        Person alex = new PersonBuilder().withName("Alex Tan").withPhone("91234567").withEmail("alex@example.com")
                .withNextSession("2026-10-23 18:00").withTags("rehab", "knee")
                .withMeasurements(measurement(SEP_30, MeasurementType.WEIGHT, "73.0"),
                        measurement(OCT_12, MeasurementType.WAIST, "81"),
                        measurement(OCT_12, MeasurementType.WEIGHT, "72.4"))
                .withGymPerformances(performance(OCT_01, "Bench Press", 3, 8, "60"),
                        performance(OCT_15, "Squat", 3, 5, "100"))
                .build();

        String expected = "Alex Tan\n"
                + "Phone: 9123 4567\n"
                + "Email: alex@example.com\n"
                + "Next session: Fri 23 Oct, 6:00pm\n"
                + "Tags: knee, rehab\n"
                + "\n"
                + "Measurements (3 records, newest first):\n"
                + "2026-10-12  weight 72.4 kg\n"
                + "2026-10-12  waist 81 cm\n"
                + "2026-09-30  weight 73 kg\n"
                + "\n"
                + "Performance (2 records, newest first):\n"
                + "2026-10-15  Squat, 3 sets of 5 reps at 100 kg\n"
                + "2026-10-01  Bench Press, 3 sets of 8 reps at 60 kg";
        assertEquals(expected, ClientHistoryFormatter.format(alex, NOW));
    }

    @Test
    public void format_noEmailNoTagsNoSessionNoRecords_showsPlaceholders() {
        Person person = new PersonBuilder().withName("Bea Lim").withPhone("911").withoutEmail().build();

        String expected = "Bea Lim\n"
                + "Phone: 911\n"
                + "Email: -\n"
                + "Next session: - (none booked)\n"
                + "Tags: -"
                + NO_RECORDS_SUFFIX;
        assertEquals(expected, ClientHistoryFormatter.format(person, NOW));
    }

    @Test
    public void format_pastSession_matchesCardText() {
        Person person = new PersonBuilder().withName("Bea Lim").withPhone("911").withoutEmail()
                .withNextSession("2026-10-01 09:00").build();

        String expected = "Bea Lim\n"
                + "Phone: 911\n"
                + "Email: -\n"
                + "Next session: - (none upcoming)\n"
                + "Tags: -"
                + NO_RECORDS_SUFFIX;
        assertEquals(expected, ClientHistoryFormatter.format(person, NOW));
    }

    @Test
    public void formatMeasurements_noRecords_showsEmptyState() {
        assertEquals(NO_MEASUREMENTS, ClientHistoryFormatter.formatMeasurements(List.of()));
    }

    @Test
    public void formatMeasurements_oneRecord_usesSingularHeader() {
        assertEquals("Measurements (1 record, newest first):\n2026-09-30  bodyfat 18.5 %",
                ClientHistoryFormatter.formatMeasurements(
                        List.of(measurement(SEP_30, MeasurementType.BODYFAT, "18.5"))));
    }

    @Test
    public void formatMeasurements_sameDate_ordersWeightBodyFatWaist() {
        List<Measurement> records = List.of(measurement(OCT_12, MeasurementType.WAIST, "81"),
                measurement(OCT_12, MeasurementType.BODYFAT, "18"),
                measurement(OCT_12, MeasurementType.WEIGHT, "72"));

        assertEquals("Measurements (3 records, newest first):\n"
                + "2026-10-12  weight 72 kg\n"
                + "2026-10-12  bodyfat 18 %\n"
                + "2026-10-12  waist 81 cm", ClientHistoryFormatter.formatMeasurements(records));
    }

    @Test
    public void formatPerformances_noRecords_showsEmptyState() {
        assertEquals(NO_PERFORMANCES, ClientHistoryFormatter.formatPerformances(new PerformanceHistory()));
    }

    @Test
    public void formatPerformances_oneRecord_usesSingularHeader() {
        assertEquals("Performance (1 record, newest first):\n2026-10-01  Pull-up, 1 set of 1 rep at 0 kg",
                ClientHistoryFormatter.formatPerformances(
                        new PerformanceHistory(List.of(performance(OCT_01, "Pull-up", 1, 1, "0")))));
    }

    @Test
    public void formatPerformances_manyRecordsOutOfOrder_newestDateFirst() {
        List<GymPerformance> records = List.of(performance(OCT_12, "Deadlift", 1, 5, "140"),
                performance(SEP_30, "Row", 3, 10, "40"),
                performance(OCT_15, "Squat", 3, 5, "100"),
                performance(OCT_01, "Bench Press", 3, 8, "60"));

        assertEquals("Performance (4 records, newest first):\n"
                + "2026-10-15  Squat, 3 sets of 5 reps at 100 kg\n"
                + "2026-10-12  Deadlift, 1 set of 5 reps at 140 kg\n"
                + "2026-10-01  Bench Press, 3 sets of 8 reps at 60 kg\n"
                + "2026-09-30  Row, 3 sets of 10 reps at 40 kg",
                ClientHistoryFormatter.formatPerformances(new PerformanceHistory(records)));
    }

    @Test
    public void formatPerformances_sameDate_mostRecentlyRecordedFirst() {
        // Recorded order is deliberately not alphabetical, and an older record sits between same-date ones.
        List<GymPerformance> records = List.of(performance(OCT_15, "Bench Press", 3, 8, "60"),
                performance(OCT_01, "Row", 3, 10, "40"),
                performance(OCT_15, "squat", 3, 5, "100"),
                performance(OCT_15, "Arm Curl", 3, 12, "10"));

        assertEquals("Performance (4 records, newest first):\n"
                + "2026-10-15  Arm Curl, 3 sets of 12 reps at 10 kg\n"
                + "2026-10-15  squat, 3 sets of 5 reps at 100 kg\n"
                + "2026-10-15  Bench Press, 3 sets of 8 reps at 60 kg\n"
                + "2026-10-01  Row, 3 sets of 10 reps at 40 kg",
                ClientHistoryFormatter.formatPerformances(new PerformanceHistory(records)));
    }

    @Test
    public void formatPerformances_sameDate_firstLineMatchesLatestPerformance() {
        Person person = new PersonBuilder().withGymPerformances(performance(OCT_15, "Squat", 3, 5, "100"),
                performance(OCT_15, "Bench Press", 3, 8, "60")).build();
        GymPerformance latest = person.getPerformanceHistory().getLatest().orElseThrow();

        String firstLine = ClientHistoryFormatter.formatPerformances(
                person.getPerformanceHistory()).split("\n")[1];
        assertEquals(latest.getDate() + "  " + latest.getSummary(), firstLine);
    }

    private static Measurement measurement(LocalDate date, MeasurementType type, String value) {
        return new Measurement(date, type, new BigDecimal(value));
    }

    private static GymPerformance performance(LocalDate date, String exercise, int sets, int reps, String load) {
        return new GymPerformance(date, new ExerciseName(exercise), sets, reps,
                new Load(load));
    }
}
