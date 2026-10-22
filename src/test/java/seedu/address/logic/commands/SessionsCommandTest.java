package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks global date filtering, deterministic session/name ordering, counts and unchanged stored data.
 */
public class SessionsCommandTest {
    private final LocalDate date = LocalDate.now().plusDays(2);
    private final Person later = new PersonBuilder().withName("Zoe Review").withNextSession(date + " 17:00").build();
    private final Person earlier = new PersonBuilder().withName("Ben Review")
            .withNextSession(date + " 09:00").build();
    private final Person tied = new PersonBuilder().withName("Ada Review").withNextSession(date + " 09:00").build();
    private final Person otherDay = new PersonBuilder().withName("Dan Review")
            .withNextSession(date.plusDays(1) + " 09:00").build();
    private final Person unbooked = new PersonBuilder().withName("Ella Review").build();
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        for (Person person : List.of(later, earlier, tied, otherDay, unbooked)) {
            model.addPerson(person);
        }
    }

    @Test
    public void execute_replacesPreviousFilterAndKeepsSessionOrder() {
        AddressBook before = new AddressBook(model.getAddressBook());
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Ella")));
        CommandResult result = new SessionsCommand(date).execute(model);
        assertEquals(new CommandResult("Listed 3 clients with sessions on " + date + ".", false, false, true), result);
        assertEquals(List.of(tied, earlier, later), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertEquals(result, new SessionsCommand(date).execute(model));
        new ListCommand().execute(model);
        assertEquals(5, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_oneOrNoSessions_showsSpecificMessage() {
        assertEquals("Listed 1 client with a session on " + date.plusDays(1) + ".",
                new SessionsCommand(date.plusDays(1)).execute(model).getFeedbackToUser());
        assertEquals(List.of(otherDay), model.getFilteredPersonList());
        assertEquals("No sessions recorded on " + date.plusDays(2) + ".",
                new SessionsCommand(date.plusDays(2)).execute(model).getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals("No sessions recorded on " + date + ".",
                new SessionsCommand(date).execute(new ModelManager()).getFeedbackToUser());
    }

    @Test
    public void execute_pastDate_matchesRetainedBookingOnly() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Person expired = new PersonBuilder(unbooked).withNextSession(yesterday + " 17:00").build();
        model.setPerson(unbooked, expired);
        new SessionsCommand(yesterday).execute(model);
        assertEquals(List.of(expired), model.getFilteredPersonList());
    }

    @Test
    public void equals_comparesDate() {
        SessionsCommand command = new SessionsCommand(date);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new SessionsCommand(date)));
        assertFalse(command.equals(new SessionsCommand(date.plusDays(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("sessions"));
    }
}
