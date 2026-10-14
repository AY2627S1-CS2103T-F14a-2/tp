package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
}
