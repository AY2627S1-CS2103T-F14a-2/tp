package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.EditCommandParser.MESSAGE_MIXED_EMPTY_TAG;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Regression checks across tag parsing, filtering, editing and persisted client data. */
public class TagManagementTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("spotter.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_addEditFilterAndReload_useOneTagPerCaseVariant() throws Exception {
        logic.execute("add n/Tag Probe p/91234567 t/Lower-Back t/lower-back t/Rehab t/rehab");
        assertEquals(2, model.getFilteredPersonList().getFirst().getTags().size());
        assertTrue(model.getFilteredPersonList().getFirst().getTags().stream()
                .anyMatch(tag -> tag.tagName.equals("Lower-Back")));
        logic.execute("filter t/LOWER-BACK");
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("edit 1 t/Strength t/strength");
        assertEquals(List.of("Strength"), model.getFilteredPersonList().getFirst().getTags().stream()
                .map(tag -> tag.tagName).toList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("edit 1 t/STRENGTH");
        assertEquals(List.of("STRENGTH"), storage.readAddressBook().orElseThrow().getPersonList().getFirst()
                .getTags().stream().map(tag -> tag.tagName).toList());
        logic.execute("edit 1 t/");
        assertTrue(storage.readAddressBook().orElseThrow().getPersonList().getFirst().getTags().isEmpty());
    }

    @Test
    public void execute_invalidTags_preserveClientFilterViewAndFile() throws Exception {
        logic.execute("add n/Tag Probe p/91234567 t/rehab");
        logic.execute("filter t/rehab");
        logic.execute("view 1");
        AddressBook before = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String tag : List.of("", "-rehab", "rehab-", "lower--back", "a".repeat(51))) {
            assertThrows(ParseException.class, () -> logic.execute("add n/Other Probe p/92345678 t/" + tag));
            if (!tag.isEmpty()) {
                assertThrows(ParseException.class, () -> logic.execute("edit 1 t/" + tag));
                assertThrows(ParseException.class, () -> logic.execute("filter t/" + tag));
            }
            assertEquals(before, model.getAddressBook());
            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals(before.getPersonList().getFirst(), model.getViewedPerson().getValue());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
        assertThrows(ParseException.class, Tag.MESSAGE_EMPTY, () -> logic.execute("add n/Other Probe p/92345678 t/"));
        for (String tags : List.of("t/rehab t/", "t/ t/rehab", "t/ t/")) {
            assertThrows(ParseException.class, MESSAGE_MIXED_EMPTY_TAG, () -> logic.execute("edit 1 " + tags));
            assertEquals(before, model.getAddressBook());
            assertEquals(before.getPersonList().getFirst(), model.getViewedPerson().getValue());
            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_maximumLengthTag_survivesSaveAndFilter() throws Exception {
        String tag = "a".repeat(50);
        logic.execute("add n/Boundary Probe p/91234567 t/" + tag);
        logic.execute("filter t/" + tag.toUpperCase(java.util.Locale.ROOT));
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void load_invalidTag_preservesOriginalFile() throws Exception {
        logic.execute("add n/Tag Probe p/91234567 t/Rehab");
        Path file = storage.getAddressBookFilePath();
        String saved = Files.readString(file);
        for (String tag : List.of("lower--back", "a".repeat(51))) {
            String invalid = saved.replace("\"Rehab\"", "\"" + tag + "\"");
            Files.writeString(file, invalid);
            assertThrows(DataLoadingException.class, () -> storage.readAddressBook());
            assertEquals(invalid, Files.readString(file));
        }
    }

    @Test
    public void load_caseVariants_collapseAndSurviveRestart() throws Exception {
        logic.execute("add n/Tag Probe p/91234567 t/Rehab");
        logic.execute("session 1 d/2026-12-15 t/0900");
        logic.execute("measure 1 d/2026-09-12 t/weight v/72.4");
        logic.execute("performance 1 d/2026-09-12 e/Squat s/3 r/8 w/60");
        Path file = storage.getAddressBookFilePath();
        Files.writeString(file, Files.readString(file).replace("\"Rehab\"", "\"Rehab\", \"rehab\""));
        ModelManager restarted = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(model.getAddressBook(), restarted.getAddressBook());
        assertEquals(List.of("Rehab"), restarted.getAddressBook().getPersonList().getFirst().getTags().stream()
                .map(tag -> tag.tagName).toList());
        storage.saveAddressBook(restarted.getAddressBook());
        assertEquals(restarted.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
