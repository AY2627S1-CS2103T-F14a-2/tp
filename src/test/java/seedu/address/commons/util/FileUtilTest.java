package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    private static final String OLD_CONTENT = "{\"persons\":[\"old\"]}";
    private static final String NEW_CONTENT = "{\"persons\":[\"new\"]}";
    private static final IOException SIMULATED_FAILURE = new IOException("simulated disk full");

    @TempDir
    public Path tempDir;

    @Test
    public void readFromFile_utf8WithByteOrderMark_omitsByteOrderMark() throws IOException {
        Path file = tempDir.resolve("withBom.json");
        Files.write(file, "﻿{}".getBytes(StandardCharsets.UTF_8));
        assertEquals("{}", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_existingFile_replacesContentAndLeavesNoTempFile() throws IOException {
        Path file = writeOldFile();

        FileUtil.writeToFile(file, NEW_CONTENT);

        assertEquals(NEW_CONTENT, FileUtil.readFromFile(file));
        assertEquals(List.of(file), filesIn(tempDir));
    }

    @Test
    public void writeToFile_missingParentFolder_createsFolderAndFile() throws IOException {
        Path file = tempDir.resolve("data").resolve("spotter.json");

        FileUtil.writeToFile(file, NEW_CONTENT);

        assertEquals(NEW_CONTENT, FileUtil.readFromFile(file));
    }

    /**
     * Reproduces the original bug in principle: writing over the live file truncated it first, so a write that
     * failed partway, e.g. on a full disk, left a cut-off file in place of valid data.
     */
    @Test
    public void writeToFile_writeFailsPartway_oldFileUnchangedAndNoTempFile() throws IOException {
        Path file = writeOldFile();
        byte[] oldBytes = Files.readAllBytes(file);

        IOException thrown = assertThrows(IOException.class, () -> FileUtil.writeToFile(file, (temp, target) -> {
            Files.write(temp, NEW_CONTENT.substring(0, 5).getBytes(StandardCharsets.UTF_8));
            throw SIMULATED_FAILURE;
        }, (temp, target) -> {
            throw new AssertionError("must not move a partly written file");
        }));

        assertEquals(SIMULATED_FAILURE, thrown);
        assertArrayEquals(oldBytes, Files.readAllBytes(file));
        assertEquals(List.of(file), filesIn(tempDir));
    }

    @Test
    public void writeToFile_moveFails_oldFileUnchangedAndNoTempFile() throws IOException {
        Path file = writeOldFile();
        byte[] oldBytes = Files.readAllBytes(file);

        FileUtil.FileStep writeFully = (temp, target) -> Files.writeString(temp, NEW_CONTENT);
        FileUtil.FileStep failToMove = (temp, target) -> {
            throw SIMULATED_FAILURE;
        };

        assertThrows(IOException.class, () -> FileUtil.writeToFile(file, writeFully, failToMove));

        assertArrayEquals(oldBytes, Files.readAllBytes(file));
        assertEquals(List.of(file), filesIn(tempDir));
    }

    @Test
    public void writeToFile_readOnlyFile_refusesAndKeepsContent() throws IOException {
        Path file = writeOldFile();
        assertTrue(file.toFile().setWritable(false));
        try {
            assertThrows(AccessDeniedException.class, () -> FileUtil.writeToFile(file, NEW_CONTENT));
            assertEquals(OLD_CONTENT, FileUtil.readFromFile(file));
            assertEquals(List.of(file), filesIn(tempDir));
        } finally {
            file.toFile().setWritable(true);
        }
    }

    @Test
    public void writeToFile_temporaryFileName_isRecognisable() throws IOException {
        Path file = writeOldFile();
        Path[] tempFile = new Path[1];

        FileUtil.writeToFile(file, (temp, target) -> tempFile[0] = temp, (temp, target) -> { });

        String name = tempFile[0].getFileName().toString();
        assertEquals(tempDir.toAbsolutePath(), tempFile[0].getParent());
        assertTrue(name.startsWith("spotter-") && name.endsWith(".tmp"), name);
        assertFalse(Files.exists(tempFile[0]));
    }

    private Path writeOldFile() throws IOException {
        Path file = tempDir.resolve("spotter.json");
        Files.writeString(file, OLD_CONTENT);
        return file;
    }

    private static List<Path> filesIn(Path folder) throws IOException {
        try (Stream<Path> files = Files.list(folder)) {
            return files.toList();
        }
    }
}
