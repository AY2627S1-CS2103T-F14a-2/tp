package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TrainingRecords.measurement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.MeasurementType;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks typed measurement commands against the live history and client-card controls.
 * AI assistance: Helen used OpenAI Codex for these integration tests for issue #199.
 * This also covered the related DG testing notes and proposed latest-measurement summary.
 */
@ExtendWith(ApplicationExtension.class)
public class MeasurementLiveHistoryTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 12);

    @TempDir
    public Path temporaryFolder;

    private Stage stage;
    private HBox content;
    private Model model;
    private Logic logic;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
    }

    @Test
    public void execute_replaceDeleteLatestAndDeleteLast_updatesOpenHistoryAndCard(FxRobot robot) throws Exception {
        JsonAddressBookStorage data = new JsonAddressBookStorage(temporaryFolder.resolve("clients.json"));
        openClient(robot, data);

        execute(robot, "measure 1 d/2026-09-12 t/weight v/71.8");
        assertDisplay(robot, "Measurements · 2 records",
                "2026-09-12  weight 71.8 kg\n2026-09-11  waist 81 cm", "weight 71.8 kg (2 records)");

        execute(robot, "deletemeasure 1 d/2026-09-12 t/weight");
        assertDisplay(robot, "Measurements · 1 record", "2026-09-11  waist 81 cm", "waist 81 cm (1 record)");

        execute(robot, "deletemeasure 1 d/2026-09-11 t/waist");
        assertDisplay(robot, "Measurements · 0 records", "None recorded yet.", "Latest measurement: -");
        assertEquals(model.getAddressBook(), data.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedReplacementAndDeletion_restoreOpenHistoryCardAndFile(FxRobot robot) throws Exception {
        JsonAddressBookStorage failing = new JsonAddressBookStorage(temporaryFolder.resolve("clients.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        openClient(robot, failing);
        JsonAddressBookStorage data = new JsonAddressBookStorage(failing.getAddressBookFilePath());
        data.saveAddressBook(model.getAddressBook());
        String saved = Files.readString(data.getAddressBookFilePath());

        for (String command : new String[] {"measure 1 d/2026-09-12 t/weight v/71.8",
            "deletemeasure 1 d/2026-09-12 t/weight"}) {
            robot.interact(() -> assertEquals(LogicManager.MESSAGE_SAVE_FAILURE,
                    assertThrows(CommandException.class, () -> logic.execute(command)).getMessage()));
            assertDisplay(robot, "Measurements · 2 records",
                    "2026-09-12  weight 72.4 kg\n2026-09-11  waist 81 cm", "weight 72.4 kg (2 records)");
            assertEquals(saved, Files.readString(data.getAddressBookFilePath()));
            assertEquals(model.getAddressBook(), data.readAddressBook().orElseThrow());
        }
    }

    private void openClient(FxRobot robot, JsonAddressBookStorage data) {
        model = new ModelManager();
        model.addPerson(new PersonBuilder().withMeasurements(measurement(DATE, MeasurementType.WEIGHT, "72.4"),
                measurement(DATE.minusDays(1), MeasurementType.WAIST, "81")).build());
        logic = new LogicManager(model, new StorageManager(data,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        robot.interact(() -> {
            content = new HBox(new PersonListPanel(logic.getFilteredPersonList()).getRoot(),
                    new ClientDetailPanel(logic.getViewedPerson()).getRoot());
            stage.setScene(new Scene(content, 900, 600));
            stage.show();
        });
        execute(robot, "view 1");
    }

    private void execute(FxRobot robot, String command) {
        robot.interact(() -> {
            try {
                logic.execute(command);
            } catch (Exception e) {
                throw new AssertionError("Command failed: " + command, e);
            }
        });
    }

    private void assertDisplay(FxRobot robot, String heading, String history, String cardSuffix) {
        robot.interact(() -> {
            assertEquals(heading, ((Label) content.lookup("#measurementHeading")).getText());
            assertEquals(history, ((Label) content.lookup("#measurements")).getText());
            String card = ((Label) content.lookup("#latestMeasurement")).getText();
            // Card dates include a year outside the current year; the value and count must still match.
            assertTrue(card.endsWith(cardSuffix), card);
        });
    }
}
