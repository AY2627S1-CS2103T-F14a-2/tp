package seedu.address.storage;

import static seedu.address.commons.util.AppUtil.checkArgument;

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
     * AI assistance: Helen used OpenAI Codex to validate stored date years and add regression tests for issue #145.
     */
    public Measurement toModelType() throws IllegalValueException {
        if (date == null || type == null || value == null) {
            throw new IllegalValueException("Measurement date, type and value must be present.");
        }
        try {
            checkArgument(date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}") && !date.startsWith("0000"),
                    "Measurement date must use yyyy-MM-dd format with a year from 0001 to 9999.");
            return new Measurement(LocalDate.parse(date), MeasurementType.valueOf(type.toUpperCase(Locale.ROOT)),
                    new BigDecimal(value));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new IllegalValueException("Invalid saved measurement: " + e.getMessage());
        }
    }
}
