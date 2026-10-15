package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.session.NextSession;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String nextSession;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedMeasurement> measurements = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, List<JsonAdaptedTag> tags) {
        this(name, phone, email, tags, null, null);
    }

    /**
     * Loads optional session and measurement data, accepting older files without either field.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("nextSession") String nextSession,
            @JsonProperty("measurements") List<JsonAdaptedMeasurement> measurements) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nextSession = nextSession;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (measurements != null) {
            this.measurements.addAll(measurements);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().map(e -> e.value).orElse(null);
        nextSession = source.getNextSession().map(NextSession::toString).orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        measurements.addAll(source.getMeasurements().stream().map(JsonAdaptedMeasurement::new).toList());
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        // Email is optional, so a missing email is stored as null rather than rejected.
        if (email != null && !Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = email == null ? null : new Email(email);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        if (nextSession != null && !NextSession.isValidNextSession(nextSession)) {
            throw new IllegalValueException(NextSession.MESSAGE_CONSTRAINTS);
        }
        final NextSession modelSession = nextSession == null ? null : new NextSession(nextSession);
        final List<Measurement> modelMeasurements = new ArrayList<>();
        for (JsonAdaptedMeasurement measurement : measurements) {
            if (measurement == null) {
                throw new IllegalValueException("Measurement records must not be null.");
            }
            Measurement record = measurement.toModelType();
            if (modelMeasurements.stream().anyMatch(existing -> existing.isSameMeasurement(record))) {
                throw new IllegalValueException("Duplicate measurement date and type.");
            }
            modelMeasurements.add(record);
        }
        return new Person(modelName, modelPhone, modelEmail, modelTags, modelSession, modelMeasurements);
    }

}
