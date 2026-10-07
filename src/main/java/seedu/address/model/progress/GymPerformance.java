package seedu.address.model.progress;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a client's dated result for one exercise: the number of sets, the repetitions per set,
 * and the load lifted.
 * Guarantees: immutable; details are present and not null, field values are validated.
 *
 * <p>Whether the date is in the future is not checked here, because a stored record must stay valid
 * regardless of when the data is loaded. That rule is enforced when the user records a performance.
 */
public class GymPerformance {

    public static final String MESSAGE_DATE_CONSTRAINTS = "The date must use the format yyyy-MM-dd.";
    public static final String MESSAGE_SETS_CONSTRAINTS =
            "The number of sets must be a whole number between 1 and 100.";
    public static final String MESSAGE_REPS_CONSTRAINTS =
            "The number of repetitions must be a whole number between 1 and 1000.";

    public static final int MIN_SETS = 1;
    public static final int MAX_SETS = 100;
    public static final int MIN_REPS = 1;
    public static final int MAX_REPS = 1000;

    /** Format used when entering and storing the date of a performance. */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDate date;
    private final ExerciseName exerciseName;
    private final int sets;
    private final int reps;
    private final Load load;

    /**
     * Every field must be present and not null, and {@code sets} and {@code reps} must be within range.
     */
    public GymPerformance(LocalDate date, ExerciseName exerciseName, int sets, int reps, Load load) {
        requireAllNonNull(date, exerciseName, load);
        checkArgument(isValidSets(sets), MESSAGE_SETS_CONSTRAINTS);
        checkArgument(isValidReps(reps), MESSAGE_REPS_CONSTRAINTS);
        this.date = date;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.load = load;
    }

    /**
     * Returns true if the given string is a real calendar date in yyyy-MM-dd format.
     */
    public static boolean isValidDate(String test) {
        requireNonNull(test);
        try {
            LocalDate.parse(test, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if the given number of sets is within the permitted range.
     */
    public static boolean isValidSets(int test) {
        return test >= MIN_SETS && test <= MAX_SETS;
    }

    /**
     * Returns true if the given number of repetitions per set is within the permitted range.
     */
    public static boolean isValidReps(int test) {
        return test >= MIN_REPS && test <= MAX_REPS;
    }

    public LocalDate getDate() {
        return date;
    }

    public ExerciseName getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public Load getLoad() {
        return load;
    }

    /**
     * Returns true if both records are for the same exercise on the same date.
     * Such records are duplicates, as only one result per exercise per day is kept for a client.
     * This defines a weaker notion of equality between two gym-performance records.
     */
    public boolean isSameEntry(GymPerformance otherPerformance) {
        if (otherPerformance == this) {
            return true;
        }

        return otherPerformance != null
                && otherPerformance.date.equals(date)
                && otherPerformance.exerciseName.equals(exerciseName);
    }

    /**
     * Returns true if both records have the same date, exercise, sets, repetitions and load.
     * This defines a stronger notion of equality between two gym-performance records.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GymPerformance otherPerformance)) {
            return false;
        }

        return date.equals(otherPerformance.date)
                && exerciseName.equals(otherPerformance.exerciseName)
                && sets == otherPerformance.sets
                && reps == otherPerformance.reps
                && load.equals(otherPerformance.load);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, exerciseName, sets, reps, load);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("exerciseName", exerciseName)
                .add("sets", sets)
                .add("reps", reps)
                .add("load", load)
                .toString();
    }

}
