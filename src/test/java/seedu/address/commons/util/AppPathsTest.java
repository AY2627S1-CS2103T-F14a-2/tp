package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
import seedu.address.testutil.AppPathsProbe;

/** Checks development paths and real archive launches independently of the current working directory. */
public class AppPathsTest {

    @TempDir
    public Path temporaryFolder;

    private int probeNumber;

    @Test
    public void resolveCodeSource_missingMetadata_failsInsteadOfUsingWorkingDirectory() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                AppPaths.resolveCodeSource(null, temporaryFolder));

        assertEquals("Cannot determine Spotter's application folder.", error.getMessage());
    }

    @Test
    public void resolveHomeDirectory_archiveWithEncodedCharacters_usesContainingFolder() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("Spotter space Ω 100%"));
        Path archive = Files.createFile(home.resolve("renamed application.jar"));
        Path other = Files.createDirectory(temporaryFolder.resolve("other"));

        assertEquals(home, AppPaths.resolveHomeDirectory(archive.toUri().toURL(), other));
    }

    @Test
    public void resolveHomeDirectory_explodedClasses_keepsNormalizedWorkingDirectory() throws Exception {
        Path classes = Files.createDirectory(temporaryFolder.resolve("classes"));
        Path workingDirectory = temporaryFolder.resolve("project").resolve("nested").resolve("..");

        assertEquals(temporaryFolder.resolve("project"),
                AppPaths.resolveHomeDirectory(classes.toUri().toURL(), workingDirectory));
    }

    @Test
    public void resolveHomeDirectory_nonLocalLocation_failsInsteadOfUsingWorkingDirectory() throws Exception {
        assertThrows(IllegalStateException.class, () -> AppPaths.resolveHomeDirectory(
                URI.create("https://example.com/spotter.jar").toURL(), temporaryFolder));
    }

    @Test
    public void resolveHomeDirectory_fileLocationWithQuery_failsInsteadOfUsingWorkingDirectory() throws Exception {
        URI invalidLocation = URI.create(temporaryFolder.resolve("spotter.jar").toUri() + "?unexpected=query");

        assertThrows(IllegalStateException.class, () ->
                AppPaths.resolveHomeDirectory(invalidLocation.toURL(), temporaryFolder));
    }

    @Test
    public void resolveHomeDirectory_nullArguments_rejected() throws Exception {
        assertThrows(NullPointerException.class, () -> AppPaths.resolveHomeDirectory(null, temporaryFolder));
        assertThrows(NullPointerException.class, () ->
                AppPaths.resolveHomeDirectory(temporaryFolder.toUri().toURL(), null));
    }

    @Test
    public void developmentPaths_areAbsoluteAndShareWorkingDirectory() {
        Path home = Path.of("").toAbsolutePath().normalize();

        assertEquals(home, AppPaths.getHomeDirectory());
        assertEquals(home.resolve("data").resolve("spotter.json"), AppPaths.getAddressBookFilePath());
        assertEquals(home.resolve("preferences.json"), AppPaths.getUserPrefsFilePath());
        assertEquals(home.resolve("spotter.log").toString().replace("%", "%%"), AppPaths.getLogFilePattern());
    }

    @Test
    public void packagedLaunch_foreignDirectory_keepsDataPreferencesAndLogsBesideJar() throws Exception {
        // Unicode URI decoding is covered above; subprocess arguments also depend on the native launcher encoding.
        Path home = Files.createDirectory(temporaryFolder.resolve("Spotter space 100%t"));
        Path archive = createProbeJar(home.resolve("renamed spotter.jar"));
        Path launchFolder = Files.createDirectory(temporaryFolder.resolve("launch elsewhere"));
        Path foreignData = launchFolder.resolve("data").resolve("spotter.json");
        Files.createDirectories(foreignData.getParent());
        Files.writeString(foreignData, "unrelated-client");
        Files.writeString(launchFolder.resolve("preferences.json"), "unrelated-preferences");

        assertTrue(runProbe(archive, launchFolder).contains("probe-client"));

        assertEquals("probe-client", Files.readString(home.resolve("data").resolve("spotter.json")));
        assertEquals("probe-preferences", Files.readString(home.resolve("preferences.json")));
        assertTrue(Files.exists(home.resolve("spotter.log.0")));
        assertEquals("unrelated-client", Files.readString(foreignData));
        assertEquals("unrelated-preferences", Files.readString(launchFolder.resolve("preferences.json")));
        assertFalse(Files.exists(launchFolder.resolve("spotter.log.0")));
    }

    @Test
    public void packagedLaunch_differentLaunchFolders_reloadSameData() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("application"));
        Path archive = createProbeJar(home.resolve("spotter.jar"));
        Path first = Files.createDirectory(temporaryFolder.resolve("first"));
        Path second = Files.createDirectory(temporaryFolder.resolve("second"));
        runProbe(archive, first);
        Files.writeString(home.resolve("data").resolve("spotter.json"), "saved-client");

        assertTrue(runProbe(archive, second).contains("saved-client"));
        try (Stream<Path> firstFiles = Files.list(first); Stream<Path> secondFiles = Files.list(second)) {
            assertEquals(List.of(), firstFiles.toList());
            assertEquals(List.of(), secondFiles.toList());
        }
    }

    @Test
    public void packagedLaunch_blockedLogFile_usesConsoleAndStillStarts() throws Exception {
        Path home = Files.createDirectory(temporaryFolder.resolve("blocked log"));
        Path archive = createProbeJar(home.resolve("spotter.jar"));
        Files.createDirectory(home.resolve("spotter.log.0"));
        Path launchFolder = Files.createDirectory(temporaryFolder.resolve("other"));

        String output = runProbe(archive, launchFolder);

        assertTrue(output.contains("using console logging only."));
        assertFalse(output.contains("\tat "), "A recoverable logging failure must not print a stack trace.");
        assertTrue(output.contains("probe-client"));
        assertTrue(Files.exists(home.resolve("preferences.json")));
        assertFalse(Files.exists(launchFolder.resolve("spotter.log.0")));
    }

    /** Packages the actual resolver and logging classes so the test exercises a real archive CodeSource. */
    private Path createProbeJar(Path archive) throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, AppPathsProbe.class.getName());
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(archive), manifest)) {
            for (Class<?> type : new Class<?>[] { AppPaths.class, LogsCenter.class, AppPathsProbe.class }) {
                String resource = type.getName().replace('.', '/') + ".class";
                jar.putNextEntry(new JarEntry(resource));
                try (InputStream bytes = type.getClassLoader().getResourceAsStream(resource)) {
                    if (bytes == null) {
                        throw new IllegalStateException("Missing probe class: " + resource);
                    }
                    bytes.transferTo(jar);
                }
                jar.closeEntry();
            }
        }
        return archive;
    }

    /** Launches the probe from a separate directory, with a deadline and captured output for diagnosis. */
    private String runProbe(Path archive, Path launchFolder) throws Exception {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path output = temporaryFolder.resolve("probe-" + probeNumber++ + ".txt");
        List<String> command = new ArrayList<>(List.of(java.toString()));
        // Include the real packaged-JVM checks in the same coverage report as the parent tests.
        ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .map(AppPathsTest::resolveCoverageDestination)
                .findFirst().ifPresent(command::add);
        command.addAll(List.of("-jar", archive.toString()));
        Process process = new ProcessBuilder(command)
                .directory(launchFolder.toFile()).redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Packaged paths probe timed out.");
            String text = Files.readString(output);
            assertEquals(0, process.exitValue(), text);
            return text;
        } finally {
            process.destroyForcibly();
        }
    }

    /** Keeps Gradle's relative coverage destination in the project when the child changes directory. */
    private static String resolveCoverageDestination(String argument) {
        return Pattern.compile("destfile=([^,]+)").matcher(argument).replaceFirst(match ->
                Matcher.quoteReplacement("destfile=" + Path.of(match.group(1)).toAbsolutePath().normalize()));
    }
}
