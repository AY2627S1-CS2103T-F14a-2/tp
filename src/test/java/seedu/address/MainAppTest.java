package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;

public class MainAppTest {

    // Every field and record type that the invalid-value tests change. The control test checks that all of
    // them load, so a misspelt key, which Jackson ignores, cannot make an invalid-value test pass.
    private static final String VALID_CLIENT = "{ \"name\": \"Bob Tan\", \"phone\": \"91234567\", "
            + "\"email\": \"bob@example.com\", "
            + "\"measurements\": [ { \"date\": \"2026-10-01\", \"type\": \"weight\", \"value\": \"70\" } ], "
            + "\"performances\": [ { \"date\": \"2026-10-01\", \"exercise\": \"Squat\", "
            + "\"sets\": \"3\", \"reps\": \"5\", \"load\": \"60\" } ] }";

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
        assertEquals("Unable to load saved Spotter data. The existing data file has not been modified.",
                MainApp.MESSAGE_DATA_LOAD_FAILURE);
        assertStartsEmptyWithWarningAndKeepsFile("{ \"persons\": [ not valid json");
    }

    // Valid JSON whose values break model constraints must be treated like a damaged file.
    // Each file also holds a valid client, so a partial load would be detected.
    // AI assistance: Claude Code helped write the tests for data files with invalid values (#79).

    @Test
    public void initModelManager_invalidPhone_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"phone\": \"91234567\"", "\"phone\": \"12\""));
    }

    @Test
    public void initModelManager_invalidEmail_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("bob@example.com", "bob-at-example"));
    }

    @Test
    public void initModelManager_invalidMeasurementValue_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"value\": \"70\"", "\"value\": \"999\""));
    }

    @Test
    public void initModelManager_invalidMeasurementType_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"weight\"", "\"height\""));
    }

    @Test
    public void initModelManager_duplicateMeasurement_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"value\": \"70\" }",
                "\"value\": \"70\" }, { \"date\": \"2026-10-01\", \"type\": \"weight\", \"value\": \"71\" }"));
    }

    @Test
    public void initModelManager_invalidPerformanceSets_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"sets\": \"3\"", "\"sets\": \"0\""));
    }

    @Test
    public void initModelManager_invalidPerformanceReps_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"reps\": \"5\"", "\"reps\": \"1001\""));
    }

    @Test
    public void initModelManager_invalidPerformanceLoad_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"load\": \"60\"", "\"load\": \"-1\""));
    }

    @Test
    public void initModelManager_validClient_loadsEveryFieldAndRecord() throws Exception {
        // Control for the cases above
        assertLoadsBothClients(VALID_CLIENT);
    }

    @Test
    public void initModelManager_futureDatedRecords_loadsWithoutWarning() throws Exception {
        // Only commands reject future dates, so that saved records stay loadable however the clock is set
        assertLoadsBothClients(validClientWith("2026-10-01", LocalDate.now().plusDays(1).toString()));
    }

    // A hand-edited null must be reported as damaged data, not as a NullPointerException that stops startup (#193).

    @Test
    public void initModelManager_nullClientEntry_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty("null");
    }

    @Test
    public void initModelManager_nullTagEntry_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertInvalidClientStartsEmpty(validClientWith("\"email\": \"bob@example.com\", ",
                "\"email\": \"bob@example.com\", \"tags\": [ null ], "));
    }

    @Test
    public void initModelManager_nullFile_startsEmptyWithWarningAndKeepsFile() throws Exception {
        assertStartsEmptyWithWarningAndKeepsFile("null");
    }

    @Test
    public void initPrefs_validFile_returnsSavedPrefs() throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        UserPrefs savedPrefs = new UserPrefs();
        savedPrefs.setGuiSettings(new GuiSettings(800, 650, 10, 20));
        new JsonUserPrefsStorage(prefsFile).saveUserPrefs(savedPrefs);

        UserPrefs prefs = new MainApp().initPrefs(new JsonUserPrefsStorage(prefsFile));

        assertEquals(savedPrefs, prefs);
    }

    @Test
    public void initPrefs_nullFile_returnsDefaultPrefs() throws Exception {
        assertInitPrefsReturnsDefaultPrefs("null");
    }

    @Test
    public void initPrefs_nullGuiSettings_returnsDefaultPrefs() throws Exception {
        assertInitPrefsReturnsDefaultPrefs("{ \"guiSettings\": null }");
    }

    /**
     * Writes {@code content} as the preferences file, then checks that Spotter falls back to the default preferences.
     */
    private void assertInitPrefsReturnsDefaultPrefs(String content) throws Exception {
        Path prefsFile = temporaryFolder.resolve("preferences.json");
        Files.writeString(prefsFile, content);

        UserPrefs prefs = new MainApp().initPrefs(new JsonUserPrefsStorage(prefsFile));

        assertEquals(new UserPrefs(), prefs);
    }

    private static String createDataFile(String secondClientJson) {
        return "{ \"persons\": [ "
                + "{ \"name\": \"Alice Lim\", \"phone\": \"98765432\", \"tags\": [ \"beginner\" ] }, "
                + secondClientJson + " ] }";
    }

    /**
     * Returns {@link #VALID_CLIENT} with every occurrence of {@code validPart} replaced by {@code newPart}.
     */
    private static String validClientWith(String validPart, String newPart) {
        assertTrue(VALID_CLIENT.contains(validPart), "Not found in VALID_CLIENT: " + validPart);
        return VALID_CLIENT.replace(validPart, newPart);
    }

    /**
     * Checks that a data file holding a valid client and {@code clientJson} loads both clients, with
     * Bob's email, measurement and performance, and no warning.
     */
    private void assertLoadsBothClients(String clientJson) throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        Files.writeString(dataFile, createDataFile(clientJson));
        MainApp app = new MainApp();

        Model model = app.initModelManager(createStorage(dataFile), new UserPrefs());

        List<Person> clients = model.getAddressBook().getPersonList();
        assertEquals(2, clients.size());
        assertTrue(clients.get(1).getEmail().isPresent());
        assertEquals(1, clients.get(1).getMeasurements().size());
        assertEquals(1, clients.get(1).getGymPerformances().size());
        assertEquals(Optional.empty(), app.getStartupWarning());
    }

    private void assertInvalidClientStartsEmpty(String invalidClientJson) throws Exception {
        assertStartsEmptyWithWarningAndKeepsFile(createDataFile(invalidClientJson));
    }

    /**
     * Writes {@code content} as the data file, then checks that Spotter starts with no clients, records the
     * load warning, and leaves the file unchanged after a read-only command.
     */
    private void assertStartsEmptyWithWarningAndKeepsFile(String content) throws Exception {
        Path dataFile = temporaryFolder.resolve("spotter.json");
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        Files.write(dataFile, bytes);
        Storage storage = createStorage(dataFile);
        MainApp app = new MainApp();

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(Optional.of(MainApp.MESSAGE_DATA_LOAD_FAILURE), app.getStartupWarning());

        new LogicManager(model, storage).execute(ListCommand.COMMAND_WORD);
        assertArrayEquals(bytes, Files.readAllBytes(dataFile));
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
