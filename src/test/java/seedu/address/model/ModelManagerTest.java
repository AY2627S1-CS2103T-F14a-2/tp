package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 25, 10, 0);

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getUpcomingSessionList_mixedBookings_onlyUpcomingSoonestFirst() {
        Person later = new PersonBuilder(ALICE).withNextSession("2026-11-02 09:00").build();
        Person sooner = new PersonBuilder(BENSON).withNextSession("2026-11-01 18:00").build();
        Person passed = new PersonBuilder(CARL).withNextSession("2026-10-01 09:00").build();
        modelManager.addPerson(later);
        modelManager.addPerson(sooner);
        modelManager.addPerson(passed);
        modelManager.addPerson(DANIEL); // no booking

        modelManager.updateUpcomingSessionList(NOW);

        assertEquals(List.of(sooner, later), modelManager.getUpcomingSessionList());
    }

    @Test
    public void getUpcomingSessionList_sessionAtNow_included() {
        Person atNow = new PersonBuilder(ALICE).withNextSession("2026-10-25 10:00").build();
        modelManager.addPerson(atNow);

        modelManager.updateUpcomingSessionList(NOW);

        assertEquals(List.of(atNow), modelManager.getUpcomingSessionList());
    }

    @Test
    public void getUpcomingSessionList_bookAndCancel_updatesWithoutRefresh() {
        modelManager.addPerson(ALICE);
        modelManager.updateUpcomingSessionList(NOW);
        Person booked = new PersonBuilder(ALICE).withNextSession("2026-11-01 18:00").build();

        modelManager.setPerson(ALICE, booked);
        assertEquals(List.of(booked), modelManager.getUpcomingSessionList());

        modelManager.setPerson(booked, ALICE);
        assertEquals(List.of(), modelManager.getUpcomingSessionList());
    }

    @Test
    public void updateUpcomingSessionList_sessionHasPassed_removesClient() {
        modelManager.addPerson(new PersonBuilder(ALICE).withNextSession("2026-11-01 18:00").build());
        modelManager.updateUpcomingSessionList(NOW);

        modelManager.updateUpcomingSessionList(LocalDateTime.of(2026, 11, 1, 18, 1));

        assertEquals(List.of(), modelManager.getUpcomingSessionList());
    }

    @Test
    public void updateUpcomingSessionList_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateUpcomingSessionList(null));
    }

    @Test
    public void getUpcomingSessionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getUpcomingSessionList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
