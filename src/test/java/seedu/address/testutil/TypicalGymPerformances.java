package seedu.address.testutil;

import java.time.LocalDate;

import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * A utility class containing a list of {@code GymPerformance} objects to be used in tests.
 */
public class TypicalGymPerformances {

    public static final GymPerformance BENCH_PRESS = new GymPerformance(LocalDate.of(2026, 9, 18),
            new ExerciseName("Bench Press"), 3, 8, new Load("60"));
    public static final GymPerformance SQUAT = new GymPerformance(LocalDate.of(2026, 9, 15),
            new ExerciseName("Squat"), 3, 5, new Load("100"));
    public static final GymPerformance PULL_UP = new GymPerformance(LocalDate.of(2026, 9, 18),
            new ExerciseName("Pull-up"), 1, 1, new Load("0"));

    private TypicalGymPerformances() {} // prevents instantiation
}
