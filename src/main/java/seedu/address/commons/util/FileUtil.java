package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists. A leading UTF-8 byte-order mark is not returned as content.
     */
    public static String readFromFile(Path file) throws IOException {
        String content = new String(Files.readAllBytes(file), CHARSET);
        // Some editors, e.g. Notepad's "UTF-8 with BOM" option, start the file with a byte-order mark.
        return content.startsWith("\uFEFF") ? content.substring(1) : content;
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        Files.write(file, content.getBytes(CHARSET));
    }

}
