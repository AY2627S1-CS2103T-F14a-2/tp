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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises sessions and measurements together across commands, copying, storage and failed saves.
 */
public class SessionMeasurementIntegrationTest {
    @TempDir
    public Path folder;

    @Test
    public void execute_sessionMeasurementReplacementsAndEditWithoutEmail_preservesBothAfterRestart() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Sarah Lim").withoutEmail().build());
        JsonAddressBookStorage json = new JsonAddressBookStorage(folder.resolve("clients.json"));
        LogicManager logic = createLogic(model, json);
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String today = LocalDate.now().toString();

        logic.execute("session n/Sarah Lim d/" + tomorrow + " t/1700");
        logic.execute("measure 1 d/" + today + " t/weight v/72.4");
        Person booked = new PersonBuilder().withName("Sarah Lim").withoutEmail().withNextSession(tomorrow + " 17:00")
                .withMeasurements(weight(today, "72.4")).build();
        assertEquals(booked, model.getFilteredPersonList().getFirst());
        assertEquals(booked, json.readAddressBook().orElseThrow().getPersonList().getFirst());

        logic.execute("session 1 d/" + tomorrow + " t/0900");
        Person rescheduled = new PersonBuilder(booked).withNextSession(tomorrow + " 09:00").build();
        assertEquals(rescheduled, model.getFilteredPersonList().getFirst());
        logic.execute("measure 1 d/" + today + " t/weight v/71.8");
        Person corrected = new PersonBuilder(rescheduled).withMeasurements(weight(today, "71.8")).build();
        assertEquals(corrected, model.getFilteredPersonList().getFirst());

        logic.execute("edit 1 p/99999999");
        Person edited = new PersonBuilder(corrected).withPhone("99999999").build();
        ModelManager restarted = new ModelManager(json.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(edited), restarted.getFilteredPersonList());
        assertEquals(List.of(edited), model.getFilteredPersonList());
    }

    @Test
    public void execute_measurementThenBooking_usesSortedDisplayedIndexAndPreservesOtherClient() throws Exception {
        ModelManager model = new ModelManager();
        Person sarah = new PersonBuilder().withName("Sarah Lim").build();
        Person alex = new PersonBuilder().withName("Alex Tan").build();
        model.addPerson(sarah);
        model.addPerson(alex);
        JsonAddressBookStorage json = new JsonAddressBookStorage(folder.resolve("sorted-clients.json"));
        LogicManager logic = createLogic(model, json);
        String today = LocalDate.now().toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();

        // Sarah is second on screen, even though she is first in the storage list.
        logic.execute("measure 2 d/" + today + " t/weight v/72.4");
        logic.execute("session n/Sarah Lim d/" + tomorrow + " t/1700");
        Person booked = new PersonBuilder(sarah).withMeasurements(weight(today, "72.4"))
                .withNextSession(tomorrow + " 17:00").build();
        assertEquals(List.of(booked, alex), model.getFilteredPersonList());

        // The booking moves Sarah to the first screen position; measurement must follow that index.
        logic.execute("measure 1 d/" + today + " t/weight v/71.8");
        Person corrected = new PersonBuilder(booked).withMeasurements(weight(today, "71.8")).build();
        assertEquals(List.of(corrected, alex), model.getFilteredPersonList());
        assertEquals(List.of(corrected, alex), json.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_failedSessionOrMeasurementSave_restoresBothFieldsFilterAndFile() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String today = LocalDate.now().toString();
        Person sarah = new PersonBuilder().withName("Sarah Lim").withNextSession(tomorrow + " 17:00")
                .withMeasurements(weight(today, "72.4")).build();
        ModelManager model = new ModelManager();
        model.addPerson(sarah);
        model.addPerson(new PersonBuilder().withName("Alex Tan").build());
        model.updateFilteredPersonList(person -> person.isSamePerson(sarah));
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

        for (String command : List.of("session 1 d/" + tomorrow + " t/0900",
                "measure 1 d/" + today + " t/weight v/71.8")) {
            CommandException failure = assertThrows(CommandException.class, () -> logic.execute(command));
            assertEquals(LogicManager.MESSAGE_SAVE_FAILURE, failure.getMessage());
            assertEquals(before, model.getAddressBook());
            assertEquals(List.of(sarah), model.getFilteredPersonList());
            assertEquals(saved, Files.readString(file));
        }
    }

    @Test
    public void load_filesWithOnlyOneFeature_preservesExistingDataAndDefaultsMissingField() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        Person plain = new PersonBuilder().build();
        Person measured = new PersonBuilder(plain).withMeasurements(weight(LocalDate.now().toString(), "72.4"))
                .build();
        Person booked = new PersonBuilder(plain).withNextSession(tomorrow + " 17:00").build();
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode sessionOnly = (ObjectNode) mapper.readTree(JsonUtil.toJsonString(new JsonAdaptedPerson(booked)));
        sessionOnly.remove("measurements");
        assertEquals(booked, JsonUtil.fromJsonString(sessionOnly.toString(), JsonAdaptedPerson.class).toModelType());
        ObjectNode measurementOnly = (ObjectNode) mapper.readTree(
                JsonUtil.toJsonString(new JsonAdaptedPerson(measured)));
        measurementOnly.remove("nextSession");
        assertEquals(measured,
                JsonUtil.fromJsonString(measurementOnly.toString(), JsonAdaptedPerson.class).toModelType());
        ObjectNode legacy = (ObjectNode) mapper.readTree(JsonUtil.toJsonString(new JsonAdaptedPerson(plain)));
        legacy.remove(List.of("nextSession", "measurements"));
        assertEquals(plain, JsonUtil.fromJsonString(legacy.toString(), JsonAdaptedPerson.class).toModelType());
    }

    /** Creates real JSON-backed command handling so the test includes parsing and persistence. */
    private LogicManager createLogic(ModelManager model, JsonAddressBookStorage json) {
        return new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
    }

    /** Supplies the independent expected measurement for a command's result. */
    private static Measurement weight(String date, String value) {
        return new Measurement(LocalDate.parse(date), MeasurementType.WEIGHT, new BigDecimal(value));
    }
}
