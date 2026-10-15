package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.session.NextSession;
import seedu.address.testutil.PersonBuilder;

/**
 * Integrates command parsing, replacement, JSON save/reload, and backward-compatible session storage.
 */
public class SessionStorageTest {
    @TempDir
    public Path folder;

    @Test
    public void execute_sessionThenOverwriteAndRestart_preservesOneBooking() throws Exception {
        Person client = new PersonBuilder().withName("Sarah Lim").build();
        ModelManager model = new ModelManager();
        model.addPerson(client);
        Path file = folder.resolve("clients.json");
        JsonAddressBookStorage json = new JsonAddressBookStorage(file);
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
        String date = LocalDate.now().plusDays(1).toString();
        logic.execute("session n/sarah   lim d/" + date + " t/0905");
        assertTrue(Files.readString(file).contains(date + " 09:05"));
        logic.execute("session 1 d/" + date + " t/17:00");
        ModelManager restarted = new ModelManager(json.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(client.withNextSession(new NextSession(date + " 17:00"))),
                restarted.getFilteredPersonList());
        assertFalse(Files.readString(file).contains(date + " 09:05"));
    }

    @Test
    public void execute_rejectedCommands_leaveModelOrderAndFileUnchanged() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Sarah Lim").build());
        Path file = folder.resolve("clients.json");
        JsonAddressBookStorage json = new JsonAddressBookStorage(file);
        json.saveAddressBook(model.getAddressBook());
        String before = Files.readString(file);
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
        String date = LocalDate.now().plusDays(1).toString();
        assertThrows(CommandException.class, SessionCommand.MESSAGE_NO_SUCH_NAME, () ->
                logic.execute("session n/Sarah d/" + date + " t/0905"));
        assertThrows(ParseException.class, () -> logic.execute("session 1 d/" + date + " t/2400"));
        assertEquals(original, model.getAddressBook());
        assertEquals(displayed, model.getFilteredPersonList());
        assertEquals(before, Files.readString(file));
    }

    @Test
    public void load_oldJsonAndExpiredBooking_success() throws Exception {
        Person client = new PersonBuilder().build();
        JsonAdaptedPerson old = new JsonAdaptedPerson(client.getName().fullName, client.getPhone().value,
                client.getEmail().map(email -> email.value).orElse(null), List.of());
        assertEquals(client, old.toModelType());
        Person expired = client.withNextSession(new NextSession("2000-01-01 00:00"));
        AddressBook book = new AddressBook();
        book.addPerson(expired);
        JsonAddressBookStorage json = new JsonAddressBookStorage(folder.resolve("expired.json"));
        json.saveAddressBook(book);
        assertEquals(book, new AddressBook(json.readAddressBook().orElseThrow()));
        assertEquals(expired, new JsonAdaptedPerson(expired).toModelType());
    }

    @Test
    public void load_invalidSession_rejectedWithoutChangingFile() throws Exception {
        Person client = new PersonBuilder().build();
        for (String invalid : List.of("", "2026-02-30 17:00", "2026-10-12 24:00", "0000-01-01 00:00")) {
            JsonAdaptedPerson adapted = new JsonAdaptedPerson(client.getName().fullName, client.getPhone().value,
                    client.getEmail().map(email -> email.value).orElse(null), List.of(), invalid, List.of());
            assertThrows(IllegalValueException.class, NextSession.MESSAGE_CONSTRAINTS, adapted::toModelType);
        }
        AddressBook book = new AddressBook();
        book.addPerson(client.withNextSession(new NextSession("2026-10-12 17:00")));
        Path file = folder.resolve("damaged-session.json");
        JsonAddressBookStorage json = new JsonAddressBookStorage(file);
        json.saveAddressBook(book);
        String damaged = Files.readString(file).replace("2026-10-12 17:00", "2026-02-30 17:00");
        Files.writeString(file, damaged);
        assertThrows(DataLoadingException.class, json::readAddressBook);
        assertEquals(damaged, Files.readString(file));
    }
}
