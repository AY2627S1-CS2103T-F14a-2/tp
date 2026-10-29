package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private static final Measurement WEIGHT_RECORD = new Measurement(LocalDate.of(2026, 9, 12),
            MeasurementType.WEIGHT, new BigDecimal("72.4"));

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_contactEdit_preservesMeasurements() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person client = new PersonBuilder(original).withMeasurements(WEIGHT_RECORD).build();
        model.setPerson(original, client);
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());

        command.execute(model);

        assertEquals(List.of(WEIGHT_RECORD), model.getFilteredPersonList().get(0).getMeasurements());
        assertEquals(VALID_PHONE_BOB, model.getFilteredPersonList().get(0).getPhone().value);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + ClientOrderNote.MESSAGE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeEmailUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithRecords = new PersonBuilder(firstPerson).withNextSession("2026-10-12 17:00")
                .withMeasurements(WEIGHT_RECORD).withGymPerformances(BENCH_PRESS).build();
        model.setPerson(firstPerson, personWithRecords);
        Person editedPerson = new PersonBuilder(personWithRecords).withoutEmail().build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withoutEmail().build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithRecords, editedPerson);

        // Person equality covers the session, measurements and performances, so this checks they are kept.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithoutEmail_keepsNoEmail() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithoutEmail = new PersonBuilder(firstPerson).withoutEmail().build();
        model.setPerson(firstPerson, personWithoutEmail);
        Person editedPerson = new PersonBuilder(personWithoutEmail).withPhone(VALID_PHONE_BOB).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithoutEmail, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clientWithGymPerformances_keepsRecords() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstPersonWithRecord = firstPerson.withGymPerformance(BENCH_PRESS);
        model.setPerson(firstPerson, firstPersonWithRecord);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Person editedPerson = new PersonBuilder(firstPersonWithRecord).withPhone(VALID_PHONE_BOB).build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPersonWithRecord, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        assertEquals(List.of(BENCH_PRESS), model.getFilteredPersonList().get(0).getGymPerformances());
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + ClientOrderNote.MESSAGE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_phoneChangedToHiddenMatches_warnsInAddressBookOrder() {
        Person original = new PersonBuilder().withName("Original Client").withPhone("81112222").build();
        Person zoe = new PersonBuilder().withName("Zoe Tan").withPhone("+65 9123-4567 (HP)").build();
        Person amy = new PersonBuilder().withName("Amy Tan").withPhone("(65) 9123.4567").build();
        model = new ModelManager();
        model.addPerson(zoe);
        model.addPerson(original);
        model.addPerson(amy);
        model.updateFilteredPersonList(person -> person.equals(original));
        Person edited = new PersonBuilder(original).withName("Renamed Client").withPhone("6591234567").build();
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName("Renamed Client").withPhone("6591234567").build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(edited))
                + "\nWarning: Zoe Tan also has this phone number."
                + "\nWarning: Amy Tan also has this phone number."
                + ClientOrderNote.MESSAGE;

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @ParameterizedTest
    @MethodSource("unchangedPhoneEdits")
    public void execute_unchangedPhoneDigits_warnsOnlyAboutOtherClient(EditPersonDescriptor descriptor) {
        Person original = new PersonBuilder().withName("Original Client").withPhone("91234567").build();
        Person other = new PersonBuilder().withName("Other Client").withPhone("9123-4567 (HP)").build();
        model = new ModelManager();
        model.addPerson(original);
        model.addPerson(other);
        Person edited = new PersonBuilder(original)
                .withName(descriptor.getName().orElse(original.getName()).fullName)
                .withPhone(descriptor.getPhone().orElse(original.getPhone()).value)
                .withEmail(descriptor.getEmail().orElseThrow().value).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(edited))
                + "\nWarning: Other Client also has this phone number."
                // Only a rename that sorts after "Other Client" swaps the two clients' indices.
                + (Name.alphabeticalComparator().compare(edited.getName().fullName, other.getName().fullName) > 0
                        ? ClientOrderNote.MESSAGE : "");

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filterResetShiftsOtherClient_notesOrderChange() throws Exception {
        // #210: Aaron keeps index 1, but index 2 changes from Chris to Bea once the filter is cleared
        Person aaron = new PersonBuilder().withName("Aaron").withPhone("81110000").withTags("keep").build();
        Person bea = new PersonBuilder().withName("Bea").withPhone("82220000").withTags("other").build();
        Person chris = new PersonBuilder().withName("Chris").withPhone("83330000").withTags("keep").build();
        model = new ModelManager();
        model.addPerson(aaron);
        model.addPerson(bea);
        model.addPerson(chris);
        model.updateFilteredPersonList(person -> person.getTags().equals(aaron.getTags()));
        assertEquals(List.of(aaron, chris), model.getFilteredPersonList());

        CommandResult result = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone("99998888").build()).execute(model);

        assertTrue(result.getFeedbackToUser().endsWith(ClientOrderNote.MESSAGE));
        Person editedAaron = new PersonBuilder(aaron).withPhone("99998888").build();
        assertEquals(List.of(editedAaron, bea, chris), model.getFilteredPersonList());
    }

    @Test
    public void execute_filterResetAppendsClientsOnly_noOrderNote() throws Exception {
        // Every previously displayed index still selects the same client, so there is nothing to recheck.
        Person first = model.getFilteredPersonList().get(0);
        model.updateFilteredPersonList(person -> person.equals(first));
        CommandResult result = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build()).execute(model);
        assertFalse(result.getFeedbackToUser().contains(ClientOrderNote.MESSAGE));
    }

    @Test
    public void execute_phoneOnlyEditInFullList_noOrderNote() throws Exception {
        List<Person> before = List.copyOf(model.getFilteredPersonList());
        CommandResult result = new EditCommand(INDEX_SECOND_PERSON,
                new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build()).execute(model);
        assertFalse(result.getFeedbackToUser().contains(ClientOrderNote.MESSAGE));
        assertEquals(before.get(2), model.getFilteredPersonList().get(2));
    }

    @Test
    public void execute_renameReordersFullList_notesOrderChange() throws Exception {
        CommandResult result = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName("Zack Last").build()).execute(model);
        assertTrue(result.getFeedbackToUser().endsWith(ClientOrderNote.MESSAGE));
        assertEquals("Zack Last", model.getFilteredPersonList().getLast().getName().fullName);
    }

    @ParameterizedTest
    @CsvSource({
        "91234567, +65 9123 4567",
        "91234567, 9123 4567 (HP) 6111-2222 (Office)",
        "9123 4567 (HP) 6111-2222 (Office), 91234567"
    })
    public void execute_differentCompletePhoneDigits_successWithoutWarning(String otherPhone, String editedPhone) {
        Person original = new PersonBuilder().withName("Original Client").withPhone("81112222").build();
        Person other = new PersonBuilder().withName("Other Client").withPhone(otherPhone).build();
        model = new ModelManager();
        model.addPerson(original);
        model.addPerson(other);
        Person edited = new PersonBuilder(original).withPhone(editedPhone).build();
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(editedPhone).build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);

        assertCommandSuccess(command, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(edited)), expectedModel);
    }

    private static Stream<EditPersonDescriptor> unchangedPhoneEdits() {
        return Stream.of(
                new EditPersonDescriptorBuilder().withEmail("changed@example.com").build(),
                new EditPersonDescriptorBuilder().withEmail("changed@example.com").withName("Renamed Client").build(),
                new EditPersonDescriptorBuilder().withEmail("changed@example.com").withName("ORIGINAL  client").build(),
                new EditPersonDescriptorBuilder().withEmail("changed@example.com").withPhone("9123-4567").build());
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameDifferentCaseUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String sameNameDifferentCase = firstPerson.getName().fullName.toUpperCase();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(sameNameDifferentCase).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_changeNameCaseOnlyUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String lowerCaseName = firstPerson.getName().fullName.toLowerCase();
        Person editedPerson = new PersonBuilder(firstPerson).withName(lowerCaseName).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(lowerCaseName).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameAndDifferentCommands_comparesValues() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand(true)));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toString_validCommand_containsFields() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
