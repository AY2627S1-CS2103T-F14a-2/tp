package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks replacement, displayed indices after sorting/filtering, exact messages, and unchanged data on failure.
 */
public class SessionCommandTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 26, 14, 0);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(NOW.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    private static final String NORMAL_MESSAGE = "Recorded next session for Sarah Lim on %s at %s";
    private static final String PAST_NOTE = "\n" + SessionCommand.MESSAGE_PAST_SESSION;
    private final Person sarah = new PersonBuilder().withName("Sarah Lim").build();
    private final Person alex = new PersonBuilder().withName("Alex Tan").build();
    private final NextSession session = new NextSession(LocalDate.now().plusDays(1) + " 17:00");
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(sarah);
        model.addPerson(alex);
    }

    @Test
    public void execute_displayedIndex_recordsAndSorts() throws Exception {
        CommandResult result = new SessionCommand(Index.fromOneBased(2), session).execute(model);
        Person booked = sarah.withNextSession(session);
        assertEquals("Recorded next session for Sarah Lim on " + session.value.toLocalDate() + " at 17:00",
                result.getFeedbackToUser());
        assertEquals(List.of(booked, alex), model.getFilteredPersonList());
        // Sorting is a display transform; storage remains in its original order.
        assertEquals(List.of(booked, alex), model.getAddressBook().getPersonList());
        assertTrue(result.isScrollToTop());
    }

    @Test
    public void execute_nameIgnoringCaseAndSpaces_looksThroughAllClients() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alex")));
        new SessionCommand("  sArAh   lIm ", session).execute(model);
        assertEquals(List.of(sarah.withNextSession(session), alex), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredIndex_targetsDisplayedClient() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Sarah")));
        new SessionCommand(Index.fromOneBased(1), session).execute(model);
        assertEquals(session, model.getFilteredPersonList().getFirst().getNextSession().orElseThrow());
        assertEquals(alex, model.getFilteredPersonList().getLast());
    }

    @Test
    public void execute_existingSession_replacesWithoutChangingContacts() throws Exception {
        new SessionCommand("Sarah Lim", session).execute(model);
        NextSession replacement = new NextSession(LocalDate.now().plusDays(2) + " 09:05");
        CommandResult result = new SessionCommand("Sarah Lim", replacement).execute(model);
        assertEquals("Rescheduled next session for Sarah Lim from " + session + " to " + replacement + ".",
                result.getFeedbackToUser());
        assertEquals(sarah.withNextSession(replacement), model.getFilteredPersonList().getFirst());
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_sameBooking_reportsUnchangedAndShowsAllClients() throws Exception {
        new SessionCommand("Sarah Lim", session).execute(model);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Alex")));
        CommandResult result = new SessionCommand("Sarah Lim", session).execute(model);
        assertEquals("Next session for Sarah Lim is already recorded on " + session + ".",
                result.getFeedbackToUser());
        assertEquals(List.of(sarah.withNextSession(session), alex), model.getFilteredPersonList());
        assertTrue(result.isScrollToTop());
    }

    @Test
    public void execute_clashOutsideFilter_warnsAndKeepsBothBookings() throws Exception {
        model.setPerson(alex, alex.withNextSession(session));
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Sarah")));
        CommandResult result = new SessionCommand(Index.fromOneBased(1), session).execute(model);
        assertTrue(result.getFeedbackToUser().endsWith(
                "\nWarning: This time is also booked for Alex Tan. All bookings are kept."));
        assertEquals(List.of(alex.withNextSession(session), sarah.withNextSession(session)),
                model.getFilteredPersonList());
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_multipleClashes_namesAreAlphabeticalAndExcludeTarget() throws Exception {
        model.setPerson(alex, alex.withNextSession(session));
        model.setPerson(sarah, sarah.withNextSession(session));
        model.addPerson(new PersonBuilder().withName("Ben Test").withNextSession(session.toString()).build());
        CommandResult result = new SessionCommand("Sarah Lim", session).execute(model);
        assertEquals("Next session for Sarah Lim is already recorded on " + session + "."
                + "\nWarning: This time is also booked for Alex Tan, Ben Test. All bookings are kept.",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_differentTimesOrOwnOldBooking_doesNotWarn() throws Exception {
        model.setPerson(alex, alex.withNextSession(new NextSession(session.value.toLocalDate() + " 17:01")));
        model.setPerson(sarah, sarah.withNextSession(session));
        CommandResult result = new SessionCommand("Sarah Lim", session).execute(model);
        assertFalse(result.getFeedbackToUser().contains("Warning:"));
    }

    @Test
    public void execute_earlierCalendarDate_recordsWithPastNote() throws Exception {
        // #117: a valid past date is recorded, not rejected, so a trainer can log a session late
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-25", "17:00") + PAST_NOTE,
                bookSarahAt("2026-10-25 17:00"));
        assertEquals(new NextSession("2026-10-25 17:00"),
                model.getAddressBook().getPersonList().getFirst().getNextSession().orElseThrow());
    }

    @Test
    public void execute_earlierTimeToday_recordsWithPastNote() throws Exception {
        // #132: today's date is not enough; a time that has passed today gets the same note
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-26", "13:59") + PAST_NOTE,
                bookSarahAt("2026-10-26 13:59"));
        assertEquals(new NextSession("2026-10-26 13:59"),
                model.getAddressBook().getPersonList().getFirst().getNextSession().orElseThrow());
    }

    @Test
    public void execute_earlierTimeTodayByName_recordsWithPastNote() throws Exception {
        CommandResult result = new SessionCommand("Sarah Lim", new NextSession("2026-10-26 13:59"), FIXED_CLOCK)
                .execute(model);
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-26", "13:59") + PAST_NOTE,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_laterTodayOrFutureDate_noPastNote() throws Exception {
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-26", "14:01"), bookSarahAt("2026-10-26 14:01"));
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-27", "09:00"), bookSarahAt("2026-10-27 09:00"));
    }

    @Test
    public void execute_exactlyNow_noPastNote() throws Exception {
        // Matches the card, which still shows a session at exactly "now" as upcoming
        assertEquals(String.format(NORMAL_MESSAGE, "2026-10-26", "14:00"), bookSarahAt("2026-10-26 14:00"));
    }

    @Test
    public void execute_rescheduleIntoPastWithClash_keepsAllFeedback() throws Exception {
        // #123 feedback comes first, then the clash warning, then the past note
        NextSession earlierToday = new NextSession("2026-10-26 09:00");
        model.setPerson(alex, alex.withNextSession(earlierToday));
        model.setPerson(sarah, sarah.withNextSession(session));
        CommandResult result = new SessionCommand("Sarah Lim", earlierToday, FIXED_CLOCK).execute(model);
        assertEquals("Rescheduled next session for Sarah Lim from " + session + " to " + earlierToday + "."
                + "\nWarning: This time is also booked for Alex Tan. All bookings are kept." + PAST_NOTE,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_missingIndexOrName_keepsDataAndFilter() {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Sarah")));
        assertCommandFailure(new SessionCommand(Index.fromOneBased(2), session), model,
                SessionCommand.MESSAGE_INVALID_INDEX);
        assertCommandFailure(new SessionCommand("Sarah", session), model, SessionCommand.MESSAGE_NO_SUCH_NAME);
        assertCommandFailure(new SessionCommand("Unknown", session), model, SessionCommand.MESSAGE_NO_SUCH_NAME);
    }

    @Test
    public void execute_editPreservesSessionAndDeleteRemovesIt() throws Exception {
        new SessionCommand("Sarah Lim", session).execute(model);
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setPhone(alex.getPhone());
        new EditCommand(Index.fromOneBased(1), descriptor).execute(model);
        assertEquals(session, model.getFilteredPersonList().getFirst().getNextSession().orElseThrow());
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(alex), model.getAddressBook().getPersonList());
    }

    /**
     * Books Sarah, the second displayed client, at {@code dateTime} with the clock fixed at {@code NOW}, and
     * returns the feedback message. The clients are reset first, as booking re-sorts the list.
     */
    private String bookSarahAt(String dateTime) throws Exception {
        setUp();
        return new SessionCommand(Index.fromOneBased(2), new NextSession(dateTime), FIXED_CLOCK).execute(model)
                .getFeedbackToUser();
    }

    @Test
    public void equals_identifierAndSessionParticipate() {
        SessionCommand command = new SessionCommand("Sarah Lim", session);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new SessionCommand("Sarah   Lim", session)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("session"));
        assertFalse(command.equals(new SessionCommand("Alex Tan", session)));
        assertFalse(command.equals(new SessionCommand(Index.fromOneBased(1), session)));
        assertTrue(new SessionCommand(Index.fromOneBased(1), session)
                .equals(new SessionCommand(Index.fromOneBased(1), session)));
    }
}
