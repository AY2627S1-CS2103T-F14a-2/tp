package seedu.address.model.progress;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;

/**
 * Represents the load, in kilograms, lifted in a gym-performance record.
 * A load of 0 represents a bodyweight or unloaded exercise.
 * Guarantees: immutable; is valid as declared in {@link #isValidLoad(String)}
 *
 * <p>The value is stored as a {@code BigDecimal} so that decimal loads such as 62.5 are kept exactly.
 */
public class Load {

    public static final String MESSAGE_RANGE_CONSTRAINTS = "Exercise load must be between 0 and 1000 kg.";
    public static final String MESSAGE_DECIMAL_PLACES_CONSTRAINTS =
            "Exercise load may have at most two decimal places.";

    public static final int MAX_DECIMAL_PLACES = 2;

    private static final BigDecimal MIN_LOAD = BigDecimal.ZERO;
    private static final BigDecimal MAX_LOAD = new BigDecimal("1000");

    /*
     * Accepts only plain non-negative decimal numbers, so that inputs such as "-5", "+5", "1e2" and ".5"
     * are rejected before they reach BigDecimal, which would otherwise accept some of them.
     */
    private static final String NUMBER_REGEX = "\\d+(\\.\\d+)?";

    public final BigDecimal value;

    /**
     * Constructs a {@code Load}.
     *
     * @param load A valid load in kilograms.
     */
    public Load(String load) {
        requireNonNull(load);
        checkArgument(isNumberWithinRange(load), MESSAGE_RANGE_CONSTRAINTS);
        checkArgument(hasValidDecimalPlaces(load), MESSAGE_DECIMAL_PLACES_CONSTRAINTS);
        value = new BigDecimal(load);
    }

    /**
     * Returns true if the given string is a valid load.
     */
    public static boolean isValidLoad(String test) {
        return isNumberWithinRange(test) && hasValidDecimalPlaces(test);
    }

    /**
     * Returns true if the given string is a plain number from 0 to 1000 inclusive.
     */
    public static boolean isNumberWithinRange(String test) {
        requireNonNull(test);
        if (!test.matches(NUMBER_REGEX)) {
            return false;
        }
        BigDecimal load = new BigDecimal(test);
        return load.compareTo(MIN_LOAD) >= 0 && load.compareTo(MAX_LOAD) <= 0;
    }

    /**
     * Returns true if the given string has at most {@value #MAX_DECIMAL_PLACES} digits after the decimal point.
     * The digits are counted as typed, so "60.500" is rejected even though it equals 60.5.
     */
    public static boolean hasValidDecimalPlaces(String test) {
        requireNonNull(test);
        int decimalPointIndex = test.indexOf('.');
        return decimalPointIndex < 0 || test.length() - decimalPointIndex - 1 <= MAX_DECIMAL_PLACES;
    }

    /**
     * Returns the load without trailing zeros, e.g. "60" instead of "60.00".
     */
    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Load otherLoad)) {
            return false;
        }

        // compareTo ignores scale, so 60 and 60.0 are equal
        return value.compareTo(otherLoad.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }

}
