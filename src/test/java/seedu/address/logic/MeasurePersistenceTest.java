package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
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
    public void execute_failedMeasurementSave_restoresRecordsFilterAndSavedData() throws Exception {
        for (IOException failure : List.of(new IOException("disk failure"), new AccessDeniedException("denied"))) {
            assertMeasurementRollback(failure, false);
            assertMeasurementRollback(failure, true);
        }
    }

    @Test
    public void execute_failedEditSave_restoresPreviousFilter() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Fictional One").build());
        model.addPerson(new PersonBuilder().withName("Fictional Two").build());
        Person selected = model.getFilteredPersonList().get(1);
        model.updateFilteredPersonList(person -> person.isSamePerson(selected));
        AddressBook before = new AddressBook(model.getAddressBook());
        Logic logic = failingLogic(model, temporaryFolder.resolve("edit.json"), new IOException("disk failure"));
        assertThrows(CommandException.class, () -> logic.execute("edit 1 p/91234567"));
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(selected), model.getFilteredPersonList());
    }

    private void assertMeasurementRollback(IOException failure, boolean replacing) throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Fictional One").build());
        Person selected = new PersonBuilder().withName("Fictional Two").build();
        if (replacing) {
            selected = selected.withMeasurement(new Measurement(LocalDate.of(2026, 9, 12),
                    MeasurementType.WEIGHT, new BigDecimal("73")));
        }
        model.addPerson(selected);
        Person target = selected;
        model.updateFilteredPersonList(person -> person.isSamePerson(target));
        AddressBook before = new AddressBook(model.getAddressBook());
        Path file = temporaryFolder.resolve("failed.json");
        JsonAddressBookStorage data = new JsonAddressBookStorage(file);
        data.saveAddressBook(before);
        Logic logic = failingLogic(model, file, failure);
        assertEquals(LogicManager.MESSAGE_SAVE_FAILURE, assertThrows(CommandException.class, () ->
                logic.execute("measure 1 d/2026-09-12 t/weight v/72.4")).getMessage());
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(target), model.getFilteredPersonList());
        assertEquals(before, data.readAddressBook().orElseThrow());
    }

    private Logic failingLogic(Model model, Path file, IOException failure) {
        JsonAddressBookStorage data = new JsonAddressBookStorage(file) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw failure;
            }
        };
        return new LogicManager(model,
                new StorageManager(data, new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

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
