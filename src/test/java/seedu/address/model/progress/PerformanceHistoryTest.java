package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class PerformanceHistoryTest {

    private static final LocalDate DAY_ONE = LocalDate.of(2026, 9, 18);
    private static final LocalDate DAY_TWO = DAY_ONE.plusDays(1);

    private static final GymPerformance BENCH_DAY_ONE = performance(DAY_ONE, "Bench Press", 3, 8, "60");
    private static final GymPerformance SQUAT_DAY_ONE = performance(DAY_ONE, "Squat", 5, 5, "100");
    private static final GymPerformance BENCH_DAY_TWO = performance(DAY_TWO, "Bench Press", 3, 8, "62.5");

    private static GymPerformance performance(LocalDate date, String exercise, int sets, int reps, String load) {
        return new GymPerformance(date, new ExerciseName(exercise), sets, reps, new Load(load));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PerformanceHistory(null));
        assertThrows(NullPointerException.class, () -> new PerformanceHistory(Arrays.asList(BENCH_DAY_ONE, null)));
    }

    @Test
    public void constructor_duplicateRecords_throwsIllegalArgumentException() {
        GymPerformance sameEntry = performance(DAY_ONE, "bench  PRESS", 5, 5, "80");
        assertThrows(IllegalArgumentException.class, PerformanceHistory.MESSAGE_DUPLICATE_RECORDS, ()
            -> new PerformanceHistory(List.of(BENCH_DAY_ONE, sameEntry)));
    }

    @Test
    public void hasNoDuplicates() {
        assertTrue(PerformanceHistory.hasNoDuplicates(List.of()));
        assertTrue(PerformanceHistory.hasNoDuplicates(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO)));

        // same exercise on the same date, differing only in case and spacing
        assertFalse(PerformanceHistory.hasNoDuplicates(
                List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, performance(DAY_ONE, " bench press ", 1, 1, "0"))));
    }

    @Test
    public void hasSameEntry() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));

        assertTrue(history.hasSameEntry(performance(DAY_ONE, "BENCH PRESS", 5, 5, "80")));
        assertFalse(history.hasSameEntry(BENCH_DAY_TWO)); // different date
        assertFalse(history.hasSameEntry(SQUAT_DAY_ONE)); // different exercise
        assertFalse(new PerformanceHistory().hasSameEntry(BENCH_DAY_ONE)); // empty history
    }

    @Test
    public void find() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO));

        assertEquals(Optional.of(BENCH_DAY_TWO), history.find(DAY_TWO, new ExerciseName("Bench Press")));

        // exercise name differs only in letter case and spacing
        assertEquals(Optional.of(BENCH_DAY_ONE), history.find(DAY_ONE, new ExerciseName("  bench   PRESS ")));

        // right exercise on the wrong date, and the wrong exercise on the right date
        assertEquals(Optional.empty(), history.find(DAY_TWO, new ExerciseName("Squat")));
        assertEquals(Optional.empty(), history.find(DAY_ONE, new ExerciseName("Deadlift")));

        // empty history
        assertEquals(Optional.empty(), new PerformanceHistory().find(DAY_ONE, new ExerciseName("Squat")));

        assertThrows(NullPointerException.class, () -> history.find(null, new ExerciseName("Squat")));
        assertThrows(NullPointerException.class, () -> history.find(DAY_ONE, null));
    }

    @Test
    public void withoutRecord_existingRecord_removesOnlyThatRecord() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO));

        PerformanceHistory updatedHistory = history.withoutRecord(SQUAT_DAY_ONE);

        assertEquals(List.of(BENCH_DAY_ONE, BENCH_DAY_TWO), updatedHistory.asUnmodifiableList());
        assertEquals(2, updatedHistory.size());
        assertEquals(3, history.size()); // original unchanged
    }

    @Test
    public void withoutRecord_latestRecord_latestFallsBackToPreviousRecord() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO));

        // removing the record with the latest date
        assertEquals(Optional.of(SQUAT_DAY_ONE), history.withoutRecord(BENCH_DAY_TWO).getLatest());

        // removing the last-recorded record of a same-date tie
        PerformanceHistory sameDate = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE));
        assertEquals(Optional.of(BENCH_DAY_ONE), sameDate.withoutRecord(SQUAT_DAY_ONE).getLatest());

        // removing an older record leaves the latest unchanged
        assertEquals(Optional.of(BENCH_DAY_TWO), history.withoutRecord(BENCH_DAY_ONE).getLatest());
    }

    @Test
    public void withoutRecord_onlyRecord_leavesEmptyHistory() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));

        PerformanceHistory updatedHistory = history.withoutRecord(BENCH_DAY_ONE);

        assertEquals(new PerformanceHistory(), updatedHistory);
        assertEquals(Optional.empty(), updatedHistory.getLatest());
    }

    @Test
    public void withoutRecord_matchesSameEntryAndIgnoresMissingRecord() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE));

        // a record for the same exercise and date with different results identifies the same entry
        assertEquals(List.of(SQUAT_DAY_ONE),
                history.withoutRecord(performance(DAY_ONE, "bench press", 1, 1, "0")).asUnmodifiableList());

        // a record that is not in the history leaves it unchanged
        assertEquals(history, history.withoutRecord(BENCH_DAY_TWO));

        assertThrows(NullPointerException.class, () -> history.withoutRecord(null));
    }

    @Test
    public void withRecord_newEntry_appendsRecord() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));

        PerformanceHistory updatedHistory = history.withRecord(SQUAT_DAY_ONE);

        assertEquals(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE), updatedHistory.asUnmodifiableList());
        assertEquals(List.of(BENCH_DAY_ONE), history.asUnmodifiableList()); // original unchanged
    }

    @Test
    public void withRecord_sameEntry_replacesAndMovesToMostRecent() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE));
        GymPerformance correctedBench = performance(DAY_ONE, "bench press", 4, 6, "65");

        PerformanceHistory updatedHistory = history.withRecord(correctedBench);

        assertEquals(List.of(SQUAT_DAY_ONE, correctedBench), updatedHistory.asUnmodifiableList());
    }

    @Test
    public void getLatest() {
        // empty history
        assertEquals(Optional.empty(), new PerformanceHistory().getLatest());

        // latest date wins, regardless of recording order
        assertEquals(Optional.of(BENCH_DAY_TWO),
                new PerformanceHistory(List.of(BENCH_DAY_TWO, SQUAT_DAY_ONE)).getLatest());

        // same date -> the one recorded last wins
        assertEquals(Optional.of(SQUAT_DAY_ONE),
                new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE)).getLatest());

        // a replaced record counts as recorded again
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE));
        GymPerformance correctedBench = performance(DAY_ONE, "Bench Press", 4, 6, "65");
        assertEquals(Optional.of(correctedBench), history.withRecord(correctedBench).getLatest());
    }

    @Test
    public void getNewestFirst() {
        // empty history
        assertEquals(List.of(), new PerformanceHistory().getNewestFirst());

        // different dates: newest date first, regardless of recording order
        assertEquals(List.of(BENCH_DAY_TWO, BENCH_DAY_ONE),
                new PerformanceHistory(List.of(BENCH_DAY_ONE, BENCH_DAY_TWO)).getNewestFirst());
        assertEquals(List.of(BENCH_DAY_TWO, BENCH_DAY_ONE),
                new PerformanceHistory(List.of(BENCH_DAY_TWO, BENCH_DAY_ONE)).getNewestFirst());

        // same date: the most recently recorded first
        GymPerformance deadliftDayOne = performance(DAY_ONE, "Deadlift", 1, 5, "140");
        assertEquals(List.of(BENCH_DAY_TWO, deadliftDayOne, SQUAT_DAY_ONE, BENCH_DAY_ONE),
                new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO, deadliftDayOne))
                        .getNewestFirst());
    }

    @Test
    public void getNewestFirst_firstRecord_isLatest() {
        // the card's latest performance and the first line of the full history must always agree
        List<PerformanceHistory> histories = List.of(
                new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE)),
                new PerformanceHistory(List.of(SQUAT_DAY_ONE, BENCH_DAY_ONE)),
                new PerformanceHistory(List.of(BENCH_DAY_TWO, SQUAT_DAY_ONE, BENCH_DAY_ONE)),
                new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE)).withRecord(BENCH_DAY_ONE));
        for (PerformanceHistory history : histories) {
            assertEquals(Optional.of(history.getNewestFirst().get(0)), history.getLatest());
        }
    }

    @Test
    public void getNewestFirst_afterReplacementAndDeletion_staysSorted() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE, BENCH_DAY_TWO));

        // a replacement counts as recorded again, so it moves ahead of other records on its date
        GymPerformance correctedBench = performance(DAY_ONE, "Bench Press", 4, 6, "65");
        assertEquals(List.of(BENCH_DAY_TWO, correctedBench, SQUAT_DAY_ONE),
                history.withRecord(correctedBench).getNewestFirst());

        // after a deletion the remaining records keep their order
        assertEquals(List.of(SQUAT_DAY_ONE, BENCH_DAY_ONE), history.withoutRecord(BENCH_DAY_TWO).getNewestFirst());
    }

    @Test
    public void getNewestFirst_modifyList_throwsUnsupportedOperationException() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));
        assertThrows(UnsupportedOperationException.class, () -> history.getNewestFirst().add(SQUAT_DAY_ONE));
    }

    @Test
    public void size() {
        assertEquals(0, new PerformanceHistory().size());
        assertEquals(2, new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE)).size());

        // replacing does not change the size
        assertEquals(2, new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE))
                .withRecord(performance(DAY_ONE, "Squat", 1, 1, "120")).size());
    }

    @Test
    public void asUnmodifiableList_modifyList_throwsUnsupportedOperationException() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));
        assertThrows(UnsupportedOperationException.class, () -> history.asUnmodifiableList().add(SQUAT_DAY_ONE));
    }

    @Test
    public void equals() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE));

        // same values -> returns true
        assertTrue(history.equals(new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE))));
        assertEquals(history.hashCode(), new PerformanceHistory(List.of(BENCH_DAY_ONE, SQUAT_DAY_ONE)).hashCode());

        // same object -> returns true
        assertTrue(history.equals(history));

        // null -> returns false
        assertFalse(history.equals(null));

        // different type -> returns false
        assertFalse(history.equals(5));

        // different records -> returns false
        assertFalse(history.equals(new PerformanceHistory(List.of(BENCH_DAY_ONE))));

        // different recording order -> returns false, as the order decides the latest record
        assertFalse(history.equals(new PerformanceHistory(List.of(SQUAT_DAY_ONE, BENCH_DAY_ONE))));
    }

    @Test
    public void toStringMethod() {
        PerformanceHistory history = new PerformanceHistory(List.of(BENCH_DAY_ONE));
        String expected = PerformanceHistory.class.getCanonicalName() + "{records=" + List.of(BENCH_DAY_ONE) + "}";
        assertEquals(expected, history.toString());
    }
}
