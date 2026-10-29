package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Comparator;

// @@author gabriel-wan
/**
 * Orders upcoming sessions by time and then name, followed by expired bookings and unbooked clients.
 * A fixed reference time keeps comparisons consistent throughout each sort.
 */
public class PersonSessionComparator implements Comparator<Person> {
    private final LocalDateTime now;
    private final Comparator<Person> ordering;

    /** Creates one ordering with a fixed reference time, reused for every comparison during a sort. */
    public PersonSessionComparator(LocalDateTime now) {
        this.now = requireNonNull(now);
        ordering = Comparator.comparingInt(this::sessionGroup)
                .thenComparing(person -> person.getNextSession().map(session -> session.value)
                        .orElse(LocalDateTime.MAX))
                .thenComparing(person -> Name.normalizeSpaces(person.getName().fullName),
                        Name.alphabeticalComparator());
    }

    @Override
    public int compare(Person first, Person second) {
        return ordering.compare(first, second);
    }

    private int sessionGroup(Person person) {
        return person.getNextSession().map(session -> session.value.isBefore(now) ? 1 : 0).orElse(2);
    }
}
// @@author
