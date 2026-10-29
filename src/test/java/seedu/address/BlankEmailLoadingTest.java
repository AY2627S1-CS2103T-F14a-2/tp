package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Tests optional-email edits through real startup, read-only commands, saving and a second startup. */
public class BlankEmailLoadingTest {
    private static final Person CLIENT_WITH_RECORDS = new PersonBuilder().withName("Ada BlankEmail")
            .withPhone("91234567").withEmail("ada@example.com").withTags("rehab")
            .withNextSession("2099-12-15 17:00")
            .withMeasurements(new Measurement(LocalDate.of(2020, 1, 1), MeasurementType.WEIGHT,
                    new BigDecimal("72.4")))
            .withGymPerformances(BENCH_PRESS).build();
    private static final Person OTHER_CLIENT = new PersonBuilder().withName("Bea Control")
            .withEmail("bea@example.com").build();

    @TempDir
    public Path folder;

    @ParameterizedTest(name = "{0} email loads without a warning and survives saving")
    @MethodSource("absentEmailRepresentations")
    public void initModelManager_absentEmail_preservesAllClientsAndRecords(String description,
            String email, boolean omitField) throws Exception {
        Path dataFile = writeEditedEmail(description, email, omitField);
        byte[] beforeLoading = Files.readAllBytes(dataFile);
        Storage storage = createStorage(dataFile);
        MainApp app = new MainApp();
        Model model = app.initModelManager(storage, new UserPrefs());
        AddressBook expected = new AddressBook();
        expected.addPerson(new PersonBuilder(CLIENT_WITH_RECORDS).withoutEmail().build());
        expected.addPerson(OTHER_CLIENT);

        assertEquals(expected, model.getAddressBook());
        assertEquals(Optional.empty(), app.getStartupWarning());
        new LogicManager(model, storage).execute("list");
        assertArrayEquals(beforeLoading, Files.readAllBytes(dataFile));

        storage.saveAddressBook(model.getAddressBook());
        MainApp restartedApp = new MainApp();
        Model restartedModel = restartedApp.initModelManager(createStorage(dataFile), new UserPrefs());
        assertEquals(expected, restartedModel.getAddressBook());
        assertEquals(Optional.empty(), restartedApp.getStartupWarning());
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", " ada@example.com "})
    public void initModelManager_nonblankInvalidEmail_preservesFileAndShowsWarning(String email) throws Exception {
        Path dataFile = writeEditedEmail("invalid", email, false);
        byte[] beforeLoading = Files.readAllBytes(dataFile);
        MainApp app = new MainApp();
        Model model = app.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(Optional.of(MainApp.MESSAGE_DATA_LOAD_FAILURE), app.getStartupWarning());
        assertArrayEquals(beforeLoading, Files.readAllBytes(dataFile));
    }

    /** Covers both existing absence formats and whitespace accepted by a blank CLI e/ field. */
    private static Stream<Arguments> absentEmailRepresentations() {
        return Stream.of(
                Arguments.of("missing", null, true),
                Arguments.of("null", null, false),
                Arguments.of("empty", "", false),
                Arguments.of("spaces", "   ", false),
                Arguments.of("tabs-and-newline", "\t\n", false),
                Arguments.of("unicode-whitespace", "\u2003", false));
    }

    /** Makes a realistic manual edit to a valid file containing every training field and another client. */
    private Path writeEditedEmail(String filename, String email, boolean omitField) throws Exception {
        Path dataFile = folder.resolve(filename + ".json");
        AddressBook original = new AddressBook();
        original.addPerson(CLIENT_WITH_RECORDS);
        original.addPerson(OTHER_CLIENT);
        new JsonAddressBookStorage(dataFile).saveAddressBook(original);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) mapper.readTree(Files.readString(dataFile));
        ObjectNode client = (ObjectNode) json.get("persons").get(0);
        if (omitField) {
            client.remove("email");
        } else {
            client.put("email", email);
        }
        Files.writeString(dataFile, json.toString());
        return dataFile;
    }

    /** Uses the same JSON-backed storage boundary as application startup. */
    private Storage createStorage(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(folder.resolve("preferences.json")));
    }
}
