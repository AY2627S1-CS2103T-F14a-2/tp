package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalGymPerformances.SQUAT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.progress.ExerciseName;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeletePerformanceCommand}.
 */
public class DeletePerformanceCommandTest {

    private static final ExerciseName BENCH_PRESS_NAME = new ExerciseName("Bench Press");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Gives the client at {@code index} the bench press (18 Sep) and squat (15 Sep) records,
     * and returns the updated client.
     */
    private Person giveRecords(Model model, Index index) {
        Person client = model.getFilteredPersonList().get(index.getZeroBased());
        Person clientWithRecords = client.withGymPerformance(BENCH_PRESS).withGymPerformance(SQUAT);
        model.setPerson(client, clientWithRecords);
        return clientWithRecords;
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new DeletePerformanceCommand(null, BENCH_PRESS.getDate(), BENCH_PRESS_NAME));
        assertThrows(NullPointerException.class, ()
            -> new DeletePerformanceCommand(INDEX_FIRST_PERSON, null, BENCH_PRESS_NAME));
        assertThrows(NullPointerException.class, ()
            -> new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), null));
    }

    @Test
    public void execute_latestRecord_deletesAndRefreshesLatestAndCount() {
        Person client = giveRecords(model, INDEX_FIRST_PERSON);
        DeletePerformanceCommand command =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME);

        String expectedMessage = String.format(DeletePerformanceCommand.MESSAGE_SUCCESS, client.getName(),
                "Bench Press, 3 sets of 8 reps at 60 kg", "2026-09-18");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(client, client.withoutGymPerformance(BENCH_PRESS));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);

        // the latest performance falls back to the older squat record, and the count drops to one
        Person updatedClient = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(Optional.of(SQUAT), updatedClient.getPerformanceHistory().getLatest());
        assertEquals(1, updatedClient.getPerformanceHistory().size());
    }

    @Test
    public void execute_onlyRecord_leavesNoRecords() {
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person clientWithRecord = client.withGymPerformance(SQUAT);
        model.setPerson(client, clientWithRecord);
        DeletePerformanceCommand command =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, SQUAT.getDate(), new ExerciseName("Squat"));

        String expectedMessage = String.format(DeletePerformanceCommand.MESSAGE_SUCCESS, client.getName(),
                "Squat, 3 sets of 5 reps at 100 kg", "2026-09-15");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(clientWithRecord, client);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        Person updatedClient = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(Optional.empty(), updatedClient.getPerformanceHistory().getLatest());
    }

    @Test
    public void execute_differentLetterCaseAndSpacing_deletesRecord() throws Exception {
        Person client = giveRecords(model, INDEX_FIRST_PERSON);
        DeletePerformanceCommand command = new DeletePerformanceCommand(INDEX_FIRST_PERSON,
                BENCH_PRESS.getDate(), new ExerciseName("  bench   PRESS "));

        command.execute(model);

        Person updatedClient = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(List.of(SQUAT), updatedClient.getGymPerformances());
        assertEquals(client.withoutGymPerformance(BENCH_PRESS), updatedClient);
    }

    @Test
    public void execute_filteredList_deletesForDisplayedClientAndKeepsFilter() {
        giveRecords(model, INDEX_SECOND_PERSON);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeletePerformanceCommand command =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME);

        String expectedMessage = String.format(DeletePerformanceCommand.MESSAGE_SUCCESS, client.getName(),
                "Bench Press, 3 sets of 8 reps at 60 kg", "2026-09-18");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(client, client.withoutGymPerformance(BENCH_PRESS));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_recordNotFound_failure() {
        Person client = giveRecords(model, INDEX_FIRST_PERSON);

        // the exercise was not recorded on that date
        LocalDate otherDate = LocalDate.of(2026, 9, 17);
        assertCommandFailure(new DeletePerformanceCommand(INDEX_FIRST_PERSON, otherDate, BENCH_PRESS_NAME), model,
                String.format(DeletePerformanceCommand.MESSAGE_RECORD_NOT_FOUND, "Bench Press",
                        "2026-09-17", client.getName(), INDEX_FIRST_PERSON.getOneBased()));

        // another exercise was recorded on that date
        assertCommandFailure(new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(),
                new ExerciseName("Deadlift")), model,
                String.format(DeletePerformanceCommand.MESSAGE_RECORD_NOT_FOUND, "Deadlift",
                        "2026-09-18", client.getName(), INDEX_FIRST_PERSON.getOneBased()));
    }

    @Test
    public void execute_clientWithoutRecords_failure() {
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertCommandFailure(new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(),
                BENCH_PRESS_NAME), model, String.format(DeletePerformanceCommand.MESSAGE_RECORD_NOT_FOUND,
                "Bench Press", "2026-09-18", client.getName(), INDEX_FIRST_PERSON.getOneBased()));
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new DeletePerformanceCommand(outOfBoundIndex, BENCH_PRESS.getDate(), BENCH_PRESS_NAME),
                model, withUsage(DeletePerformanceCommand.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        giveRecords(model, INDEX_SECOND_PERSON);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        // the second client has the record but is not displayed
        assertCommandFailure(new DeletePerformanceCommand(INDEX_SECOND_PERSON, BENCH_PRESS.getDate(),
                BENCH_PRESS_NAME), model, withUsage(DeletePerformanceCommand.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void equals() {
        DeletePerformanceCommand command =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME);

        // same values -> returns true
        assertTrue(command.equals(
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different index, date or exercise -> returns false
        assertFalse(command.equals(
                new DeletePerformanceCommand(INDEX_SECOND_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME)));
        assertFalse(command.equals(
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, SQUAT.getDate(), BENCH_PRESS_NAME)));
        assertFalse(command.equals(
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), new ExerciseName("Squat"))));
    }

    @Test
    public void toStringMethod() {
        DeletePerformanceCommand command =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS.getDate(), BENCH_PRESS_NAME);
        String expected = DeletePerformanceCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", date=2026-09-18, exerciseName=Bench Press}";
        assertEquals(expected, command.toString());
    }

    private static String withUsage(String message) {
        return Messages.withUsage(message, DeletePerformanceCommand.MESSAGE_USAGE);
    }
}
