package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.AppPaths;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.4.0";

    public static final String MESSAGE_DATA_LOAD_FAILURE =
            "Unable to load saved Spotter data. The existing data file has not been modified.";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = AppPaths.getUserPrefsFilePath();
    private static final Path ADDRESS_BOOK_FILE_PATH = AppPaths.getAddressBookFilePath();

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    /** Warning to show once the UI starts, or empty if the data file was loaded without problems. */
    private Optional<String> startupWarning = Optional.empty();

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing Spotter ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     * In the latter case, a startup warning is recorded and the data file is left untouched; it is only
     * overwritten by the next successful data-changing command.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Data file " + storage.getAddressBookFilePath()
                        + " not found. Starting with sample Spotter data.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with empty Spotter data.");
            initialData = new AddressBook();
            startupWarning = Optional.of(MESSAGE_DATA_LOAD_FAILURE);
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Spotter " + MainApp.VERSION);
        ui.start(primaryStage);
        startupWarning.ifPresent(ui::showMessage);
    }

    /**
     * Returns the warning to show once the UI starts, if any.
     */
    Optional<String> getStartupWarning() {
        return startupWarning;
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Spotter ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
