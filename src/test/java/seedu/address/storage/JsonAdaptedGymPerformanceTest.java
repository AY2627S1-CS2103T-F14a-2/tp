package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedGymPerformance.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

public class JsonAdaptedGymPerformanceTest {

    private static final String VALID_DATE = "2026-09-18";
    private static final String VALID_EXERCISE = "Bench Press";
    private static final String VALID_SETS = "3";
    private static final String VALID_REPS = "8";
    private static final String VALID_LOAD = "62.5";

    private static final GymPerformance BENCH_PRESS = new GymPerformance(LocalDate.of(2026, 9, 18),
            new ExerciseName(VALID_EXERCISE), 3, 8, new Load(VALID_LOAD));

    @Test
    public void toModelType_validDetails_returnsGymPerformance() throws Exception {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(BENCH_PRESS);
        assertEquals(BENCH_PRESS, performance.toModelType());
    }

    @Test
    public void toModelType_futureDate_returnsGymPerformance() throws Exception {
        // Stored records must stay loadable, so the future-date rule only applies to new input
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance("2999-01-01", VALID_EXERCISE,
                VALID_SETS, VALID_REPS, VALID_LOAD);
        assertEquals(LocalDate.of(2999, 1, 1), performance.toModelType().getDate());
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance("2026-02-30", VALID_EXERCISE,
                VALID_SETS, VALID_REPS, VALID_LOAD);
        assertThrows(IllegalValueException.class, GymPerformance.MESSAGE_DATE_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(null, VALID_EXERCISE,
                VALID_SETS, VALID_REPS, VALID_LOAD);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "date");
        assertThrows(IllegalValueException.class, expectedMessage, performance::toModelType);
    }

    @Test
    public void toModelType_invalidExercise_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, "Bench/Press",
                VALID_SETS, VALID_REPS, VALID_LOAD);
        assertThrows(IllegalValueException.class, ExerciseName.MESSAGE_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_nullExercise_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, null,
                VALID_SETS, VALID_REPS, VALID_LOAD);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "exercise");
        assertThrows(IllegalValueException.class, expectedMessage, performance::toModelType);
    }

    @Test
    public void toModelType_invalidSets_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                "0", VALID_REPS, VALID_LOAD);
        assertThrows(IllegalValueException.class, GymPerformance.MESSAGE_SETS_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_nullSets_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                null, VALID_REPS, VALID_LOAD);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "sets");
        assertThrows(IllegalValueException.class, expectedMessage, performance::toModelType);
    }

    @Test
    public void toModelType_invalidReps_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, "8.5", VALID_LOAD);
        assertThrows(IllegalValueException.class, GymPerformance.MESSAGE_REPS_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_nullReps_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, null, VALID_LOAD);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "reps");
        assertThrows(IllegalValueException.class, expectedMessage, performance::toModelType);
    }

    @Test
    public void toModelType_nonNumericLoad_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, VALID_REPS, "sixty");
        assertThrows(IllegalValueException.class, Load.MESSAGE_NOT_NUMERIC, performance::toModelType);
    }

    @Test
    public void toModelType_loadOutOfRange_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, VALID_REPS, "1000.01");
        assertThrows(IllegalValueException.class, Load.MESSAGE_RANGE_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_loadWithTooManyDecimalPlaces_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, VALID_REPS, "60.555");
        assertThrows(IllegalValueException.class, Load.MESSAGE_DECIMAL_PLACES_CONSTRAINTS, performance::toModelType);
    }

    @Test
    public void toModelType_nullLoad_throwsIllegalValueException() {
        JsonAdaptedGymPerformance performance = new JsonAdaptedGymPerformance(VALID_DATE, VALID_EXERCISE,
                VALID_SETS, VALID_REPS, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "load");
        assertThrows(IllegalValueException.class, expectedMessage, performance::toModelType);
    }
}
