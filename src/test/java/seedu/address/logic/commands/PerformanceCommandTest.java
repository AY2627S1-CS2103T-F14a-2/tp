package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalGymPerformances.PULL_UP;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code PerformanceCommand}.
 */
public class PerformanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new PerformanceCommand(null, BENCH_PRESS));
        assertThrows(NullPointerException.class, ()
            -> new PerformanceCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_newRecord_recordsPerformance() {
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        PerformanceCommand command = new PerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS);

        String expectedMessage = String.format(PerformanceCommand.MESSAGE_RECORDED, client.getName(),
                "Bench Press, 3 sets of 8 reps at 60 kg", "2026-09-18");

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(client, client.withGymPerformance(BENCH_PRESS));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameExerciseAndDate_replacesRecord() {
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person clientWithRecord = client.withGymPerformance(BENCH_PRESS);
        model.setPerson(client, clientWithRecord);

        // the exercise name differs only in letter case and spacing
        GymPerformance correction = new GymPerformance(BENCH_PRESS.getDate(), new ExerciseName("bench  press"),
                1, 1, new Load("62.5"));
        PerformanceCommand command = new PerformanceCommand(INDEX_FIRST_PERSON, correction);

        String expectedMessage = String.format(PerformanceCommand.MESSAGE_UPDATED, client.getName(),
                "bench press, 1 set of 1 rep at 62.5 kg", "2026-09-18");

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(clientWithRecord, clientWithRecord.withGymPerformance(correction));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(1, model.getFilteredPersonList().get(0).getGymPerformances().size());
    }

    @Test
    public void execute_filteredList_recordsForDisplayedClientAndKeepsFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person client = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        PerformanceCommand command = new PerformanceCommand(INDEX_FIRST_PERSON, PULL_UP);

        String expectedMessage = String.format(PerformanceCommand.MESSAGE_RECORDED, client.getName(),
                "Pull-up, 1 set of 1 rep at 0 kg", "2026-09-18");

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(client, client.withGymPerformance(PULL_UP));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        PerformanceCommand command = new PerformanceCommand(outOfBoundIndex, BENCH_PRESS);

        assertCommandFailure(command, model, withUsage(PerformanceCommand.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        // the second client exists in the address book but is not displayed
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        PerformanceCommand command = new PerformanceCommand(INDEX_SECOND_PERSON, BENCH_PRESS);

        assertCommandFailure(command, model, withUsage(PerformanceCommand.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void equals() {
        PerformanceCommand command = new PerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS);

        // same values -> returns true
        assertTrue(command.equals(new PerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand(true)));

        // different index -> returns false
        assertFalse(command.equals(new PerformanceCommand(INDEX_SECOND_PERSON, BENCH_PRESS)));

        // different performance -> returns false
        assertFalse(command.equals(new PerformanceCommand(INDEX_FIRST_PERSON, PULL_UP)));
    }

    @Test
    public void toStringMethod() {
        PerformanceCommand command = new PerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS);
        String expected = PerformanceCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", performance=" + BENCH_PRESS + "}";
        assertEquals(expected, command.toString());
    }

    private static String withUsage(String message) {
        return Messages.withUsage(message, PerformanceCommand.MESSAGE_USAGE);
    }
}
