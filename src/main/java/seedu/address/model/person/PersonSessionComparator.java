package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Orders upcoming sessions by time and then name, followed by expired bookings and unbooked clients.
 * A fixed reference time keeps comparisons consistent throughout each sort.
 */
public class PersonSessionComparator implements Comparator<Person> {
    private final LocalDateTime now;

    public PersonSessionComparator(LocalDateTime now) {
        this.now = requireNonNull(now);
    }

    @Override
    public int compare(Person first, Person second) {
        return Comparator.comparingInt(this::sessionGroup)
                .thenComparing(person -> person.getNextSession().map(session -> session.value)
                        .orElse(LocalDateTime.MAX))
                .thenComparing(person -> person.getName().fullName.trim().replaceAll("\\s+", " "),
                        String.CASE_INSENSITIVE_ORDER)
                .compare(first, second);
    }

    private int sessionGroup(Person person) {
        return person.getNextSession().map(session -> session.value.isBefore(now) ? 1 : 0).orElse(2);
    }
}
