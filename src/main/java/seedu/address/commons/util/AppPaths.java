package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.CodeSource;

/**
 * Locates application files beside the running JAR, independently of the launch directory.
 * Exploded classes used by IntelliJ and Gradle retain their configured working directory.
 * AI assistance: Gabriel used OpenAI Codex for the application paths and their regression tests.
 */
public final class AppPaths {

    private static final Path HOME_DIRECTORY = locateHomeDirectory();

    private AppPaths() {
    }

    /** Returns the absolute, normalized directory containing the running application. */
    public static Path getHomeDirectory() {
        return HOME_DIRECTORY;
    }

    /** Returns the client-data path inside the application's home folder. */
    public static Path getAddressBookFilePath() {
        return HOME_DIRECTORY.resolve("data").resolve("spotter.json");
    }

    /** Returns the window-preferences path inside the application's home folder. */
    public static Path getUserPrefsFilePath() {
        return HOME_DIRECTORY.resolve("preferences.json");
    }

    /**
     * Returns a FileHandler pattern whose directory is literal, including any percent signs.
     * FileHandler interprets percent escapes itself, so an ordinary path string is not sufficient.
     */
    public static String getLogFilePattern() {
        return HOME_DIRECTORY.resolve("spotter.log").toString().replace("%", "%%");
    }

    /**
     * Uses this class's code location so paths are available before the first logger is initialized.
     * An unknown location fails explicitly instead of silently writing into an unrelated folder.
     */
    private static Path locateHomeDirectory() {
        CodeSource source = AppPaths.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException("Cannot determine Spotter's application folder.");
        }
        return resolveHomeDirectory(source.getLocation(), Path.of(""));
    }

    /**
     * Resolves an archive's containing folder, or the working directory for an exploded class directory.
     * Converts through URI rather than URL text so spaces, Unicode and percent signs are decoded correctly.
     */
    static Path resolveHomeDirectory(URL codeLocation, Path workingDirectory) {
        requireNonNull(codeLocation);
        requireNonNull(workingDirectory);
        if (!"file".equalsIgnoreCase(codeLocation.getProtocol())) {
            throw new IllegalStateException("Spotter must run from a local JAR or class directory.");
        }
        try {
            Path location = Path.of(codeLocation.toURI()).toAbsolutePath().normalize();
            if (codeLocation.getPath().endsWith("/")) {
                return workingDirectory.toAbsolutePath().normalize();
            }
            return requireNonNull(location.getParent(), "The application archive must be inside a folder.");
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot resolve Spotter's application folder.", e);
        }
    }
}
