package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSessionComparator;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final FilteredList<Person> upcomingSessionPersons;
    private final SortedList<Person> sortedUpcomingSessionPersons;
    private Predicate<Person> filteredPersonListPredicate = PREDICATE_SHOW_ALL_PERSONS;
    private final SimpleObjectProperty<Person> viewedPerson = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons, new PersonSessionComparator(LocalDateTime.now()));
        // Built from the whole address book, so find and filter never hide an upcoming session.
        LocalDateTime now = LocalDateTime.now();
        upcomingSessionPersons = new FilteredList<>(this.addressBook.getPersonList(), hasSessionFrom(now));
        sortedUpcomingSessionPersons = new SortedList<>(upcomingSessionPersons, new PersonSessionComparator(now));
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        if (!this.addressBook.getPersonList().contains(viewedPerson.get())) {
            viewedPerson.set(null);
        }
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        if (target.equals(viewedPerson.get())) {
            viewedPerson.set(null);
        }
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
        // Person is immutable, so the viewed client follows its replacement.
        if (target.equals(viewedPerson.get())) {
            viewedPerson.set(editedPerson);
        }
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersonListPredicate = predicate;
        filteredPersons.setPredicate(predicate);
        sortedPersons.setComparator(new PersonSessionComparator(LocalDateTime.now()));
    }

    @Override
    public Predicate<Person> getFilteredPersonListPredicate() {
        return filteredPersonListPredicate;
    }

    //=========== Upcoming Session List Accessors ===========================================================

    @Override
    public ObservableList<Person> getUpcomingSessionList() {
        return sortedUpcomingSessionPersons;
    }

    @Override
    public void updateSessionLists(LocalDateTime now) {
        requireNonNull(now);
        sortedPersons.setComparator(new PersonSessionComparator(now));
        upcomingSessionPersons.setPredicate(hasSessionFrom(now));
        sortedUpcomingSessionPersons.setComparator(new PersonSessionComparator(now));
    }

    /**
     * Returns a predicate that keeps clients with a session at or after {@code now}.
     * This is the client card's rule: a session before now has passed and is no longer upcoming.
     */
    private static Predicate<Person> hasSessionFrom(LocalDateTime now) {
        return person -> person.getNextSession().map(session -> !session.value.isBefore(now)).orElse(false);
    }

    //=========== Viewed Person =============================================================================

    @Override
    public ObservableValue<Person> getViewedPerson() {
        return viewedPerson;
    }

    @Override
    public void setViewedPerson(Person person) {
        assert person == null || addressBook.getPersonList().contains(person)
                : "The viewed client must exist in the address book";
        logger.fine(person == null ? "Clearing the viewed client" : "Viewing a client");
        viewedPerson.set(person);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && Objects.equals(viewedPerson.get(), otherModelManager.viewedPerson.get());
    }

}
