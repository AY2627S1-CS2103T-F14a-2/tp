package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.SQUAT;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
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
 * Checks measurement deletion through parsing, model updates, storage and restart.
 */
public class DeleteMeasurePersistenceTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_replaceDeleteAndRecordAgain_persistsAndKeepsOtherClientData() throws Exception {
        Person original = new PersonBuilder().withNextSession("2026-10-23 17:00")
                .withGymPerformances(SQUAT).build();
        Model model = new ModelManager();
        model.addPerson(original);
        JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("clients.json"));
        Logic logic = createLogic(model, data);
        logic.execute("measure 1 d/2026-09-12 t/weight v/72.4");
        logic.execute("measure 1 d/2026-09-12 t/weight v/71.8");
        logic.execute("measure 1 d/2026-09-12 t/waist v/81");
        assertEquals(2, model.getFilteredPersonList().get(0).getMeasurements().size());
        assertEquals("Deleted measurement for Amy Bee: weight = 71.8 kg on 2026-09-12.",
                logic.execute("deletemeasure 1 d/2026-09-12 t/weight").getFeedbackToUser());

        Measurement waist = new Measurement(LocalDate.of(2026, 9, 12), MeasurementType.WAIST, new BigDecimal("81"));
        Person expected = original.withMeasurement(waist);
        Model restarted = new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(expected), restarted.getFilteredPersonList());
        Logic restartedLogic = createLogic(restarted, data);
        assertEquals("Recorded measurement for Amy Bee: weight = 70 kg on 2026-09-12.",
                restartedLogic.execute("measure 1 d/2026-09-12 t/weight v/70").getFeedbackToUser());
        assertEquals(2, data.readAddressBook().orElseThrow().getPersonList().get(0).getMeasurements().size());
    }

    @Test
    public void execute_futureDatedRecordFromFile_canBeDeletedAndPersisted() throws Exception {
        LocalDate future = LocalDate.now().plusYears(1);
        Model model = new ModelManager();
        Person original = new PersonBuilder().build();
        model.addPerson(original.withMeasurement(new Measurement(future, MeasurementType.WEIGHT,
                new BigDecimal("72"))));
        JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("future.json"));
        data.saveAddressBook(model.getAddressBook());
        Model restarted = new ModelManager(data.readAddressBook().orElseThrow(), new UserPrefs());
        createLogic(restarted, data).execute("deletemeasure 1 d/" + future + " t/weight");
        assertEquals(List.of(original), data.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_invalidOrMissingRecord_doesNotWriteDataFile() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("unchanged.json"));
        data.saveAddressBook(model.getAddressBook());
        String before = Files.readString(data.getAddressBookFilePath());
        Logic logic = createLogic(model, data);
        assertThrows(CommandException.class, () -> logic.execute("deletemeasure 1 d/2026-09-12 t/weight"));
        assertThrows(ParseException.class, () -> logic.execute("deletemeasure 1 d/2026-02-30 t/weight"));
        assertEquals(before, Files.readString(data.getAddressBookFilePath()));
    }

    @Test
    public void execute_failedSave_restoresMeasurementsFilterAndFile() throws Exception {
        for (IOException failure : List.of(new IOException("disk failure"), new AccessDeniedException("denied"))) {
            Model model = new ModelManager();
            model.addPerson(new PersonBuilder().withName("Other Client").build());
            Person selected = new PersonBuilder().withName("Selected Client").withGymPerformances(SQUAT)
                    .withNextSession("2026-10-23 17:00").withMeasurements(new Measurement(
                            LocalDate.of(2026, 9, 12), MeasurementType.WEIGHT, new BigDecimal("72.4"))).build();
            model.addPerson(selected);
            model.updateFilteredPersonList(person -> person.isSamePerson(selected));
            AddressBook before = new AddressBook(model.getAddressBook());
            Path file = temporaryFolder.resolve("failed.json");
            new JsonAddressBookStorage(file).saveAddressBook(before);
            String saved = Files.readString(file);
            JsonAddressBookStorage failing = new JsonAddressBookStorage(file) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                    throw failure;
                }
            };
            assertEquals(LogicManager.MESSAGE_SAVE_FAILURE, assertThrows(CommandException.class, () ->
                    createLogic(model, failing).execute("deletemeasure 1 d/2026-09-12 t/weight")).getMessage());
            assertEquals(before, model.getAddressBook());
            assertEquals(List.of(selected), model.getFilteredPersonList());
            assertEquals(saved, Files.readString(file));
        }
    }

    private Logic createLogic(Model model, JsonAddressBookStorage data) {
        return new LogicManager(model, new StorageManager(data,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
