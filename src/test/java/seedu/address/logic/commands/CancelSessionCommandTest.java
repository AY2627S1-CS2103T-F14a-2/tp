package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SessionOnDatePredicate;
import seedu.address.testutil.PersonBuilder;

/**
 * Covers cancellation by displayed index and by name, filter behaviour, the index-shift note and preservation
 * of all non-session data.
 */
public class CancelSessionCommandTest {
    private final LocalDate date = LocalDate.now().plusDays(2);
    private final Person booked = new PersonBuilder().withName("Zoe Review").withoutEmail().withTags("strength")
            .withNextSession(date + " 09:00")
            .withMeasurements(new Measurement(LocalDate.now().minusDays(1), MeasurementType.WEIGHT,
                    new BigDecimal("72.4")))
            .withGymPerformances(BENCH_PRESS).build();
    private final Person other = new PersonBuilder().withName("Ada Review")
            .withNextSession(date + " 17:00").build();
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(other);
        model.addPerson(booked);
    }

    @Test
    public void execute_sortedIndex_preservesRecordsAndResorts() throws Exception {
        assertEquals(List.of(booked, other), model.getFilteredPersonList());
        CommandResult result = new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        Person cancelled = new PersonBuilder(booked).withNextSession(null).build();
        assertEquals(new CommandResult("Cancelled session on " + date + " 09:00 for Zoe Review."
                + ClientOrderNote.MESSAGE,
                false, false, true), result);
        assertEquals(List.of(other, cancelled), model.getFilteredPersonList());
        assertEquals(List.of(other, cancelled), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_lastClientInDateView_warnsWithoutClaimingOtherClientsMoved() throws Exception {
        model.updateFilteredPersonList(new SessionOnDatePredicate(date));
        CommandResult result = new CancelSessionCommand("Ada Review").execute(model);
        assertTrue(result.getFeedbackToUser().endsWith(ClientOrderNote.MESSAGE));
        assertFalse(result.getFeedbackToUser().contains("clients below"));
        assertEquals(List.of(booked), model.getFilteredPersonList());
    }

    @Test
    public void execute_lastClientInFullList_keepsIndicesWithoutWarning() throws Exception {
        CommandResult result = new CancelSessionCommand(Index.fromOneBased(2)).execute(model);
        assertEquals("Cancelled session on " + date + " 17:00 for Ada Review.", result.getFeedbackToUser());
        assertEquals(List.of(booked, other.withNextSession(null)), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameFilteredIndex_preservesFilter() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Zoe")));
        new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(booked.withNextSession(null)), model.getFilteredPersonList());
        assertEquals(other, model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_nameOutsideDisplayedList_preservesFilterAndProgress() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Ada")));
        new CancelSessionCommand(" zOe   rEvIeW ").execute(model);
        assertEquals(List.of(other), model.getFilteredPersonList());
        assertEquals(List.of(other, booked.withNextSession(null)), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_unknownOrPartialName_keepsData() {
        assertCommandFailure(new CancelSessionCommand("Zoe"), model, SessionCommand.MESSAGE_NO_SUCH_NAME);
        assertCommandFailure(new CancelSessionCommand("Unknown"), model, SessionCommand.MESSAGE_NO_SUCH_NAME);
    }

    @Test
    public void execute_namedClientWithoutSession_keepsData() throws Exception {
        new CancelSessionCommand("Zoe Review").execute(model);
        assertCommandFailure(new CancelSessionCommand("Zoe Review"), model,
                "No session is recorded for Zoe Review.");
    }

    @Test
    public void execute_dateFilteredIndex_removesCancelledClientFromView() throws Exception {
        model.updateFilteredPersonList(new SessionOnDatePredicate(date));
        new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(other), model.getFilteredPersonList());
        // The next index now refers to the other client, not the previously cancelled one.
        new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(List.of(other.withNextSession(null), booked.withNextSession(null)),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_dateFilteredIndex_notesThatClientsShifted() throws Exception {
        model.updateFilteredPersonList(new SessionOnDatePredicate(date));
        CommandResult result = new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertEquals("Cancelled session on " + date + " 09:00 for Zoe Review." + ClientOrderNote.MESSAGE,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_normalizedNameInFullList_warnsWhenClientMoves() throws Exception {
        CommandResult result = new CancelSessionCommand("  zoe   REVIEW ").execute(model);
        assertEquals("Cancelled session on " + date + " 09:00 for Zoe Review." + ClientOrderNote.MESSAGE,
                result.getFeedbackToUser());
        assertEquals(booked.withNextSession(null), model.getAddressBook().getPersonList().get(1));
        assertEquals(other, model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_nameHiddenByFilter_cancelsWithoutNote() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Ada")));
        CommandResult result = new CancelSessionCommand("Zoe Review").execute(model);
        // Zoe was never shown, so no displayed index moved
        assertEquals("Cancelled session on " + date + " 09:00 for Zoe Review.", result.getFeedbackToUser());
        assertEquals(List.of(other), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedNameInDateView_neverCancelsNextClient() throws Exception {
        // Issue #134: repeating cancelsession 1 here would cancel bob after Alice
        Person alice = new PersonBuilder().withName("Alice Ong").withNextSession(date + " 08:00").build();
        Person bob = new PersonBuilder().withName("bob Lee").withNextSession(date + " 08:00").build();
        Person carl = new PersonBuilder().withName("Carl Ng").withNextSession(date + " 08:00").build();
        Model sameDay = new ModelManager();
        sameDay.addPerson(alice);
        sameDay.addPerson(bob);
        sameDay.addPerson(carl);
        sameDay.updateFilteredPersonList(new SessionOnDatePredicate(date));
        assertEquals(List.of(alice, bob, carl), sameDay.getFilteredPersonList());

        CommandResult result = new CancelSessionCommand("Alice Ong").execute(sameDay);
        assertTrue(result.getFeedbackToUser().endsWith(ClientOrderNote.MESSAGE));
        assertCommandFailure(new CancelSessionCommand("Alice Ong"), sameDay, "No session is recorded for Alice Ong.");
        assertEquals(List.of(bob, carl), sameDay.getFilteredPersonList());
    }

    @Test
    public void execute_expiredSession_canBeRemoved() throws Exception {
        Person expired = new PersonBuilder(booked).withNextSession(LocalDate.now().minusDays(1) + " 09:00").build();
        model.setPerson(booked, expired);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Zoe")));
        CommandResult result = new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertEquals("Cancelled session on " + LocalDate.now().minusDays(1) + " 09:00 for Zoe Review.",
                result.getFeedbackToUser());
        assertEquals(List.of(expired.withNextSession(null)), model.getFilteredPersonList());
    }

    @Test
    public void execute_missingSessionOrInvalidDisplayedIndex_keepsDataAndFilter() throws Exception {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Zoe")));
        assertCommandFailure(new CancelSessionCommand(Index.fromOneBased(2)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        new CancelSessionCommand(Index.fromOneBased(1)).execute(model);
        assertCommandFailure(new CancelSessionCommand(Index.fromOneBased(1)), model,
                "No session is recorded for Zoe Review.");
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new CancelSessionCommand(Index.fromOneBased(1)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_comparesDisplayedIndex() {
        CancelSessionCommand command = new CancelSessionCommand(Index.fromOneBased(1));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new CancelSessionCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(new CancelSessionCommand(Index.fromOneBased(2))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("cancelsession"));
        assertFalse(command.equals(new CancelSessionCommand("Zoe Review")));
        assertEquals(new CancelSessionCommand("Zoe Review"), new CancelSessionCommand("Zoe   Review"));
        assertFalse(new CancelSessionCommand("Zoe Review").equals(new CancelSessionCommand("Ada Review")));
    }
}
