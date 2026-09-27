package seedu.address.ui;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final String REMARK = "Likes swimming";

    @BeforeAll
    public static void setUpJavaFx() {
        Platform.startup(() -> { });
    }

    @AfterAll
    public static void tearDownJavaFx() {
        Platform.exit();
    }

    @Test
    public void constructor_personWithRemark_displaysRemark() throws Exception {
        Person person = new PersonBuilder().withRemark(REMARK).build();
        FutureTask<String> getDisplayedRemark = new FutureTask<>(() -> {
            PersonCard personCard = new PersonCard(person, 1);
            Label remark = (Label) personCard.getRoot().lookup("#remark");
            return remark.getText();
        });

        Platform.runLater(getDisplayedRemark);

        assertEquals(REMARK, getDisplayedRemark.get(5, SECONDS));
    }
}
