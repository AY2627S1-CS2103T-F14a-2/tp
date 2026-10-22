package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Verifies that date filtering matches the recorded date rather than time-of-day or client name.
 */
public class SessionOnDatePredicateTest {
    private final LocalDate date = LocalDate.of(2026, 10, 22);

    @Test
    public void test_matchesOnlyRecordedDate() {
        SessionOnDatePredicate predicate = new SessionOnDatePredicate(date);
        assertTrue(predicate.test(new PersonBuilder().withNextSession(date + " 00:00").build()));
        assertTrue(predicate.test(new PersonBuilder().withNextSession(date + " 23:59").build()));
        assertFalse(predicate.test(new PersonBuilder().withNextSession(date.minusDays(1) + " 23:59").build()));
        assertFalse(predicate.test(new PersonBuilder().withNextSession(date.plusDays(1) + " 00:00").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void equals_comparesDate() {
        SessionOnDatePredicate predicate = new SessionOnDatePredicate(date);
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new SessionOnDatePredicate(date)));
        assertFalse(predicate.equals(new SessionOnDatePredicate(date.plusDays(1))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("sessions"));
    }
}
