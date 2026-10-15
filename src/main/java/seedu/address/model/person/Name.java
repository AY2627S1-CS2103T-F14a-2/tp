package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and should not be blank";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both names refer to the same client, ignoring case,
     * leading and trailing spaces, and repeated internal spaces.
     */
    public boolean isSameName(Name otherName) {
        return otherName != null
                && matches(otherName.fullName);
    }

    /**
     * Returns whether the supplied full name identifies this client, ignoring case and extra whitespace.
     */
    public boolean matches(String candidate) {
        return candidate != null
                && normalizeSpaces(fullName).equalsIgnoreCase(normalizeSpaces(candidate));
    }

    /**
     * Trims outer whitespace and collapses internal whitespace using the same rule as duplicate detection.
     */
    public static String normalizeSpaces(String name) {
        return requireNonNull(name).trim().replaceAll("\\s+", " ");
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
