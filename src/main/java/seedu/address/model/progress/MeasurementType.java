package seedu.address.model.progress;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

/**
 * Supported body measurements, their fixed units and permitted value ranges.
 * Decimal precision is validated separately by the body-measurement record.
 */
public enum MeasurementType {
    WEIGHT("weight", "kg", "1", "500", true, "Weight must be between 1 and 500 kg."),
    BODYFAT("bodyfat", "%", "0", "100", false,
            "Body-fat percentage must be greater than 0 and no more than 100."),
    WAIST("waist", "cm", "1", "300", true, "Waist measurement must be between 1 and 300 cm.");

    public static final String MESSAGE_CONSTRAINTS = "Measurement type must be weight, bodyfat, or waist.";

    private final String commandName;
    private final String unit;
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final boolean minimumInclusive;
    private final String valueConstraints;

    MeasurementType(String commandName, String unit, String minimum, String maximum,
            boolean minimumInclusive, String valueConstraints) {
        this.commandName = commandName;
        this.unit = unit;
        this.minimum = new BigDecimal(minimum);
        this.maximum = new BigDecimal(maximum);
        this.minimumInclusive = minimumInclusive;
        this.valueConstraints = valueConstraints;
    }

    /**
     * Parses a type name, ignoring case and surrounding whitespace.
     *
     * @throws NullPointerException if {@code name} is null.
     * @throws IllegalArgumentException if {@code name} is unsupported.
     */
    public static MeasurementType fromString(String name) {
        String trimmedName = requireNonNull(name).strip();
        for (MeasurementType type : values()) {
            if (type.commandName.equalsIgnoreCase(trimmedName)) {
                return type;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    public String getUnit() {
        return unit;
    }

    public String getValueConstraints() {
        return valueConstraints;
    }

    /**
     * Checks the type-specific range only, without checking decimal precision.
     *
     * @throws NullPointerException if {@code value} is null.
     */
    public boolean isValidValue(BigDecimal value) {
        int minimumComparison = requireNonNull(value).compareTo(minimum);
        return (minimumInclusive ? minimumComparison >= 0 : minimumComparison > 0)
                && value.compareTo(maximum) <= 0;
    }

    @Override
    public String toString() {
        return commandName;
    }
}
