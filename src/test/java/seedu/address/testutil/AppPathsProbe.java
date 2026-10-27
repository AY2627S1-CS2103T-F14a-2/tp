package seedu.address.testutil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.AppPaths;

/** A small packaged entry point that exercises application paths and logging in a separate JVM. */
public final class AppPathsProbe {

    private AppPathsProbe() {
    }

    /** Writes only through the production path resolver and prints the data it found for the parent test. */
    public static void main(String[] args) throws Exception {
        Logger logger = LogsCenter.getLogger(AppPathsProbe.class);
        logger.info("Checking packaged application paths.");
        Path dataFile = AppPaths.getAddressBookFilePath();
        Files.createDirectories(dataFile.getParent());
        if (!Files.exists(dataFile)) {
            Files.writeString(dataFile, "probe-client");
        }
        Files.writeString(AppPaths.getUserPrefsFilePath(), "probe-preferences");
        System.out.println(Files.readString(dataFile));
        for (Handler handler : Logger.getLogger("ab3").getHandlers()) {
            handler.close();
        }
    }
}
