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
        assertEquals("Recorded measurement for Amy Bee: weight = 72.4 kg on 2026-09-12.",
                logic.execute("measure 1 d/2026-09-12 t/weight v/72.4").getFeedbackToUser());
        assertEquals("Updated measurement for Amy Bee: weight = 71.8 kg on 2026-09-12.",
                logic.execute("measure 1 d/2026-09-12 t/weight v/71.8").getFeedbackToUser());
        logic.execute("measure 1 d/2026-09-12 t/waist v/81");
        assertEquals("Recorded measurement for Amy Bee: bodyfat = 18.4 % on 2026-09-12.",
                logic.execute("measure 1 d/2026-09-12 t/bodyfat v/18.4").getFeedbackToUser());
        List<Person> beforeInvalidCommand = List.copyOf(model.getAddressBook().getPersonList());
        assertThrows(ParseException.class, () -> logic.execute("measure 1 d/2026-09-12 t/weight v/999"));
        assertEquals(beforeInvalidCommand, model.getAddressBook().getPersonList());
        Model restarted = new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restarted.getAddressBook());
        assertEquals(3, restarted.getFilteredPersonList().get(0).getMeasurements().size());
        logic.execute("edit 1 p/91234567");
        assertEquals(beforeInvalidCommand.get(0).getMeasurements(),
                data.readAddressBook().orElseThrow().getPersonList().get(0).getMeasurements());
    }
}
