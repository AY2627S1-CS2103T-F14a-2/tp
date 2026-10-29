package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.PersonBuilder;

// @@author gabriel-wan
/**
 * Checks chronological priority, name ties, expired/unbooked rows, and stable stored order.
 */
public class PersonSessionComparatorTest {
    @Test
    public void compare_upcomingThenExpiredThenUnbooked_withCaseInsensitiveNameTies() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 12, 0);
        Person soon = new PersonBuilder().withName("Zed").withNextSession("2026-10-12 13:00").build();
        Person alex = new PersonBuilder().withName("alex").withNextSession("2026-10-13 09:00").build();
        Person bert = new PersonBuilder().withName("Bert").withNextSession("2026-10-13 09:00").build();
        Person expired = new PersonBuilder().withName("Aaron").withNextSession("2026-10-12 11:00").build();
        Person unbookedAmy = new PersonBuilder().withName("amy").build();
        Person unbookedBob = new PersonBuilder().withName("Bob").build();
        List<Person> shuffled = List.of(unbookedBob, bert, expired, alex, unbookedAmy, soon);
        assertEquals(List.of(soon, alex, bert, expired, unbookedAmy, unbookedBob),
                shuffled.stream().sorted(new PersonSessionComparator(now)).toList());
    }

    @Test
    public void compare_accentedLatinNames_usesEnglishOrderWithinEachSessionGroup() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 12, 0);
        List<Person> clients = List.of(new PersonBuilder().withName("Zoe").build(),
                new PersonBuilder().withName("Émile").build(), new PersonBuilder().withName("amy").build());
        PersonSessionComparator comparator = new PersonSessionComparator(now);
        List<String> expectedNames = List.of("amy", "Émile", "Zoe");
        assertEquals(expectedNames, clients.stream().sorted(comparator).map(p -> p.getName().fullName).toList());
        for (String booking : List.of("2026-10-12 13:00", "2026-10-12 11:00")) {
            List<Person> booked = clients.stream()
                    .map(p -> new PersonBuilder(p).withNextSession(booking).build()).toList();
            assertEquals(expectedNames, booked.stream().sorted(comparator).map(p -> p.getName().fullName).toList());
        }
    }

    @Test
    public void model_startupAndFilter_sortWithoutChangingStorageOrder() {
        Person zed = new PersonBuilder().withName("Zed").build();
        Person amy = new PersonBuilder().withName("amy").build();
        AddressBook stored = new AddressBook();
        stored.addPerson(zed);
        stored.addPerson(amy);
        ModelManager model = new ModelManager(stored, new UserPrefs());
        assertEquals(List.of(amy, zed), model.getFilteredPersonList());
        assertEquals(List.of(zed, amy), model.getAddressBook().getPersonList());
        model.updateFilteredPersonList(person -> person.getName().fullName.equals("Zed"));
        assertEquals(List.of(zed), model.getFilteredPersonList());
        model.updateFilteredPersonList(person -> true);
        assertEquals(List.of(amy, zed), model.getFilteredPersonList());
    }
}
// @@author
