package seedu.address.model.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ExerciseNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExerciseName(null));
    }

    @Test
    public void constructor_invalidExerciseName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ExerciseName.MESSAGE_CONSTRAINTS, () -> new ExerciseName(""));
    }

    @Test
    public void constructor_untidyWhitespace_normalisesName() {
        assertEquals("Bench Press", new ExerciseName("  Bench    Press  ").value);
        assertEquals("Bench Press", new ExerciseName("Bench\tPress").value);
    }

    @Test
    public void isValidExerciseName() {
        // null exercise name
        assertThrows(NullPointerException.class, () -> ExerciseName.isValidExerciseName(null));

        // invalid exercise names
        assertFalse(ExerciseName.isValidExerciseName("")); // empty string
        assertFalse(ExerciseName.isValidExerciseName("   ")); // spaces only
        assertFalse(ExerciseName.isValidExerciseName("-")); // no letter or digit
        assertFalse(ExerciseName.isValidExerciseName("' -")); // no letter or digit
        assertFalse(ExerciseName.isValidExerciseName("Bench@Press")); // unsupported character
        assertFalse(ExerciseName.isValidExerciseName("Squat/Deadlift")); // unsupported character
        assertFalse(ExerciseName.isValidExerciseName("a".repeat(51))); // too long

        // valid exercise names
        assertTrue(ExerciseName.isValidExerciseName("a")); // one character
        assertTrue(ExerciseName.isValidExerciseName("1")); // digit only
        assertTrue(ExerciseName.isValidExerciseName("Bench Press")); // letters and space
        assertTrue(ExerciseName.isValidExerciseName("Pull-up")); // hyphen
        assertTrue(ExerciseName.isValidExerciseName("Farmer's Walk")); // straight apostrophe
        assertTrue(ExerciseName.isValidExerciseName("Farmer’s Walk")); // curly apostrophe
        assertTrue(ExerciseName.isValidExerciseName("21s Bicep Curl")); // letters and digits
        assertTrue(ExerciseName.isValidExerciseName("a".repeat(50))); // maximum length
        assertTrue(ExerciseName.isValidExerciseName(" " + "a".repeat(50) + " ")); // maximum length after trimming
        assertTrue(ExerciseName.isValidExerciseName("a".repeat(25) + "    " + "a".repeat(24))); // spaces collapsed
    }

    @Test
    public void equals() {
        ExerciseName exerciseName = new ExerciseName("Bench Press");

        // same values -> returns true
        assertTrue(exerciseName.equals(new ExerciseName("Bench Press")));

        // same object -> returns true
        assertTrue(exerciseName.equals(exerciseName));

        // null -> returns false
        assertFalse(exerciseName.equals(null));

        // different types -> returns false
        assertFalse(exerciseName.equals(5.0f));

        // different letter case and extra whitespace -> returns true
        assertTrue(exerciseName.equals(new ExerciseName("  bench   PRESS ")));

        // different values -> returns false
        assertFalse(exerciseName.equals(new ExerciseName("Incline Bench Press")));
    }

    @Test
    public void hashCode_differentLetterCase_sameHashCode() {
        assertEquals(new ExerciseName("Bench Press").hashCode(), new ExerciseName("bench press").hashCode());
    }

    @Test
    public void toString_returnsNormalisedName() {
        assertEquals("Farmer's Walk", new ExerciseName(" Farmer's   Walk ").toString());
    }
}
