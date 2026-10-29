package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies deletion targets only the selected record and preserves the active filter.
 */
public class DeleteMeasureCommandTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);
    private static final Measurement WEIGHT = new Measurement(DATE, MeasurementType.WEIGHT, new BigDecimal("72.4"));

    @Test
    public void execute_filteredList_deletesOnlySelectedMeasurement() throws Exception {
        Model model = new ModelManager();
        Person other = new PersonBuilder().withName("Other Client").withMeasurements(WEIGHT).build();
        Person selected = new PersonBuilder().withName("Selected Client").withMeasurements(WEIGHT).build();
        model.addPerson(other);
        model.addPerson(selected);
        model.updateFilteredPersonList(person -> person.isSamePerson(selected));

        assertEquals("Deleted measurement for Selected Client: weight = 72.4 kg on 2026-09-12.",
                command().execute(model).getFeedbackToUser());
        assertEquals(List.of(selected.withoutMeasurement(DATE, MeasurementType.WEIGHT)), model.getFilteredPersonList());
        assertEquals(other, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_missingDateTypeOrEmptyHistory_leavesDataUnchanged() {
        for (List<Measurement> records : List.of(List.<Measurement>of(), List.of(WEIGHT),
                List.of(new Measurement(DATE, MeasurementType.WAIST, new BigDecimal("81"))))) {
            Model model = new ModelManager();
            model.addPerson(new PersonBuilder().withMeasurements(records.toArray(Measurement[]::new)).build());
            AddressBook before = new AddressBook(model.getAddressBook());
            LocalDate date = records.equals(List.of(WEIGHT)) ? DATE.minusDays(1) : DATE;
            DeleteMeasureCommand missing = new DeleteMeasureCommand(Index.fromOneBased(1), date,
                    MeasurementType.WEIGHT);
            assertEquals("No measurement record for weight on " + date + " was found for Amy Bee."
                            + "\nUse \"view 1\" to check this client's measurement history.",
                    assertThrows(CommandException.class, () -> missing.execute(model)).getMessage());
            assertEquals(before, model.getAddressBook());
        }
    }

    @Test
    public void execute_outOfRangeIndex_leavesDataUnchanged() {
        Model model = new ModelManager();
        AddressBook before = new AddressBook(model.getAddressBook());
        assertEquals(DeleteMeasureCommand.MESSAGE_INVALID_INDEX + "\nUse: " + DeleteMeasureCommand.MESSAGE_USAGE,
                assertThrows(CommandException.class, () -> command().execute(model)).getMessage());
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void equals_comparesIndexDateAndType() {
        assertEquals(command(), command());
        assertNotEquals(command(), new DeleteMeasureCommand(Index.fromOneBased(2), DATE, MeasurementType.WEIGHT));
        assertNotEquals(command(), new DeleteMeasureCommand(Index.fromOneBased(1), DATE.minusDays(1),
                MeasurementType.WEIGHT));
        assertNotEquals(command(), new DeleteMeasureCommand(Index.fromOneBased(1), DATE, MeasurementType.WAIST));
        assertNotEquals(command(), null);
        assertNotEquals(command(), "deletemeasure");
        assertThrows(NullPointerException.class, () -> command().execute(null));
        assertThrows(NullPointerException.class, () -> new DeleteMeasureCommand(null, DATE, MeasurementType.WEIGHT));
    }

    private DeleteMeasureCommand command() {
        return new DeleteMeasureCommand(Index.fromOneBased(1), DATE, MeasurementType.WEIGHT);
    }
}
