package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path tempDir;

    @Test
    public void readFromFile_utf8WithByteOrderMark_omitsByteOrderMark() throws IOException {
        Path file = tempDir.resolve("withBom.json");
        Files.write(file, "\uFEFF{}".getBytes(StandardCharsets.UTF_8));
        assertEquals("{}", FileUtil.readFromFile(file));
    }
}
