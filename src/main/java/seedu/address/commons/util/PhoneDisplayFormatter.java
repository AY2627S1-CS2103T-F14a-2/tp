package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

/**
 * Formats stored phone numbers for display. Only the displayed text changes; the stored value stays digits-only.
 */
public class PhoneDisplayFormatter {

    private static final int GROUPED_PHONE_LENGTH = 8;
    private static final int GROUP_SIZE = 4;

    private PhoneDisplayFormatter() {}

    /**
     * Returns {@code phone} split into two groups of four digits (e.g. "9927 2758") if it has exactly
     * eight digits, otherwise returns {@code phone} unchanged.
     */
    public static String format(String phone) {
        requireNonNull(phone);
        if (phone.length() != GROUPED_PHONE_LENGTH) {
            return phone;
        }
        return phone.substring(0, GROUP_SIZE) + " " + phone.substring(GROUP_SIZE);
    }
}
