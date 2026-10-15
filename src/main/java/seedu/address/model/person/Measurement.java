package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * An immutable dated body measurement. Values use decimal arithmetic to preserve precision.
 * AI assistance: OpenAI Codex helped implement this model and its tests.
 */
public final class Measurement {
    public static final String MESSAGE_FUTURE_DATE = "The measurement date cannot be in the future.";
    public static final String MESSAGE_PRECISION = "Measurement values may have at most two decimal places.";

    private final LocalDate date;
    private final MeasurementType type;
    private final BigDecimal value;

    /**
     * Creates a measurement with a non-future date and a value valid for its type.
     */
    public Measurement(LocalDate date, MeasurementType type, BigDecimal value) {
        requireAllNonNull(date, type, value);
        checkArgument(!date.isAfter(LocalDate.now()), MESSAGE_FUTURE_DATE);
        checkArgument(value.scale() <= 2, MESSAGE_PRECISION);
        checkArgument(type.isValidValue(value), type.getValueConstraints());
        this.date = date;
        this.type = type;
        this.value = value.stripTrailingZeros();
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

    /**
     * Returns whether both records have the same date and type, regardless of value.
     */
    public boolean isSameMeasurement(Measurement other) {
        return other != null && date.equals(other.date) && type == other.type;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Measurement measurement
                && date.equals(measurement.date)
                && type == measurement.type
                && value.equals(measurement.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, type, value);
    }

    @Override
    public String toString() {
        return type + " = " + value.toPlainString() + " " + type.getUnit() + " on " + date;
    }
}
