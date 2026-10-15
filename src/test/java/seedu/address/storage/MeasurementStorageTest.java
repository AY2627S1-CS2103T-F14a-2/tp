package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks persisted measurement data and backward compatibility with existing clients.
 */
public class MeasurementStorageTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void saveAndReload_multipleRecords_preservesMeasurements() throws Exception {
        Person client = new PersonBuilder().withMeasurements(
                new Measurement(LocalDate.of(2026, 9, 12), MeasurementType.WEIGHT, new BigDecimal("72.4")),
                new Measurement(LocalDate.of(2026, 9, 12), MeasurementType.WAIST, new BigDecimal("81")),
                new Measurement(LocalDate.of(2026, 9, 13), MeasurementType.BODYFAT, new BigDecimal("18.45")))
                .build();
        AddressBook book = new AddressBook();
        book.addPerson(client);
        Path file = temporaryFolder.resolve("clients.json");
        new JsonAddressBookStorage(file).saveAddressBook(book);
        assertEquals(book, new JsonAddressBookStorage(file).readAddressBook().orElseThrow());
    }

    @Test
    public void saveAndReload_futureDatedMeasurement_preservesRecord() throws Exception {
        // A saved record may appear future-dated after a clock or time-zone change.
        LocalDate date = LocalDate.now().plusDays(1);
        Measurement measurement = new JsonAdaptedMeasurement(date.toString(), "weight", "72.4").toModelType();
        assertEquals(date, measurement.getDate());
        assertEquals(MeasurementType.WEIGHT, measurement.getType());
        assertEquals(new BigDecimal("72.4"), measurement.getValue());
        Person client = new PersonBuilder().withMeasurements(measurement).build();
        AddressBook book = new AddressBook();
        book.addPerson(client);
        Path file = temporaryFolder.resolve("clients.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(book);
        assertEquals(book, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void load_legacyClient_hasNoMeasurements() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(new PersonBuilder().build()));
        json = json.replaceAll(",?\\s*\"measurements\"\\s*:\\s*\\[\\s*\\]", "");
        assertEquals(List.of(), JsonUtil.fromJsonString(json, JsonAdaptedPerson.class)
                .toModelType().getMeasurements());
    }

    @Test
    public void load_invalidMeasurement_throwsIllegalValueException() {
        for (JsonAdaptedMeasurement record : List.of(
                new JsonAdaptedMeasurement(null, "weight", "72"),
                new JsonAdaptedMeasurement("2026-02-30", "weight", "72"),
                new JsonAdaptedMeasurement("2026-09-12", "height", "72"),
                new JsonAdaptedMeasurement("2026-09-12", "weight", "NaN"),
                new JsonAdaptedMeasurement("2026-09-12", "weight", "501"),
                new JsonAdaptedMeasurement("2026-09-12", "weight", "72.123"))) {
            assertThrows(IllegalValueException.class, record::toModelType);
        }
    }

    @Test
    public void load_duplicateRecords_rejected() {
        Person client = new PersonBuilder().build();
        JsonAdaptedMeasurement record = new JsonAdaptedMeasurement("2026-09-12", "weight", "72");
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(client.getName().fullName, client.getPhone().value,
                client.getEmail().value, client.getAddress().value, List.of(), List.of(record, record));
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }
}
