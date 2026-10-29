package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Regression coverage for actionable errors without changing accepted input or client data. */
public class ErrorRecoveryGuidanceTest {
    @TempDir
    public Path temporaryFolder;

    // AI assistance: Helen used OpenAI Codex for PR #203's locale-independent recovery regression.
    @ParameterizedTest
    @ValueSource(strings = {"deletemeasure %d d/2026-09-12 t/waist",
        "deleteperformance %d d/2026-09-12 e/Squat"})
    @ResourceLock(Resources.LOCALE)
    public void execute_missingRecordWithArabicLocale_suggestsExecutableIndex(String command) throws Exception {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("ar-EG"));
            Model model = new ModelManager();
            model.addPerson(new PersonBuilder().withName("Fictional Alpha").build());
            model.addPerson(new PersonBuilder().withName("Fictional Beta").build());
            AddressBook before = new AddressBook(model.getAddressBook());
            Logic logic = createLogic(model);

            for (int index : new int[] {2, 1}) {
                if (index == 1) {
                    logic.execute("find Beta");
                }
                String message = assertThrows(CommandException.class, () ->
                        logic.execute(String.format(Locale.ROOT, command, index))).getMessage();
                Matcher hint = Pattern.compile("Use \"([^\"]+)\"").matcher(message);
                assertTrue(hint.find(), message);
                String suggestedCommand = hint.group(1);
                assertEquals("view " + index, suggestedCommand);
                logic.execute(suggestedCommand);
                assertEquals("Fictional Beta", model.getViewedPerson().getValue().getName().fullName);
                assertEquals(before, model.getAddressBook());
            }
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void execute_missingRecords_suggestsActualDisplayedIndexWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Fictional Alpha").build());
        model.addPerson(new PersonBuilder().withName("Fictional Beta").build());
        AddressBook before = new AddressBook(model.getAddressBook());
        Logic logic = createLogic(model);

        for (String command : new String[] {"deletemeasure 2 d/2026-09-12 t/waist",
            "deleteperformance 2 d/2026-09-12 e/Squat"}) {
            String message = assertThrows(CommandException.class, () -> logic.execute(command)).getMessage();
            assertTrue(message.contains("Fictional Beta"), message);
            assertTrue(message.contains("2026-09-12"), message);
            assertTrue(message.contains("Use \"view 2\""), message);
            assertEquals(before, model.getAddressBook());
        }
        logic.execute("find Beta");
        String message = assertThrows(CommandException.class, () ->
                logic.execute("deletemeasure 1 d/2026-09-12 t/waist")).getMessage();
        assertTrue(message.contains("Use \"view 1\""), message);
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void parse_numericRepresentations_explainsAcceptedDecimalFormat() {
        AddressBookParser parser = new AddressBookParser();
        for (String value : new String[] {"1e2", "72kg", "72,4", "abc"}) {
            String message = assertThrows(ParseException.class, () ->
                    parser.parseCommand("measure 1 d/2026-09-12 t/weight v/" + value)).getMessage();
            assertTrue(message.contains("v/72.4"), message);
            assertTrue(message.contains("without units, commas or scientific notation"), message);
        }
    }

    @Test
    public void execute_invalidIndexes_explainsDisplayedListAcrossCommands() throws Exception {
        Model model = new ModelManager();
        AddressBookParser parser = new AddressBookParser();
        for (String command : new String[] {"delete 1", "edit 1 p/91234567", "view 1",
            "session 1 d/2026-12-15 t/1900", "cancelsession 1",
            "measure 1 d/2026-09-12 t/weight v/72", "deletemeasure 1 d/2026-09-12 t/weight",
            "performance 1 d/2026-09-12 e/Squat s/3 r/5 w/60", "deleteperformance 1 d/2026-09-12 e/Squat"}) {
            String message = assertThrows(CommandException.class, () ->
                    parser.parseCommand(command).execute(model)).getMessage();
            assertTrue(message.contains("positive index from the displayed client list"), message);
            assertTrue(message.contains("Use \"list\""), message);
        }
    }

    @Test
    public void parse_slashName_explainsLookupLimitationAndIndexAlternative() {
        AddressBookParser parser = new AddressBookParser();
        for (String command : new String[] {"session n/Sarah/Lim d/2026-12-15 t/1900",
            "cancelsession n/Sarah/Lim"}) {
            String message = assertThrows(ParseException.class, () -> parser.parseCommand(command)).getMessage();
            assertTrue(message.contains("session name lookup"), message);
            assertTrue(message.contains("displayed index"), message);
        }
    }

    @Test
    public void execute_duplicateName_explainsIdentityAndRecovery() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Fictional Alpha").build());
        String message = assertThrows(CommandException.class, () -> new AddressBookParser()
                .parseCommand("add n/FICTIONAL  ALPHA p/99998888").execute(model)).getMessage();
        assertTrue(message.contains("already has this name"), message);
        assertTrue(message.contains("ignoring letter case and extra spaces"), message);
        assertTrue(message.contains("find NAME"), message);
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    private Logic createLogic(Model model) {
        return new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("clients.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
