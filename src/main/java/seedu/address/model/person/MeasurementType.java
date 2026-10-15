package seedu.address.model.person;

import java.math.BigDecimal;

/**
 * The supported body measurements, with fixed units and permitted ranges.
 */
public enum MeasurementType {
    WEIGHT("weight", "kg", "1", "500", "Weight must be between 1 and 500 kg."),
    BODYFAT("bodyfat", "%", "0", "100", "Body-fat percentage must be greater than 0 and no more than 100."),
    WAIST("waist", "cm", "1", "300", "Waist measurement must be between 1 and 300 cm.");

    public static final String MESSAGE_CONSTRAINTS = "Measurement type must be weight, bodyfat, or waist.";

    private final String label;
    private final String unit;
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final String valueConstraints;

    MeasurementType(String label, String unit, String minimum, String maximum, String valueConstraints) {
        this.label = label;
        this.unit = unit;
        this.minimum = new BigDecimal(minimum);
        this.maximum = new BigDecimal(maximum);
        this.valueConstraints = valueConstraints;
    }

    public String getUnit() {
        return unit;
    }

    public String getValueConstraints() {
        return valueConstraints;
    }

    /**
     * Checks the range; body fat has an exclusive lower bound.
     */
    public boolean isValidValue(BigDecimal value) {
        int comparison = value.compareTo(minimum);
        return (this == BODYFAT ? comparison > 0 : comparison >= 0) && value.compareTo(maximum) <= 0;
    }

    @Override
    public String toString() {
        return label;
    }
}
