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
    public void constructor_parenthesizedName_keepsIdentityAndLimits() {
        ExerciseName cableRow = new ExerciseName("  Row   (Cable)  ");
        assertEquals("Row (Cable)", cableRow.value);
        assertEquals(cableRow, new ExerciseName("row (cable)"));
        assertEquals(cableRow.hashCode(), new ExerciseName("ROW (CABLE)").hashCode());
        assertTrue(ExerciseName.isValidExerciseName("a".repeat(48) + "()"));
        assertFalse(ExerciseName.isValidExerciseName("a".repeat(49) + "()"));
        assertFalse(ExerciseName.isValidExerciseName("()"));
        assertFalse(ExerciseName.isValidExerciseName("Row (Cable)/"));
        assertEquals("Row (Cable", new ExerciseName("  Row (Cable  ").value);
        assertEquals("Row Cable)", new ExerciseName("Row Cable)").value);
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
        assertFalse(ExerciseName.isValidExerciseName("& + .")); // symbols only
        assertFalse(ExerciseName.isValidExerciseName("Bench@Press")); // unsupported character
        assertFalse(ExerciseName.isValidExerciseName("Squat/Deadlift")); // unsupported character
        assertFalse(ExerciseName.isValidExerciseName("a".repeat(51))); // too long
        assertFalse(ExerciseName.isValidExerciseName("Développé couché")); // accented letters are not English A-Z
        assertFalse(ExerciseName.isValidExerciseName("Жим лёжа")); // non-Latin letters
        assertFalse(ExerciseName.isValidExerciseName("Bench Press ３")); // full-width digit

        // valid exercise names
        assertTrue(ExerciseName.isValidExerciseName("a")); // one character
        assertTrue(ExerciseName.isValidExerciseName("1")); // digit only
        assertTrue(ExerciseName.isValidExerciseName("Bench Press")); // letters and space
        assertTrue(ExerciseName.isValidExerciseName("Pull-up")); // hyphen
        assertTrue(ExerciseName.isValidExerciseName("Farmer's Walk")); // straight apostrophe
        assertTrue(ExerciseName.isValidExerciseName("Farmer’s Walk")); // curly apostrophe
        assertTrue(ExerciseName.isValidExerciseName("21s Bicep Curl")); // letters and digits
        assertTrue(ExerciseName.isValidExerciseName("Clean & Jerk")); // ampersand
        assertTrue(ExerciseName.isValidExerciseName("Squat + Press")); // plus sign
        assertTrue(ExerciseName.isValidExerciseName("1.5 Rep Squat")); // full stop
        assertTrue(ExerciseName.isValidExerciseName("DB Press (Incline)")); // parentheses
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

        // straight and curly apostrophes -> returns true
        assertTrue(new ExerciseName("Farmer's Walk").equals(new ExerciseName("farmer’s walk")));

        // an apostrophe is not the same as no apostrophe -> returns false
        assertFalse(new ExerciseName("Farmer's Walk").equals(new ExerciseName("Farmers Walk")));

        // apostrophe style ignored together with a parenthesized label -> returns true
        assertTrue(new ExerciseName("Farmer's Walk (Cable)").equals(new ExerciseName("farmer’s walk  (CABLE)")));

        // the label is part of the name -> returns false
        assertFalse(new ExerciseName("Farmer's Walk (Cable)").equals(new ExerciseName("Farmer's Walk")));
    }

    @Test
    public void hashCode_differentLetterCase_sameHashCode() {
        assertEquals(new ExerciseName("Bench Press").hashCode(), new ExerciseName("bench press").hashCode());
    }

    @Test
    public void hashCode_differentApostrophe_sameHashCode() {
        assertEquals(new ExerciseName("Farmer's Walk").hashCode(), new ExerciseName("Farmer’s Walk").hashCode());
    }

    @Test
    public void toString_returnsNormalisedName() {
        assertEquals("Farmer's Walk", new ExerciseName(" Farmer's   Walk ").toString());
    }

    @Test
    public void toString_curlyApostrophe_keptAsTyped() {
        assertEquals("Farmer’s Walk", new ExerciseName("Farmer’s Walk").toString());
    }
}
