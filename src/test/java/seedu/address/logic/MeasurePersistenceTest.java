package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the complete typed-command to persisted-measurement workflow.
 */
public class MeasurePersistenceTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_recordUpdateAndReload_preservesFinalState() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("clients.json"));
        Logic logic = new LogicManager(model,
                new StorageManager(data, new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute("measure 1 d/2026-09-12 t/weight v/72.4");
        logic.execute("measure 1 d/2026-09-12 t/weight v/71.8");
        logic.execute("measure 1 d/2026-09-12 t/waist v/81");
        List<Person> beforeInvalidCommand = List.copyOf(model.getAddressBook().getPersonList());
        assertThrows(ParseException.class, () -> logic.execute("measure 1 d/2026-09-12 t/weight v/999"));
        assertEquals(beforeInvalidCommand, model.getAddressBook().getPersonList());
        Model restarted = new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restarted.getAddressBook());
        assertEquals(2, restarted.getFilteredPersonList().get(0).getMeasurements().size());
    }
}
