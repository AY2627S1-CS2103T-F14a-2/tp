package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PerformanceCommand;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");
    private static final String ADD_AMY_COMMAND = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
            + EMAIL_DESC_AMY;
    private static final String PERFORMANCE_COMMAND = PerformanceCommand.COMMAND_WORD
            + " 1 d/2026-09-18 e/Bench Press s/3 r/8 w/60";

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_clearWithArguments_preservesClientsFilterViewedClientAndStoredData() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.updateFilteredPersonList(person -> person.isSamePerson(BENSON));
        model.setViewedPerson(ALICE);
        Path file = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(file).saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(file);
        AddressBook before = new AddressBook(model.getAddressBook());

        for (String argument : List.of("1", "all", "n/Alice", "confirm now")) {
            assertThrows(ParseException.class, ClearCommand.MESSAGE_EXTRA_PARAMETERS, () -> {
                logic.execute("clear " + argument);
            });
            assertEquals(before, model.getAddressBook());
            assertEquals(List.of(BENSON), model.getFilteredPersonList());
            assertEquals(ALICE, model.getViewedPerson().getValue());
            assertEquals(savedData, Files.readString(file));
        }
    }

    @Test
    public void execute_clearWithoutConfirm_preservesClientsFilterViewedClientAndStoredData() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.updateFilteredPersonList(person -> person.isSamePerson(BENSON));
        model.setViewedPerson(ALICE);
        Path file = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(file).saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(file);
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult result = logic.execute("clear");

        assertEquals(ClearCommand.MESSAGE_CONFIRMATION_REQUIRED, result.getFeedbackToUser());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertEquals(ALICE, model.getViewedPerson().getValue());
        assertEquals(savedData, Files.readString(file));
    }

    @Test
    public void execute_clearConfirm_clearsClientsAndSavesEmptyData() throws Exception {
        model.addPerson(ALICE);
        Path file = temporaryFolder.resolve("addressBook.json");

        assertCommandSuccess("clear confirm", ClearCommand.MESSAGE_SUCCESS, new ModelManager());
        assertEquals(new AddressBook(), new JsonAddressBookStorage(file).readAddressBook().orElseThrow());
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_NO_CLIENTS, model);
    }

    @Test
    public void execute_dataChangingCommand_savesDataFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        logic.execute(ADD_AMY_COMMAND);
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void execute_addWithEmptyEmail_savesClientWithoutEmail() throws Exception {
        Path dataFile = temporaryFolder.resolve("addressBook.json");

        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + " e/");

        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow();
        assertEquals(Optional.empty(), savedData.getPersonList().get(0).getEmail());
    }

    @Test
    public void execute_hyphenatedEmailDomain_addEditAndReloadPreserveEmail() throws Exception {
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        logic.execute("add n/Email Probe p/91234567 e/a@b.c-d");
        ReadOnlyAddressBook added = new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow();
        assertEquals("a@b.c-d", added.getPersonList().get(0).getEmail().orElseThrow().value);

        logic.execute("edit 1 e/a@x--y.com");
        ReadOnlyAddressBook edited = new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow();
        assertEquals("a@x--y.com", edited.getPersonList().get(0).getEmail().orElseThrow().value);
        assertEquals(model.getAddressBook(), new AddressBook(edited));
    }

    @Test
    public void execute_readOnlyCommand_doesNotSaveDataFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        logic.execute(ListCommand.COMMAND_WORD);
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void execute_viewCommand_doesNotSaveDataFile() throws Exception {
        model.addPerson(ALICE);
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        logic.execute(ViewCommand.COMMAND_WORD + " 1");
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void execute_storageThrowsIoExceptionWithExistingClients_restoresPreviousState() {
        model.addPerson(new PersonBuilder().build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        logic = new LogicManager(model, createStorageThrowing(DUMMY_IO_EXCEPTION));

        assertCommandFailure("clear confirm", CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE,
                expectedModel);
    }

    @Test
    public void execute_addFailsToSaveWhileFiltered_displayedListUnchanged() {
        assertSaveFailureKeepsFilteredList(ADD_AMY_COMMAND);
    }

    @Test
    public void execute_editFailsToSaveWhileFiltered_displayedListUnchanged() {
        assertSaveFailureKeepsFilteredList(EditCommand.COMMAND_WORD + " 1" + PHONE_DESC_AMY);
    }

    @Test
    public void execute_performanceCommand_savesRecordToDataFile() throws Exception {
        // Saving is triggered by Person#equals detecting a change, so a new record must reach the data file
        model.addPerson(ALICE);
        Path dataFile = temporaryFolder.resolve("addressBook.json");

        logic.execute(PERFORMANCE_COMMAND);

        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow();
        assertEquals(List.of(BENCH_PRESS), savedData.getPersonList().get(0).getGymPerformances());
    }

    @Test
    public void execute_performanceFailsToSave_restoresPreviousState() {
        model.addPerson(ALICE);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        logic = new LogicManager(model, createStorageThrowing(DUMMY_IO_EXCEPTION));

        assertCommandFailure(PERFORMANCE_COMMAND, CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE,
                expectedModel);
    }

    @Test
    public void execute_viewedClientChangeFailsToSave_viewedClientRestored() throws Exception {
        viewAlice();
        logic = new LogicManager(model, createStorageThrowing(DUMMY_IO_EXCEPTION));

        assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () ->
                logic.execute(PERFORMANCE_COMMAND));
        assertEquals(ALICE, logic.getViewedPerson().getValue());
    }

    @Test
    public void execute_viewedClientChangeSaved_viewedClientUpdated() throws Exception {
        viewAlice();

        logic.execute(PERFORMANCE_COMMAND);

        assertEquals(List.of(BENCH_PRESS), logic.getViewedPerson().getValue().getGymPerformances());
    }

    @Test
    public void execute_sessionBooked_clientInUpcomingSessions() throws Exception {
        model.addPerson(ALICE);

        logic.execute(SessionCommand.COMMAND_WORD + " 1 d/2099-12-01 t/1800");

        assertEquals(List.of(ALICE.getName()),
                logic.getUpcomingSessionList().stream().map(Person::getName).toList());
    }

    @Test
    public void execute_readOnlyCommandWhenStorageThrows_success() throws Exception {
        logic = new LogicManager(model, createStorageThrowing(DUMMY_IO_EXCEPTION));
        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_NO_CLIENTS, new ModelManager());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        logic = new LogicManager(model, createStorageThrowing(e));

        // Triggers the saveAddressBook method by executing an add command
        Person addedPerson = new PersonBuilder(AMY).withTags().build();

        // The failed save must undo the add, leaving the model in its pre-command state
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(ADD_AMY_COMMAND, CommandException.class, expectedMessage, expectedModel);
        assertFalse(model.hasPerson(addedPerson));
    }

    /**
     * Filters the list to one of two clients, executes the data-changing {@code inputCommand} with storage that
     * fails to save, and confirms that both the clients and the displayed list are restored.
     */
    private void assertSaveFailureKeepsFilteredList(String inputCommand) {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.updateFilteredPersonList(person -> person.isSamePerson(BENSON));
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        logic = new LogicManager(model, createStorageThrowing(DUMMY_IO_EXCEPTION));

        assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () -> logic.execute(inputCommand));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    /**
     * Adds ALICE as the only client and views her with the {@code view} command.
     */
    private void viewAlice() throws Exception {
        model.addPerson(ALICE);
        logic.execute(ViewCommand.COMMAND_WORD + " 1");
    }

    /**
     * Returns a {@code StorageManager} whose address book storage throws {@code e} when saving.
     */
    private StorageManager createStorageThrowing(IOException e) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(prefPath);
        return new StorageManager(addressBookStorage, userPrefsStorage);
    }
}
