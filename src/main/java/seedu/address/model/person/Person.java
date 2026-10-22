package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.PerformanceHistory;
import seedu.address.model.session.NextSession;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: name, phone and tags are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    /** Null when no session has ever been booked; past sessions are retained for display. */
    private final NextSession nextSession;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Measurement> measurements;
    private final PerformanceHistory performanceHistory;

    /**
     * Creates a client with no session or training records. Name, phone and tags must be present and not null.
     *
     * @param email The client's email, or {@code null} if none was given.
     */
    public Person(Name name, Phone phone, Email email, Set<Tag> tags) {
        this(name, phone, email, tags, null, List.of());
    }

    /**
     * Creates a client with an optional session, their measurement records and no gym-performance records.
     */
    public Person(Name name, Phone phone, Email email, Set<Tag> tags,
            NextSession nextSession, List<Measurement> measurements) {
        this(name, phone, email, tags, nextSession, measurements, new PerformanceHistory());
    }

    /**
     * Creates a client with an optional session, an immutable copy of their measurement records,
     * and their gym-performance records.
     * This single full constructor keeps all training information together when a client is copied or restored,
     * so every copy method must pass all of it on.
     */
    public Person(Name name, Phone phone, Email email, Set<Tag> tags,
            NextSession nextSession, List<Measurement> measurements, PerformanceHistory performanceHistory) {
        requireAllNonNull(name, phone, tags, measurements, performanceHistory);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.nextSession = nextSession;
        this.tags.addAll(tags);
        this.measurements = List.copyOf(measurements);
        this.performanceHistory = performanceHistory;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * Returns a copy without measurements matching the given date and type, preserving all other client data.
     * A missing record leaves the measurement contents unchanged.
     */
    public Person withoutMeasurement(LocalDate date, MeasurementType type) {
        requireAllNonNull(date, type);
        List<Measurement> remaining = measurements.stream()
                .filter(record -> !record.getDate().equals(date) || record.getType() != type)
                .toList();
        return new Person(name, phone, email, tags, nextSession, remaining, performanceHistory);
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
                return new Person(name, phone, email, tags, nextSession, updatedMeasurements, performanceHistory);
            }
        }
        updatedMeasurements.add(measurement);
        return new Person(name, phone, email, tags, nextSession, updatedMeasurements, performanceHistory);
    }

    public Optional<NextSession> getNextSession() {
        return Optional.ofNullable(nextSession);
    }

    /**
     * Returns a copy with the supplied session, preserving all other client information.
     */
    public Person withNextSession(NextSession session) {
        return new Person(name, phone, email, tags, session, measurements, performanceHistory);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public PerformanceHistory getPerformanceHistory() {
        return performanceHistory;
    }

    /**
     * Returns the client's gym-performance records as an unmodifiable list, oldest recorded first.
     */
    public List<GymPerformance> getGymPerformances() {
        return performanceHistory.asUnmodifiableList();
    }

    /**
     * Returns true if the client already has a record for the same exercise on the same date as {@code performance}.
     */
    public boolean hasGymPerformance(GymPerformance performance) {
        return performanceHistory.hasSameEntry(performance);
    }

    /**
     * Returns a copy of this client with {@code performance} recorded, replacing any record for the same
     * exercise on the same date. All other client information is preserved.
     */
    public Person withGymPerformance(GymPerformance performance) {
        return new Person(name, phone, email, tags, nextSession, measurements,
                performanceHistory.withRecord(performance));
    }

    /**
     * Returns a copy of this client without the record for the same exercise on the same date as
     * {@code performance}. All other client information, including the session and measurements, is preserved.
     */
    public Person withoutGymPerformance(GymPerformance performance) {
        return new Person(name, phone, email, tags, nextSession, measurements,
                performanceHistory.withoutRecord(performance));
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
                && Objects.equals(email, otherPerson.email)
                && tags.equals(otherPerson.tags)
                && Objects.equals(nextSession, otherPerson.nextSession)
                && measurements.equals(otherPerson.measurements)
                && performanceHistory.equals(otherPerson.performanceHistory);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, tags, nextSession, measurements, performanceHistory);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("tags", tags)
                .add("nextSession", nextSession)
                .add("performanceHistory", performanceHistory)
                .toString();
    }

}
