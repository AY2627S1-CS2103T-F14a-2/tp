package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.charset.StandardCharsets;
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

    // Valid JSON whose values break model constraints must be treated like a damaged file.
    // Each file also holds a valid client, so a partial load would be detected.

    @Test
    public void initModelManager_invalidPhone_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(
                "{ \"name\": \"Bob Tan\", \"phone\": \"12\" }");
    }

    @Test
    public void initModelManager_invalidEmail_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(
                "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", \"email\": \"bob-at-example\" }");
    }

    @Test
    public void initModelManager_invalidMeasurementValue_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(
                "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", \"measurements\": ["
                + "{ \"date\": \"2026-10-01\", \"type\": \"weight\", \"value\": \"999\" } ] }");
    }

    @Test
    public void initModelManager_invalidMeasurementType_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(
                "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", \"measurements\": ["
                + "{ \"date\": \"2026-10-01\", \"type\": \"height\", \"value\": \"170\" } ] }");
    }

    @Test
    public void initModelManager_duplicateMeasurement_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(
                "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", \"measurements\": ["
                + "{ \"date\": \"2026-10-01\", \"type\": \"weight\", \"value\": \"70\" }, "
                + "{ \"date\": \"2026-10-01\", \"type\": \"weight\", \"value\": \"71\" } ] }");
    }

    @Test
    public void initModelManager_invalidPerformanceSets_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(createClientWithPerformance("0", "5", "60"));
    }

    @Test
    public void initModelManager_invalidPerformanceReps_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(createClientWithPerformance("3", "1001", "60"));
    }

    @Test
    public void initModelManager_invalidPerformanceLoad_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidValuesStartEmptyAndKeepFile(createClientWithPerformance("3", "5", "-1"));
    }

    @Test
    public void initModelManager_validClientsInSameFormat_loadsAllClients() throws Exception {
        // Control for the cases above: the same file layout with valid values loads normally
        Path dataFile = temporaryFolder.resolve("spotter.json");
        Files.writeString(dataFile, createDataFile(createClientWithPerformance("3", "5", "60")));
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertEquals(Optional.empty(), app.getStartupWarning());
    }

    private static String createDataFile(String secondClientJson) {
        return "{ \"persons\": [ "
                + "{ \"name\": \"Alice Lim\", \"phone\": \"98765432\", \"tags\": [ \"beginner\" ] }, "
                + secondClientJson + " ] }";
    }

    private static String createClientWithPerformance(String sets, String reps, String load) {
        return "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", \"performances\": [ "
                + "{ \"date\": \"2026-10-01\", \"exercise\": \"Squat\", \"sets\": \"" + sets
                + "\", \"reps\": \"" + reps + "\", \"load\": \"" + load + "\" } ] }";
    }

    /**
     * Writes a data file containing one valid client followed by {@code invalidClientJson}, then checks that
     * Spotter starts with no clients, records the load warning, and leaves the file unchanged after a
     * read-only command.
     */
    private void assertInvalidValuesStartEmptyAndKeepFile(String invalidClientJson) throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        byte[] content = createDataFile(invalidClientJson).getBytes(StandardCharsets.UTF_8);
        Files.write(dataFile, content);
        Storage storage = createStorage(dataFile);
        MainApp app = new MainApp();

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(Optional.of(MainApp.MESSAGE_DATA_LOAD_FAILURE), app.getStartupWarning());

        new LogicManager(model, storage).execute(ListCommand.COMMAND_WORD);
        assertArrayEquals(content, Files.readAllBytes(dataFile));
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
