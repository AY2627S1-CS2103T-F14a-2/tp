package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises next sessions, body measurements and gym performances together across commands, edits,
 * storage and failed saves. Each command that copies a client must keep the other two kinds of data.
 *
 * <p>AI assistance: Claude (Anthropic) helped implement these regression tests.
 */
public class TrainingRecordsIntegrationTest {

    private static final String TODAY = LocalDate.now().toString();
    private static final String TOMORROW = LocalDate.now().plusDays(1).toString();

    @TempDir
    public Path folder;

    @Test
    public void execute_sessionMeasurementPerformanceAndEdit_preservesAllThreeAfterRestart() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Sarah Lim").build());
        JsonAddressBookStorage json = new JsonAddressBookStorage(folder.resolve("clients.json"));
        LogicManager logic = createLogic(model, json);

        logic.execute("session 1 d/" + TOMORROW + " t/1700");
        logic.execute("measure 1 d/" + TODAY + " t/weight v/72.4");
        logic.execute("performance 1 d/" + TODAY + " e/Bench Press s/3 r/8 w/60");
        // the same exercise on the same date replaces the record
        logic.execute("performance 1 d/" + TODAY + " e/bench press s/1 r/1 w/62.5");
        logic.execute("edit 1 p/99999999");

        Person expected = new PersonBuilder().withName("Sarah Lim").withPhone("99999999")
                .withNextSession(TOMORROW + " 17:00")
                .withMeasurements(weight("72.4"))
                .withGymPerformances(performance("bench press", 1, 1, "62.5"))
                .build();
        assertEquals(List.of(expected), model.getFilteredPersonList());

        ModelManager restarted = new ModelManager(json.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(expected), restarted.getFilteredPersonList());
    }

    @Test
    public void execute_performanceBeforeSessionAndMeasurement_keepsPerformance() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Sarah Lim").build());
        JsonAddressBookStorage json = new JsonAddressBookStorage(folder.resolve("reversed.json"));
        LogicManager logic = createLogic(model, json);

        logic.execute("performance 1 d/" + TODAY + " e/Squat s/3 r/5 w/100");
        logic.execute("session 1 d/" + TOMORROW + " t/0900");
        logic.execute("measure 1 d/" + TODAY + " t/weight v/71.8");

        Person expected = new PersonBuilder().withName("Sarah Lim")
                .withNextSession(TOMORROW + " 09:00")
                .withMeasurements(weight("71.8"))
                .withGymPerformances(performance("Squat", 3, 5, "100"))
                .build();
        assertEquals(List.of(expected), model.getFilteredPersonList());
        assertEquals(List.of(expected), json.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_failedPerformanceSave_restoresAllThreeAndFile() throws Exception {
        Person sarah = new PersonBuilder().withName("Sarah Lim").withNextSession(TOMORROW + " 17:00")
                .withMeasurements(weight("72.4"))
                .withGymPerformances(performance("Bench Press", 3, 8, "60"))
                .build();
        ModelManager model = new ModelManager();
        model.addPerson(sarah);
        AddressBook before = new AddressBook(model.getAddressBook());
        Path file = folder.resolve("failed-save.json");
        new JsonAddressBookStorage(file).saveAddressBook(before);
        String saved = Files.readString(file);
        JsonAddressBookStorage failing = new JsonAddressBookStorage(file) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated write failure");
            }
        };
        LogicManager logic = createLogic(model, failing);

        CommandException failure = assertThrows(CommandException.class, ()
            -> logic.execute("performance 1 d/" + TODAY + " e/Bench Press s/5 r/5 w/80"));
        assertEquals(LogicManager.MESSAGE_SAVE_FAILURE, failure.getMessage());
        assertEquals(before, model.getAddressBook());
        assertEquals(saved, Files.readString(file));
    }

    private LogicManager createLogic(ModelManager model, JsonAddressBookStorage json) {
        return new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
    }

    private static Measurement weight(String value) {
        return new Measurement(LocalDate.parse(TODAY), MeasurementType.WEIGHT, new BigDecimal(value));
    }

    private static GymPerformance performance(String exercise, int sets, int reps, String load) {
        return new GymPerformance(LocalDate.parse(TODAY), new ExerciseName(exercise), sets, reps, new Load(load));
    }
}
