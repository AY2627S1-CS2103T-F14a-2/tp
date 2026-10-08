package seedu.address.model.progress;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * An immutable, dated body measurement. Client ownership is managed separately.
 * Input parsing and future-date validation belong to the command parser. Stored records must
 * remain valid regardless of the current date or system clock.
 */
public final class BodyMeasurement {
    public static final String MESSAGE_PRECISION = "Measurement values may have at most two decimal places.";

    private final LocalDate date;
    private final MeasurementType type;
    private final BigDecimal value;

    /**
     * Constructs a measurement from a calendar date, supported type and decimal value.
     * Trailing zeros are removed before validating decimal precision.
     *
     * @throws NullPointerException if any argument is null.
     * @throws IllegalArgumentException if an argument violates the measurement constraints.
     */
    public BodyMeasurement(LocalDate date, MeasurementType type, BigDecimal value) {
        requireAllNonNull(date, type, value);
        BigDecimal normalizedValue = value.stripTrailingZeros();
        checkArgument(normalizedValue.scale() <= 2, MESSAGE_PRECISION);
        checkArgument(type.isValidValue(normalizedValue), type.getValueConstraints());
        this.date = date;
        this.type = type;
        this.value = normalizedValue;
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
