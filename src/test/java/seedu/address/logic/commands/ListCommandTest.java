package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.getInArabicFormatLocale;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;
    private String expectedManyMessage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedManyMessage = String.format(ListCommand.MESSAGE_SUCCESS,
                model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertListSuccess(model, expectedManyMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertListSuccess(model, expectedManyMessage, expectedModel);
    }

    @Test
    public void execute_clientViewed_showsUpcomingSessionsAgain() {
        model.setViewedPerson(ALICE);
        // expectedModel views no client, and ModelManager#equals compares the viewed client
        assertListSuccess(model, expectedManyMessage, expectedModel);
    }

    @Test
    public void execute_noClients_showsEmptyMessage() {
        Model emptyModel = new ModelManager();
        assertListSuccess(emptyModel, ListCommand.MESSAGE_NO_CLIENTS, new ModelManager());
    }

    @Test
    public void execute_oneClient_showsSingularMessage() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        Model oneClientModel = new ModelManager(addressBook, new UserPrefs());
        Model expectedOneClientModel = new ModelManager(addressBook, new UserPrefs());
        assertListSuccess(oneClientModel, "Listed 1 client.", expectedOneClientModel);
    }

    @Test
    public void execute_oneClientFilteredOut_showsSingularMessage() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        Model oneClientModel = new ModelManager(addressBook, new UserPrefs());
        oneClientModel.updateFilteredPersonList(person -> false);
        Model expectedOneClientModel = new ModelManager(addressBook, new UserPrefs());
        assertListSuccess(oneClientModel, "Listed 1 client.", expectedOneClientModel);
    }

    @Test
    public void execute_anyList_setsScrollToTop() {
        assertTrue(new ListCommand().execute(model).isScrollToTop());
        assertTrue(new ListCommand().execute(new ModelManager()).isScrollToTop());
    }

    @Test
    public void getResultMessage_boundaries() {
        assertEquals("You have no clients yet. Use \"add\" to add your first client.",
                ListCommand.getResultMessage(0));
        assertEquals("Listed 1 client.", ListCommand.getResultMessage(1));
        assertEquals("Listed 2 clients.", ListCommand.getResultMessage(2));
        assertEquals("Listed 7 clients.", ListCommand.getResultMessage(7));
    }

    @Test
    public void getResultMessage_arabicFormatLocale_usesLatinDigits() {
        assertEquals("Listed 7 clients.", getInArabicFormatLocale(() -> ListCommand.getResultMessage(7)));
    }

    @Test
    public void getResultMessage_negativeCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> ListCommand.getResultMessage(-1));
    }

    /**
     * Executes a {@code ListCommand} and confirms that it shows {@code expectedMessage}, scrolls the client list
     * to the top, and leaves {@code actualModel} equal to {@code expectedModel}.
     */
    private static void assertListSuccess(Model actualModel, String expectedMessage, Model expectedModel) {
        CommandResult expectedResult = new CommandResult(expectedMessage, false, false, true);
        assertCommandSuccess(new ListCommand(), actualModel, expectedResult, expectedModel);
    }
}
