package seedu.address.model.progress;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents the name of an exercise in a gym-performance record.
 * Guarantees: immutable; is valid as declared in {@link #isValidExerciseName(String)}
 *
 * <p>Names are normalised by trimming them and collapsing repeated internal whitespace into one space.
 * Two exercise names are equal if their normalised forms match, ignoring letter case,
 * so that {@code bench  press} and {@code Bench Press} refer to the same exercise.
 */
public class ExerciseName {

    public static final String MESSAGE_CONSTRAINTS = "Exercise names must be 1 to 50 characters and may contain "
            + "only letters, numbers, spaces, hyphens, and apostrophes.";

    public static final int MAX_LENGTH = 50;

    /*
     * Allows only letters, digits, spaces, hyphens, and straight or curly (’) apostrophes,
     * and requires at least one letter or digit so that names such as "-" or "'" are rejected.
     */
    private static final String VALIDATION_REGEX = "(?=.*\\p{Alnum})[\\p{Alnum} '’-]+";

    public final String value;

    /**
     * Constructs an {@code ExerciseName}.
     *
     * @param exerciseName A valid exercise name.
     */
    public ExerciseName(String exerciseName) {
        requireNonNull(exerciseName);
        checkArgument(isValidExerciseName(exerciseName), MESSAGE_CONSTRAINTS);
        value = normalise(exerciseName);
    }

    /**
     * Returns true if the given string, after normalisation, is a valid exercise name.
     */
    public static boolean isValidExerciseName(String test) {
        requireNonNull(test);
        String normalised = normalise(test);
        return normalised.length() <= MAX_LENGTH && normalised.matches(VALIDATION_REGEX);
    }

    private static String normalise(String exerciseName) {
        return exerciseName.trim().replaceAll("\\s+", " ");
    }

    private String comparisonKey() {
        return value.toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ExerciseName otherExerciseName)) {
            return false;
        }

        return comparisonKey().equals(otherExerciseName.comparisonKey());
    }

    @Override
    public int hashCode() {
        return comparisonKey().hashCode();
    }

}
