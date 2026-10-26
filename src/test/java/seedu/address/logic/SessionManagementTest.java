package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises real command parsing, filtering, cancellation and JSON persistence together, including failed saves.
 */
public class SessionManagementTest {
    @TempDir
    public Path temporaryFolder;

    private final LocalDate date = LocalDate.now().plusDays(2);
    private final Person booked = new PersonBuilder().withName("Zoe Review").withoutEmail().withTags("strength")
            .withNextSession(date + " 09:00")
            .withMeasurements(new Measurement(LocalDate.now().minusDays(1), MeasurementType.WEIGHT,
                    new BigDecimal("72.4")))
            .withGymPerformances(BENCH_PRESS).build();
    private final Person other = new PersonBuilder().withName("Ada Review").withNextSession(date + " 17:00").build();
    private Model model;
    private LogicManager logic;
    private JsonAddressBookStorage json;
    private JsonUserPrefsStorage prefs;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        model.addPerson(other);
        model.addPerson(booked);
        json = new JsonAddressBookStorage(temporaryFolder.resolve("spotter.json"));
        prefs = new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"));
        json.saveAddressBook(model.getAddressBook());
        logic = new LogicManager(model, new StorageManager(json, prefs));
    }

    @Test
    public void execute_cancelSavesAndReloadsWithoutLosingProgress() throws Exception {
        logic.execute("sessions d/" + date);
        logic.execute("cancelsession 1");
        assertEquals(List.of(other), model.getFilteredPersonList());
        assertEquals(List.of(other, booked.withNextSession(null)), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), json.readAddressBook().orElseThrow());
        logic.execute("list");
        logic.execute("session n/Zoe Review d/" + date.plusDays(1) + " t/1900");
        Person rebooked = model.getAddressBook().getPersonList().get(1);
        assertEquals(booked.getMeasurements(), rebooked.getMeasurements());
        assertEquals(booked.getPerformanceHistory(), rebooked.getPerformanceHistory());
        assertEquals(date.plusDays(1), rebooked.getNextSession().orElseThrow().value.toLocalDate());
        assertEquals(model.getAddressBook(), json.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedCancellationSave_restoresDataAndDateFilter() throws Exception {
        logic.execute("sessions d/" + date);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayedBefore = List.copyOf(model.getFilteredPersonList());
        byte[] savedBefore = Files.readAllBytes(json.getAddressBookFilePath());
        LogicManager failingLogic = new LogicManager(model, new StorageManager(failingStorage(), prefs));
        assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () ->
                failingLogic.execute("cancelsession 1"));
        assertEquals(before, model.getAddressBook());
        assertEquals(displayedBefore, model.getFilteredPersonList());
        assertArrayEquals(savedBefore, Files.readAllBytes(json.getAddressBookFilePath()));
    }

    @Test
    public void execute_failedCancellationByNameSave_restoresDataAndDateFilter() throws Exception {
        logic.execute("sessions d/" + date);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayedBefore = List.copyOf(model.getFilteredPersonList());
        byte[] savedBefore = Files.readAllBytes(json.getAddressBookFilePath());
        LogicManager failingLogic = new LogicManager(model, new StorageManager(failingStorage(), prefs));
        assertThrows(CommandException.class, LogicManager.MESSAGE_SAVE_FAILURE, () ->
                failingLogic.execute("cancelsession n/Zoe Review"));
        assertEquals(before, model.getAddressBook());
        assertEquals(displayedBefore, model.getFilteredPersonList());
        assertArrayEquals(savedBefore, Files.readAllBytes(json.getAddressBookFilePath()));
    }

    @Test
    public void execute_dateListingNeverSaves_evenWhenWritesFail() throws Exception {
        byte[] savedBefore = Files.readAllBytes(json.getAddressBookFilePath());
        LogicManager readOnlyLogic = new LogicManager(model, new StorageManager(failingStorage(), prefs));
        readOnlyLogic.execute("sessions d/" + date);
        assertEquals(List.of(booked, other), model.getFilteredPersonList());
        assertArrayEquals(savedBefore, Files.readAllBytes(json.getAddressBookFilePath()));
    }

    @Test
    public void execute_invalidCommands_leaveDataFileAndViewUnchanged() throws Exception {
        logic.execute("sessions d/" + date);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayedBefore = List.copyOf(model.getFilteredPersonList());
        byte[] savedBefore = Files.readAllBytes(json.getAddressBookFilePath());
        for (String command : List.of("cancelsession 0", "cancelsession 1 2", "cancelsession 999",
                "sessions", "sessions d/2026-02-30", "sessions d/2026-10-22 x/foo",
                "sessions d/2026-10-22 d/2026-10-23")) {
            boolean rejected = false;
            try {
                logic.execute(command);
            } catch (ParseException | CommandException exception) {
                rejected = true;
            }
            assertTrue(rejected, command);
            assertEquals(before, model.getAddressBook(), command);
            assertEquals(displayedBefore, model.getFilteredPersonList(), command);
            assertArrayEquals(savedBefore, Files.readAllBytes(json.getAddressBookFilePath()), command);
        }
    }

    /**
     * Simulates a write failure without changing the existing JSON file.
     */
    private JsonAddressBookStorage failingStorage() {
        return new JsonAddressBookStorage(json.getAddressBookFilePath()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated session save failure");
            }
        };
    }
}
