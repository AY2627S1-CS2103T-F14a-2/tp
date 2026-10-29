package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

/**
 * Formats plain eight-digit phone numbers for display, preserving other stored formats.
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
        if (phone.length() != GROUPED_PHONE_LENGTH || !phone.matches("[0-9]+")) {
            return phone;
        }
        return phone.substring(0, GROUP_SIZE) + " " + phone.substring(GROUP_SIZE);
    }
}
