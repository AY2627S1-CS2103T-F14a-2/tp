package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Person;

/**
 * Verifies measurement commands update only the selected client and preserve the displayed filter.
 */
public class MeasureCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Measurement measurement = new Measurement(LocalDate.of(2026, 9, 12),
            MeasurementType.WEIGHT, new BigDecimal("72.4"));

    @Test
    public void execute_recordThenReplace_correctMessagesAndRecordCount() throws Exception {
        Person client = model.getFilteredPersonList().get(0);
        Person otherClient = model.getFilteredPersonList().get(1);
        MeasureCommand command = new MeasureCommand(Index.fromOneBased(1), measurement);
        assertEquals("Recorded measurement for " + client.getName() + ": weight = 72.4 kg on 2026-09-12.",
                command.execute(model).getFeedbackToUser());
        Measurement replacement = new Measurement(measurement.getDate(), MeasurementType.WEIGHT,
                new BigDecimal("71"));
        assertEquals("Updated measurement for " + client.getName() + ": weight on 2026-09-12: 72.4 → 71 kg.",
                new MeasureCommand(Index.fromOneBased(1), replacement).execute(model).getFeedbackToUser());
        assertEquals(client.withMeasurement(replacement), model.getFilteredPersonList().get(0));
        assertEquals(List.of(replacement), model.getFilteredPersonList().get(0).getMeasurements());
        assertEquals(otherClient, model.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_replaceEachType_usesMatchingRecordAndPreservesOthers() throws Exception {
        for (MeasurementType type : MeasurementType.values()) {
            Model testModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Person client = testModel.getFilteredPersonList().get(0);
            Measurement original = new Measurement(measurement.getDate(), type, new BigDecimal("20.25"));
            Measurement otherDate = new Measurement(measurement.getDate().minusDays(1), type,
                    new BigDecimal("30"));
            MeasurementType otherType = type == MeasurementType.WEIGHT
                    ? MeasurementType.WAIST : MeasurementType.WEIGHT;
            Measurement sameDate = new Measurement(measurement.getDate(), otherType, new BigDecimal("40"));
            Person recorded = client.withMeasurement(otherDate).withMeasurement(sameDate).withMeasurement(original);
            testModel.setPerson(client, recorded);
            Measurement replacement = new Measurement(measurement.getDate(), type, new BigDecimal("19.5"));

            String expected = "Updated measurement for " + client.getName() + ": " + type
                    + " on 2026-09-12: 20.25 → 19.5 " + type.getUnit() + ".";
            assertEquals(expected, new MeasureCommand(Index.fromOneBased(1), replacement)
                    .execute(testModel).getFeedbackToUser());
            assertEquals(recorded.withMeasurement(replacement), testModel.getFilteredPersonList().get(0));
            assertEquals(3, testModel.getFilteredPersonList().get(0).getMeasurements().size());

            String unchanged = "Updated measurement for " + client.getName() + ": " + type
                    + " on 2026-09-12: 19.5 → 19.5 " + type.getUnit() + ".";
            assertEquals(unchanged, new MeasureCommand(Index.fromOneBased(1), replacement)
                    .execute(testModel).getFeedbackToUser());
            assertEquals(recorded.withMeasurement(replacement), testModel.getFilteredPersonList().get(0));
        }
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() throws Exception {
        Person firstClient = model.getFilteredPersonList().get(0);
        Person selected = model.getFilteredPersonList().get(1);
        model.updateFilteredPersonList(person -> person.isSamePerson(selected));
        new MeasureCommand(Index.fromOneBased(1), measurement).execute(model);
        assertEquals(List.of(selected.withMeasurement(measurement)), model.getFilteredPersonList());
        assertEquals(firstClient, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_invalidDisplayedIndex_doesNotChangeData() {
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());
        MeasureCommand command = new MeasureCommand(Index.fromOneBased(before.size() + 1), measurement);
        assertEquals(MeasureCommand.MESSAGE_INVALID_INDEX + "\nUse: " + MeasureCommand.MESSAGE_USAGE,
                assertThrows(CommandException.class, () -> command.execute(model)).getMessage());
        assertEquals(before, model.getAddressBook().getPersonList());
    }
}
