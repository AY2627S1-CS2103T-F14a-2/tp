package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.session.NextSession;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    /** Null when no session has ever been booked; past sessions are retained for display. */
    private final NextSession nextSession;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Measurement> measurements;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, null, List.of());
    }

    /**
     * Creates a client with an optional session and an immutable copy of their measurement records.
     * A single full constructor keeps both features together when a client is copied or restored.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            NextSession nextSession, List<Measurement> measurements) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.nextSession = nextSession;
        this.tags.addAll(tags);
        this.measurements = List.copyOf(measurements);
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * Checks for a record with the same date and type, so callers can distinguish an update from an addition.
     */
    public boolean hasMeasurement(Measurement measurement) {
        requireAllNonNull(measurement);
        return measurements.stream().anyMatch(record -> record.isSameMeasurement(measurement));
    }

    /**
     * Returns a new client with the measurement added, or replaced at its existing position.
     * Other records and client details are preserved; this client remains unchanged.
     * AI assistance: OpenAI Codex helped implement record replacement and its tests.
     */
    public Person withMeasurement(Measurement measurement) {
        requireAllNonNull(measurement);
        List<Measurement> updatedMeasurements = new ArrayList<>(measurements);
        for (int i = 0; i < updatedMeasurements.size(); i++) {
            if (updatedMeasurements.get(i).isSameMeasurement(measurement)) {
                updatedMeasurements.set(i, measurement);
                return new Person(name, phone, email, address, tags, nextSession, updatedMeasurements);
            }
        }
        updatedMeasurements.add(measurement);
        return new Person(name, phone, email, address, tags, nextSession, updatedMeasurements);
    }

    public Optional<NextSession> getNextSession() {
        return Optional.ofNullable(nextSession);
    }

    /**
     * Returns a copy with the supplied session, preserving all other client information.
     */
    public Person withNextSession(NextSession session) {
        return new Person(name, phone, email, address, tags, session, measurements);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name, ignoring case and extra spaces.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().isSameName(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && Objects.equals(nextSession, otherPerson.nextSession)
                && measurements.equals(otherPerson.measurements);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, nextSession, measurements);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("nextSession", nextSession)
                .toString();
    }

}
