package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_viewsClientWithoutChangingData() {
        Person clientToView = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // ModelManager#equals also compares the viewed client
        assertCommandSuccess(new ViewCommand(INDEX_SECOND_PERSON), model, expectedMessage(clientToView),
                expectedModelViewing(clientToView));
    }

    @Test
    public void execute_validIndexFilteredList_viewsClientAndKeepsFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person clientToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<Person> filteredListBefore = new ArrayList<>(model.getFilteredPersonList());

        Model expectedModel = expectedModelViewing(clientToView);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(new ViewCommand(INDEX_FIRST_PERSON), model, expectedMessage(clientToView),
                expectedModel);
        assertEquals(filteredListBefore, model.getFilteredPersonList());
    }

    @Test
    public void execute_anotherClientAlreadyViewed_viewsNewClient() throws Exception {
        Person firstClient = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondClient = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        model.setViewedPerson(firstClient);

        new ViewCommand(INDEX_SECOND_PERSON).execute(model);

        assertEquals(secondClient, model.getViewedPerson().getValue());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new ViewCommand(outOfBoundIndex), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertCommandFailure(new ViewCommand(outOfBoundIndex), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        assertTrue(viewFirstCommand.equals(new ViewCommand(INDEX_FIRST_PERSON)));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewCommand.toString());
    }

    @Test
    public void execute_validIndex_exactWording() throws Exception {
        assertEquals("Showing details and history of Alice Pauline.",
                new ViewCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser());
    }

    private static String expectedMessage(Person viewedClient) {
        return String.format(ViewCommand.MESSAGE_VIEW_SUCCESS, viewedClient.getName().fullName);
    }

    /**
     * Returns a model with the same clients as {@code model}, unfiltered, viewing {@code viewedClient}.
     */
    private Model expectedModelViewing(Person viewedClient) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setViewedPerson(viewedClient);
        return expectedModel;
    }
}
