package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 100;

    public static final String MESSAGE_CONSTRAINTS =
            "Names must start with a Latin letter or digit and contain only Latin letters, accents, digits, "
                    + "spaces, apostrophes, hyphens, full stops and slashes.";
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Names should be at most " + MAX_LENGTH + " characters long";

    // Combining accents preserve decomposed Latin names without changing the existing identity rule.
    public static final String VALIDATION_REGEX = "[\\p{IsLatin}0-9][\\p{IsLatin}\\p{M}0-9 '’./-]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}, trimming it and collapsing repeated internal spaces.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalizeSpaces(name);
    }

    /**
     * Returns true if a given string is a valid name once trimmed and its internal spaces collapsed.
     */
    public static boolean isValidName(String test) {
        String normalized = normalizeSpaces(test);
        return normalized.length() <= MAX_LENGTH && normalized.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is longer than {@code MAX_LENGTH} once trimmed and its internal spaces collapsed.
     */
    public static boolean isTooLong(String test) {
        return normalizeSpaces(test).length() > MAX_LENGTH;
    }

    /**
     * Returns true if both names refer to the same client, ignoring case.
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
