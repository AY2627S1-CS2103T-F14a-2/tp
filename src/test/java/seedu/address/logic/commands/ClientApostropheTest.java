package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.testutil.PersonBuilder;

/** Regression checks for typographic apostrophes and compatibility with existing client records. */
public class ClientApostropheTest {
    private final ModelManager model = new ModelManager();
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void commands_equivalentApostrophes_findBookCancelAndRejectDuplicate() throws Exception {
        execute("add n/Mary O'Brien p/91234567");
        execute("find O’Brien");
        assertEquals(1, model.getFilteredPersonList().size());
        execute("session n/Mary O’Brien d/2026-12-15 t/0900");
        assertTrue(model.getAddressBook().getPersonList().getFirst().getNextSession().isPresent());
        execute("cancelsession n/Mary O’Brien");
        assertTrue(model.getAddressBook().getPersonList().getFirst().getNextSession().isEmpty());
        assertThrows(CommandException.class, () -> execute("add n/Mary O’Brien p/92345678"));
        execute("add n/Other Client p/93456789");
        execute("find Other");
        assertThrows(CommandException.class, () -> execute("edit 1 n/Mary O’Brien"));
        assertEquals("Other Client", model.getFilteredPersonList().getFirst().getName().fullName);
        execute("find Mary");
        execute("edit 1 n/Mary O’Brien");
        assertEquals("Mary O’Brien", model.getAddressBook().getPersonList().getFirst().getName().fullName);
    }

    @Test
    public void legacyVariants_loadWithoutLossAndUseExactNames(@TempDir Path directory) throws Exception {
        model.addPerson(new PersonBuilder().withName("Mary O'Brien").build());
        model.addPerson(new PersonBuilder().withName("Mary O’Brien").build());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("clients.json"));
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        execute("find Mary");
        execute("edit 1 p/98765432");
        execute("session n/Mary O’Brien d/2026-12-15 t/0900");
        assertTrue(model.getAddressBook().getPersonList().getFirst().getNextSession().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().get(1).getNextSession().isPresent());
    }

    @Test
    public void ambiguousLegacyVariants_requireIndexWithoutChangingData() throws Exception {
        model.addPerson(new PersonBuilder().withName("A'B'C").build());
        model.addPerson(new PersonBuilder().withName("A’B’C").build());
        CommandException exception = assertThrows(CommandException.class, () ->
                execute("session n/A'B’C d/2026-12-15 t/0900"));
        assertEquals(SessionCommand.MESSAGE_AMBIGUOUS_NAME, exception.getMessage());
        assertTrue(model.getAddressBook().getPersonList().stream().allMatch(p -> p.getNextSession().isEmpty()));
    }

    private void execute(String command) throws Exception {
        parser.parseCommand(command).execute(model);
    }
}
