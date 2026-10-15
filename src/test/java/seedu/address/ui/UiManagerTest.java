package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;

public class UiManagerTest {

    @Test
    public void showMessage_beforeWindowExists_doesNothing() {
        UiManager uiManager = new UiManager(new LogicManager(new ModelManager(), null), Path.of("spotter.json"));
        assertDoesNotThrow(() -> uiManager.showMessage("Unable to load saved Spotter data."));
    }
}
