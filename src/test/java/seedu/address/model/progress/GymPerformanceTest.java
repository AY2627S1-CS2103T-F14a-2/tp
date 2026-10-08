package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class GymPerformanceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 18);
    private static final ExerciseName BENCH_PRESS = new ExerciseName("Bench Press");
    private static final Load LOAD = new Load("60");

    private final GymPerformance benchPress = new GymPerformance(DATE, BENCH_PRESS, 3, 8, LOAD);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GymPerformance(null, BENCH_PRESS, 3, 8, LOAD));
        assertThrows(NullPointerException.class, () -> new GymPerformance(DATE, null, 3, 8, LOAD));
        assertThrows(NullPointerException.class, () -> new GymPerformance(DATE, BENCH_PRESS, 3, 8, null));
    }

    @Test
    public void constructor_invalidSets_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, GymPerformance.MESSAGE_SETS_CONSTRAINTS, ()
            -> new GymPerformance(DATE, BENCH_PRESS, 0, 8, LOAD));
    }

    @Test
    public void constructor_invalidReps_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, GymPerformance.MESSAGE_REPS_CONSTRAINTS, ()
            -> new GymPerformance(DATE, BENCH_PRESS, 3, 0, LOAD));
    }

    @Test
    public void getters_returnConstructorValues() {
        assertEquals(DATE, benchPress.getDate());
        assertEquals(BENCH_PRESS, benchPress.getExerciseName());
        assertEquals(3, benchPress.getSets());
        assertEquals(8, benchPress.getReps());
        assertEquals(LOAD, benchPress.getLoad());
    }

    @Test
    public void isValidDate() {
        // null date
        assertThrows(NullPointerException.class, () -> GymPerformance.isValidDate(null));

        // invalid dates
        assertFalse(GymPerformance.isValidDate("")); // empty string
        assertFalse(GymPerformance.isValidDate("18-09-2026")); // wrong order
        assertFalse(GymPerformance.isValidDate("2026/09/18")); // wrong separator
        assertFalse(GymPerformance.isValidDate("2026-9-18")); // missing leading zero
        assertFalse(GymPerformance.isValidDate("2026-02-30")); // not a real date
        assertFalse(GymPerformance.isValidDate("2026-02-29")); // not a leap year
        assertFalse(GymPerformance.isValidDate("2026-13-01")); // invalid month
        assertFalse(GymPerformance.isValidDate(" 2026-09-18")); // untrimmed

        // valid dates
        assertTrue(GymPerformance.isValidDate("2026-09-18"));
        assertTrue(GymPerformance.isValidDate("2028-02-29")); // leap year
        assertTrue(GymPerformance.isValidDate("2026-12-31"));
    }

    @Test
    public void isValidSets() {
        assertFalse(GymPerformance.isValidSets(-1));
        assertFalse(GymPerformance.isValidSets(0)); // just below minimum
        assertFalse(GymPerformance.isValidSets(101)); // just above maximum

        assertTrue(GymPerformance.isValidSets(1)); // minimum
        assertTrue(GymPerformance.isValidSets(100)); // maximum
    }

    @Test
    public void isValidReps() {
        assertFalse(GymPerformance.isValidReps(-1));
        assertFalse(GymPerformance.isValidReps(0)); // just below minimum
        assertFalse(GymPerformance.isValidReps(1001)); // just above maximum

        assertTrue(GymPerformance.isValidReps(1)); // minimum
        assertTrue(GymPerformance.isValidReps(1000)); // maximum
    }

    @Test
    public void isSameEntry() {
        // same object -> returns true
        assertTrue(benchPress.isSameEntry(benchPress));

        // null -> returns false
        assertFalse(benchPress.isSameEntry(null));

        // same date and exercise, different results -> returns true
        assertTrue(benchPress.isSameEntry(new GymPerformance(DATE, BENCH_PRESS, 5, 5, new Load("80"))));

        // exercise name differs only in letter case and whitespace -> returns true
        assertTrue(benchPress.isSameEntry(new GymPerformance(DATE, new ExerciseName(" bench  press "), 3, 8, LOAD)));

        // different date -> returns false
        assertFalse(benchPress.isSameEntry(new GymPerformance(DATE.plusDays(1), BENCH_PRESS, 3, 8, LOAD)));

        // different exercise -> returns false
        assertFalse(benchPress.isSameEntry(new GymPerformance(DATE, new ExerciseName("Squat"), 3, 8, LOAD)));
    }

    @Test
    public void equals() {
        // same values -> returns true
        assertTrue(benchPress.equals(new GymPerformance(DATE, BENCH_PRESS, 3, 8, new Load("60.0"))));

        // same object -> returns true
        assertTrue(benchPress.equals(benchPress));

        // null -> returns false
        assertFalse(benchPress.equals(null));

        // different type -> returns false
        assertFalse(benchPress.equals(5));

        // different date -> returns false
        assertFalse(benchPress.equals(new GymPerformance(DATE.minusDays(1), BENCH_PRESS, 3, 8, LOAD)));

        // different exercise -> returns false
        assertFalse(benchPress.equals(new GymPerformance(DATE, new ExerciseName("Squat"), 3, 8, LOAD)));

        // different sets -> returns false
        assertFalse(benchPress.equals(new GymPerformance(DATE, BENCH_PRESS, 4, 8, LOAD)));

        // different reps -> returns false
        assertFalse(benchPress.equals(new GymPerformance(DATE, BENCH_PRESS, 3, 10, LOAD)));

        // different load -> returns false
        assertFalse(benchPress.equals(new GymPerformance(DATE, BENCH_PRESS, 3, 8, new Load("62.5"))));
    }

    @Test
    public void hashCode_equalRecords_sameHashCode() {
        assertEquals(benchPress.hashCode(),
                new GymPerformance(DATE, new ExerciseName("bench press"), 3, 8, new Load("60.00")).hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = GymPerformance.class.getCanonicalName() + "{date=" + DATE + ", exerciseName=" + BENCH_PRESS
                + ", sets=3, reps=8, load=" + LOAD + "}";
        assertEquals(expected, benchPress.toString());
    }
}
