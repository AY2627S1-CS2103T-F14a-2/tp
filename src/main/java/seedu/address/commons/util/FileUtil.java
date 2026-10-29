package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
     * Writes given string to a file, creating the file and its parent folders if needed.
     * The content is written to a temporary file in the same folder first, so a failed write leaves any
     * existing file unchanged.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        byte[] bytes = content.getBytes(CHARSET);
        writeToFile(file, (temp, target) -> Files.write(temp, bytes), FileUtil::moveIntoPlace);
    }

    // Package-private only so that tests can make writing or moving the temporary file fail.
    static void writeToFile(Path file, FileStep write, FileStep move) throws IOException {
        if (Files.exists(file) && !Files.isWritable(file)) {
            // A move would replace a read-only file, so keep refusing as a direct write did.
            throw new AccessDeniedException(file.toString());
        }
        createParentDirsOfFile(file);
        Path temp = Files.createTempFile(file.toAbsolutePath().getParent(), "spotter-", ".tmp");
        try {
            write.apply(temp, file);
            move.apply(temp, file);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static void moveIntoPlace(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** One I/O step of {@link #writeToFile(Path, FileStep, FileStep)} on the temporary and target files. */
    @FunctionalInterface
    interface FileStep {
        void apply(Path temp, Path target) throws IOException;
    }

}
