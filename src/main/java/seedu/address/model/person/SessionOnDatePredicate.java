package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.function.Predicate;

/**
 * Matches a client's recorded session date, including an expired booking that has not been replaced or cancelled.
 */
public class SessionOnDatePredicate implements Predicate<Person> {
    private final LocalDate date;

    public SessionOnDatePredicate(LocalDate date) {
        this.date = requireNonNull(date);
    }

    @Override
    public boolean test(Person person) {
        return person.getNextSession().map(session -> session.value.toLocalDate().equals(date)).orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SessionOnDatePredicate predicate && date.equals(predicate.date);
    }
}
