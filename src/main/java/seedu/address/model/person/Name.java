package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.Collator;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.Locale;

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

    // Combining accents support Latin names whose accents have no precomposed Unicode form.
    public static final String VALIDATION_REGEX = "[\\p{IsLatin}0-9][\\p{IsLatin}\\p{M}0-9 '’./-]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}, normalizing equivalent accents and whitespace.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalize(name);
    }

    /**
     * Returns true if a given string is a valid name after accent and whitespace normalization.
     */
    public static boolean isValidName(String test) {
        String normalized = normalize(test);
        return normalized.length() <= MAX_LENGTH && normalized.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string exceeds {@code MAX_LENGTH} after accent and whitespace normalization.
     */
    public static boolean isTooLong(String test) {
        return normalize(test).length() > MAX_LENGTH;
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
                && fullName.equalsIgnoreCase(normalize(candidate));
    }

    /**
     * Returns a canonical spelling for storage, validation and name-based lookup.
     * NFC unifies equivalent accent encodings without removing accents or transliterating scripts.
     */
    public static String normalize(String name) {
        return Normalizer.normalize(normalizeSpaces(name), Normalizer.Form.NFC);
    }

    /**
     * Trims outer whitespace and collapses internal whitespace using the same rule as duplicate detection.
     */
    public static String normalizeSpaces(String name) {
        return requireNonNull(name).trim().replaceAll("\\s+", " ");
    }

    /**
     * Creates an English alphabetical comparator for canonical client names, ignoring case.
     * Each caller owns its collator so mutable locale state is not shared between independent sorts.
     * A character comparison breaks collation ties between distinct names without changing client identity.
     */
    public static Comparator<String> alphabeticalComparator() {
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        collator.setStrength(Collator.SECONDARY);
        Comparator<String> alphabetical = collator::compare;
        return alphabetical.thenComparing(String.CASE_INSENSITIVE_ORDER);
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
