package seedu.address.storage;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * Jackson-friendly version of {@link GymPerformance}.
 * All fields are stored as strings so that the data file stays easy to edit by hand.
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

        if (sets == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "sets"));
        }
        if (!GymPerformance.isValidSets(sets)) {
            throw new IllegalValueException(GymPerformance.MESSAGE_SETS_CONSTRAINTS);
        }
        final int modelSets = Integer.parseInt(sets);

        if (reps == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "reps"));
        }
        if (!GymPerformance.isValidReps(reps)) {
            throw new IllegalValueException(GymPerformance.MESSAGE_REPS_CONSTRAINTS);
        }
        final int modelReps = Integer.parseInt(reps);

        if (load == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "load"));
        }
        if (!Load.isNumberWithinRange(load)) {
            throw new IllegalValueException(Load.MESSAGE_RANGE_CONSTRAINTS);
        }
        if (!Load.hasValidDecimalPlaces(load)) {
            throw new IllegalValueException(Load.MESSAGE_DECIMAL_PLACES_CONSTRAINTS);
        }
        final Load modelLoad = new Load(load);

        return new GymPerformance(modelDate, modelExercise, modelSets, modelReps, modelLoad);
    }

}
