package seedu.address.storage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;

/**
 * Jackson-friendly measurement with date and decimal value stored as readable strings.
 * AI assistance: OpenAI Codex helped implement measurement storage and its tests.
 */
class JsonAdaptedMeasurement {
    private final String date;
    private final String type;
    private final String value;

    @JsonCreator
    public JsonAdaptedMeasurement(@JsonProperty("date") String date, @JsonProperty("type") String type,
            @JsonProperty("value") String value) {
        this.date = date;
        this.type = type;
        this.value = value;
    }

    public JsonAdaptedMeasurement(Measurement source) {
        date = source.getDate().toString();
        type = source.getType().toString();
        value = source.getValue().toPlainString();
    }

    /**
     * Validates saved records before converting them to immutable model objects.
     */
    public Measurement toModelType() throws IllegalValueException {
        if (date == null || type == null || value == null) {
            throw new IllegalValueException("Measurement date, type and value must be present.");
        }
        try {
            return new Measurement(LocalDate.parse(date), MeasurementType.valueOf(type.toUpperCase(Locale.ROOT)),
                    new BigDecimal(value));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new IllegalValueException("Invalid saved measurement: " + e.getMessage());
        }
    }
}
