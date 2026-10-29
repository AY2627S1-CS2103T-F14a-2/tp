package seedu.address.testutil;

import java.math.BigDecimal;
import java.time.LocalDate;

import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * Builds measurement and gym-performance records from plain values, so tests can state them in one line.
 */
public final class TrainingRecords {

    private TrainingRecords() {} // prevents instantiation

    /**
     * Returns a measurement of {@code type} with the decimal {@code value} on {@code date}.
     */
    public static Measurement measurement(LocalDate date, MeasurementType type, String value) {
        return new Measurement(date, type, new BigDecimal(value));
    }

    /**
     * Returns a gym performance of {@code exercise} on {@code date}, with the load given as a decimal string.
     */
    public static GymPerformance performance(LocalDate date, String exercise, int sets, int reps, String load) {
        return new GymPerformance(date, new ExerciseName(exercise), sets, reps, new Load(load));
    }
}
