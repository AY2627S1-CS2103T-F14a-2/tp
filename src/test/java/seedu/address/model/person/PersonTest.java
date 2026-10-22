package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalGymPerformances.SQUAT;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
        String nameWithRepeatedSpaces = VALID_NAME_BOB.replace(" ", "   ");
        editedBob = new PersonBuilder(BOB).withName(nameWithRepeatedSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // missing email -> returns false
        editedAlice = new PersonBuilder(ALICE).withoutEmail().build();
        assertFalse(ALICE.equals(editedAlice));
        assertFalse(editedAlice.equals(ALICE));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void session_affectsEqualityButNotClientIdentity() {
        Person booked = new PersonBuilder(ALICE).withNextSession("2026-10-12 17:00").build();
        Person sameBooking = new PersonBuilder(booked).build();
        Person replacement = new PersonBuilder(booked).withNextSession("2026-10-13 17:00").build();
        assertEquals(booked, sameBooking);
        assertEquals(booked.hashCode(), sameBooking.hashCode());
        assertFalse(booked.equals(ALICE));
        assertFalse(booked.equals(replacement));
        assertTrue(booked.isSamePerson(ALICE));
        assertTrue(ALICE.getNextSession().isEmpty());
    }

    @Test
    public void hashCode_equalPersons_sameHashCode() {
        assertEquals(ALICE.hashCode(), new PersonBuilder(ALICE).build().hashCode());

        // missing email -> no exception, equal persons still share a hash code
        Person aliceWithoutEmail = new PersonBuilder(ALICE).withoutEmail().build();
        assertEquals(aliceWithoutEmail.hashCode(), new PersonBuilder(ALICE).withoutEmail().build().hashCode());
    }

    @Test
    public void equals_differentGymPerformances_returnsFalse() {
        // Saving relies on equals to detect changes, so a new record must make the client unequal
        Person aliceWithRecord = new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build();
        assertFalse(ALICE.equals(aliceWithRecord));
        assertTrue(aliceWithRecord.equals(new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build()));
        assertEquals(aliceWithRecord.hashCode(),
                new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build().hashCode());
    }

    @Test
    public void hasGymPerformance() {
        Person alice = new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build();

        GymPerformance sameEntry = new GymPerformance(BENCH_PRESS.getDate(), new ExerciseName("bench press"), 5, 5,
                new Load("80"));
        assertTrue(alice.hasGymPerformance(sameEntry));
        assertFalse(ALICE.hasGymPerformance(BENCH_PRESS));
    }

    @Test
    public void withGymPerformance_preservesOtherDetails() {
        Person alice = new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build();
        GymPerformance squat = new GymPerformance(BENCH_PRESS.getDate(), new ExerciseName("Squat"), 5, 5,
                new Load("100"));

        Person updatedAlice = alice.withGymPerformance(squat);

        assertEquals(new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS, squat).build(), updatedAlice);
        assertEquals(List.of(BENCH_PRESS), alice.getGymPerformances()); // original unchanged
    }

    @Test
    public void withGymPerformance_sameEntry_replacesRecord() {
        Person alice = new PersonBuilder(ALICE).withGymPerformances(BENCH_PRESS).build();
        GymPerformance correctedBench = new GymPerformance(BENCH_PRESS.getDate(), new ExerciseName("Bench Press"),
                4, 6, new Load("65"));

        assertEquals(List.of(correctedBench), alice.withGymPerformance(correctedBench).getGymPerformances());
    }

    @Test
    public void withoutGymPerformance_preservesSessionMeasurementsAndOtherRecords() {
        Measurement weight = new Measurement(LocalDate.of(2026, 9, 12), MeasurementType.WEIGHT,
                new BigDecimal("72.4"));
        Person alice = new PersonBuilder(ALICE).withNextSession("2026-10-12 17:00").withMeasurements(weight)
                .withGymPerformances(BENCH_PRESS, SQUAT).build();

        Person updatedAlice = alice.withoutGymPerformance(BENCH_PRESS);

        Person expected = new PersonBuilder(ALICE).withNextSession("2026-10-12 17:00").withMeasurements(weight)
                .withGymPerformances(SQUAT).build();
        assertEquals(expected, updatedAlice);
        assertEquals(List.of(BENCH_PRESS, SQUAT), alice.getGymPerformances()); // original unchanged
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail().orElse(null) + ", tags=" + ALICE.getTags()
                + ", nextSession=null, performanceHistory=" + ALICE.getPerformanceHistory() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
