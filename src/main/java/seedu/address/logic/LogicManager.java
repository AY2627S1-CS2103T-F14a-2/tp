package seedu.address.logic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_SAVE_FAILURE = "Unable to save Spotter data.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        AddressBook addressBookBeforeCommand = new AddressBook(model.getAddressBook());
        Predicate<Person> predicateBeforeCommand = model.getFilteredPersonListPredicate();
        Person viewedPersonBeforeCommand = model.getViewedPerson().getValue();
        CommandResult commandResult = command.execute(model);
        // Re-sorts by the current time only after the command has used the displayed indices.
        model.updateSessionLists(LocalDateTime.now());

        // Read-only commands leave the data unchanged, so they never write (or fail to write) the data file.
        if (!model.getAddressBook().equals(addressBookBeforeCommand)) {
            saveOrRollBack(addressBookBeforeCommand, predicateBeforeCommand, viewedPersonBeforeCommand);
        }

        // Resolve the command's index against what the user saw before refreshing expired-session order.
        // Recomputing the comparator also refreshes time-dependent client card rows without saving data.
        model.updateFilteredPersonList(model.getFilteredPersonListPredicate());
        return commandResult;
    }

    /**
     * Saves the model's address book to storage. If saving fails (including {@code AccessDeniedException}),
     * restores {@code addressBookBeforeCommand}, {@code predicateBeforeCommand} and
     * {@code viewedPersonBeforeCommand} so that the app keeps showing the last saved state with the same
     * displayed list and viewed client.
     */
    private void saveOrRollBack(AddressBook addressBookBeforeCommand, Predicate<Person> predicateBeforeCommand,
            Person viewedPersonBeforeCommand) throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException ioe) {
            logger.warning("Unable to save data file, undoing the change: " + ioe);
            model.setAddressBook(addressBookBeforeCommand);
            model.updateFilteredPersonList(predicateBeforeCommand);
            model.setViewedPerson(viewedPersonBeforeCommand);
            throw new CommandException(MESSAGE_SAVE_FAILURE, ioe);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Person> getUpcomingSessionList() {
        return model.getUpcomingSessionList();
    }

    @Override
    public ObservableValue<Person> getViewedPerson() {
        return model.getViewedPerson();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
