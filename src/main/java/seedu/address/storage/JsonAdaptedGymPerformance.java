package seedu.address.storage;

import java.time.LocalDate;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * Jackson-friendly version of {@link GymPerformance}.
 * All fields are stored as strings so that the data file stays easy to edit by hand.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement this class and its tests.
 */
class JsonAdaptedGymPerformance {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Gym performance's %s field is missing!";

    private final String date;
    private final String exercise;
    private final String sets;
    private final String reps;
    private final String load;

    /**
     * Constructs a {@code JsonAdaptedGymPerformance} with the given details.
     */
    @JsonCreator
    public JsonAdaptedGymPerformance(@JsonProperty("date") String date, @JsonProperty("exercise") String exercise,
            @JsonProperty("sets") String sets, @JsonProperty("reps") String reps,
            @JsonProperty("load") String load) {
        this.date = date;
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.load = load;
    }

    /**
     * Converts a given {@code GymPerformance} into this class for Jackson use.
     */
    public JsonAdaptedGymPerformance(GymPerformance source) {
        date = source.getDate().format(GymPerformance.DATE_FORMATTER);
        exercise = source.getExerciseName().value;
        sets = String.valueOf(source.getSets());
        reps = String.valueOf(source.getReps());
        load = source.getLoad().toString();
    }

    /**
     * Converts this Jackson-friendly adapted object into the model's {@code GymPerformance} object.
     * Dates in the future are accepted, so that stored records stay loadable however the clock is set.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted object.
     */
    public GymPerformance toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        if (!GymPerformance.isValidDate(date)) {
            throw new IllegalValueException(GymPerformance.MESSAGE_DATE_CONSTRAINTS);
        }
        final LocalDate modelDate = LocalDate.parse(date, GymPerformance.DATE_FORMATTER);

        if (exercise == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "exercise"));
        }
        if (!ExerciseName.isValidExerciseName(exercise)) {
            throw new IllegalValueException(ExerciseName.MESSAGE_CONSTRAINTS);
        }
        final ExerciseName modelExercise = new ExerciseName(exercise);

        final int modelSets = toCount(sets, "sets", GymPerformance::isValidSets,
                GymPerformance.MESSAGE_SETS_CONSTRAINTS);
        final int modelReps = toCount(reps, "reps", GymPerformance::isValidReps,
                GymPerformance.MESSAGE_REPS_CONSTRAINTS);

        if (load == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "load"));
        }
        final Load modelLoad;
        try {
            // Load reports whether the value is not numeric, out of range or too precise
            modelLoad = new Load(load);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }

        return new GymPerformance(modelDate, modelExercise, modelSets, modelReps, modelLoad);
    }

    /**
     * Converts a stored whole-number count, such as the number of sets, after checking it with {@code isValid}.
     */
    private static int toCount(String count, String fieldName, Predicate<String> isValid, String constraintsMessage)
            throws IllegalValueException {
        if (count == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldName));
        }
        if (!isValid.test(count)) {
            throw new IllegalValueException(constraintsMessage);
        }
        return Integer.parseInt(count);
    }

}
