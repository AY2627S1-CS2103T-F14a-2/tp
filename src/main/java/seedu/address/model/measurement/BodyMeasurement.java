package seedu.address.model.measurement;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * An immutable, dated body measurement. Client ownership is managed separately.
 */
public final class BodyMeasurement {
    public static final String MESSAGE_DATE_FORMAT = "The date must use the format yyyy-MM-dd.";
    public static final String MESSAGE_FUTURE_DATE = "The measurement date cannot be in the future.";
    public static final String MESSAGE_NUMERIC_VALUE = "Measurement value must be a number.";
    public static final String MESSAGE_PRECISION = "Measurement values may have at most two decimal places.";

    private final LocalDate date;
    private final MeasurementType type;
    private final BigDecimal value;

    /**
     * Constructs a measurement from a calendar date, supported type and decimal value.
     * Surrounding whitespace is ignored. Values use ordinary decimal notation, without exponents.
     *
     * @throws NullPointerException if any argument is null.
     * @throws IllegalArgumentException if an argument violates the measurement constraints.
     */
    public BodyMeasurement(String date, String type, String value) {
        requireAllNonNull(date, type, value);
        this.date = parseDate(date.strip());
        this.type = MeasurementType.fromString(type);
        this.value = parseValue(value.strip(), this.type);
    }

    private static LocalDate parseDate(String date) {
        checkArgument(date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"), MESSAGE_DATE_FORMAT);
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_DATE_FORMAT, e);
        }
        checkArgument(!parsedDate.isAfter(LocalDate.now()), MESSAGE_FUTURE_DATE);
        return parsedDate;
    }

    private static BigDecimal parseValue(String value, MeasurementType type) {
        checkArgument(value.matches("[+-]?[0-9]+(?:\\.[0-9]+)?"), MESSAGE_NUMERIC_VALUE);
        BigDecimal parsedValue = new BigDecimal(value);
        // Check input precision before normalizing so that 72.500 is rejected.
        checkArgument(parsedValue.scale() <= 2, MESSAGE_PRECISION);
        checkArgument(type.isValidValue(parsedValue), type.getValueConstraints());
        return parsedValue.stripTrailingZeros();
    }

    public LocalDate getDate() {
        return date;
    }

    public MeasurementType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof BodyMeasurement otherMeasurement)) {
            return false;
        }
        return date.equals(otherMeasurement.date)
                && type == otherMeasurement.type
                && value.equals(otherMeasurement.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("type", type)
                .add("value", value.toPlainString())
                .toString();
    }
}
