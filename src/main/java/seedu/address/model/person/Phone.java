package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers must contain at least 3 digits and only digits, spaces, letters A-Z, "
                    + "plus signs, hyphens, brackets and full stops.";
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Phone numbers should be at most " + MAX_LENGTH + " characters long";
    public static final String VALIDATION_REGEX =
            "(?=(?:[^0-9]*[0-9]){3})[0-9A-Za-z +().-]{1," + MAX_LENGTH + "}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = normalize(phone);
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return normalize(test).matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the stored form would exceed {@code MAX_LENGTH} characters.
     */
    public static boolean isTooLong(String test) {
        return normalize(test).length() > MAX_LENGTH;
    }

    /**
     * Returns the complete digit sequence for shared-phone warnings, not a dialable-number interpretation.
     */
    public String getDigits() {
        return value.replaceAll("[^0-9]", "");
    }

    /**
     * Preserves labels and punctuation but keeps plain digit groups compatible with existing stored numbers.
     */
    private static String normalize(String phone) {
        String normalized = requireNonNull(phone).trim().replaceAll(" +", " ");
        return normalized.matches("[0-9 ]+") ? normalized.replace(" ", "") : normalized;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
