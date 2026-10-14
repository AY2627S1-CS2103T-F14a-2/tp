package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;

public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    private Storage createStorage(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
    }

    @Test
    public void initModelManager_missingFile_loadsSampleDataWithoutWarning() {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(SampleDataUtil.getSampleAddressBook()), model.getAddressBook());
        assertEquals(Optional.empty(), app.getStartupWarning());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void initModelManager_validFile_loadsDataWithoutWarning() throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        Storage storage = createStorage(dataFile);
        storage.saveAddressBook(getTypicalAddressBook());
        MainApp app = new MainApp();

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(getTypicalAddressBook(), model.getAddressBook());
        assertEquals(Optional.empty(), app.getStartupWarning());
    }

    @Test
    public void initModelManager_damagedFile_startsEmptyWithWarningAndKeepsFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        byte[] damagedContent = "{ \"persons\": [ not valid json".getBytes();
        Files.write(dataFile, damagedContent);
        Storage storage = createStorage(dataFile);
        MainApp app = new MainApp();

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(Optional.of(MainApp.MESSAGE_DATA_LOAD_FAILURE), app.getStartupWarning());
        assertEquals("Unable to load saved Spotter data. The existing data file has not been modified.",
                MainApp.MESSAGE_DATA_LOAD_FAILURE);

        // A read-only command must not overwrite the damaged file
        new LogicManager(model, storage).execute(ListCommand.COMMAND_WORD);
        assertArrayEquals(damagedContent, Files.readAllBytes(dataFile));
    }

    @Test
    public void start_damagedFile_showsWarningInUi() throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        Files.writeString(dataFile, "{ \"persons\": [ not valid json");
        MainApp app = new MainApp();
        app.initModelManager(createStorage(dataFile), new UserPrefs());
        UiStub uiStub = new UiStub();
        app.ui = uiStub;

        app.start(null);

        assertEquals(1, uiStub.startCount);
        assertEquals(List.of(MainApp.MESSAGE_DATA_LOAD_FAILURE), uiStub.messages);
    }

    @Test
    public void start_missingFile_showsNoMessage() {
        MainApp app = new MainApp();
        app.initModelManager(createStorage(temporaryFolder.resolve("spotter.json")), new UserPrefs());
        UiStub uiStub = new UiStub();
        app.ui = uiStub;

        app.start(null);

        assertEquals(1, uiStub.startCount);
        assertEquals(List.of(), uiStub.messages);
    }

    @Test
    public void stop_damagedFile_savesPreferencesButNotDataFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        byte[] damagedContent = "{ \"persons\": [ not valid json".getBytes();
        Files.write(dataFile, damagedContent);
        Storage storage = createStorage(dataFile);
        MainApp app = new MainApp();
        app.storage = storage;
        app.model = app.initModelManager(storage, new UserPrefs());

        app.stop();

        assertTrue(Files.exists(temporaryFolder.resolve("preferences.json")));
        assertArrayEquals(damagedContent, Files.readAllBytes(dataFile));
    }

    /**
     * A {@code Ui} stub that records calls instead of showing a window.
     */
    private static class UiStub implements Ui {
        private int startCount = 0;
        private final List<String> messages = new ArrayList<>();

        @Override
        public void start(Stage primaryStage) {
            startCount++;
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }
}
