package com.boracompany.airplanes.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class AirplaneSwingAppE2E extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION_NAME = "test-collection";

    private static final String AIRPLANE_FIXTURE_1_ID = "1";
    private static final String AIRPLANE_FIXTURE_1_MODEL = "first airplane";
    private static final String AIRPLANE_FIXTURE_2_ID = "2";
    private static final String AIRPLANE_FIXTURE_2_MODEL = "second airplane";

    private MongoClient mongoClient;

    private FrameFixture window;

    @SuppressWarnings("deprecation")
    @Override
    protected void onSetUp() {
        String containerIpAddress = mongo.getContainerIpAddress();
        Integer mappedPort = mongo.getMappedPort(27017);
        mongoClient = new MongoClient(containerIpAddress, mappedPort);
        // always start with an empty database
        mongoClient.getDatabase(DB_NAME).drop();

        addTestAirplaneToDatabase(AIRPLANE_FIXTURE_1_ID, AIRPLANE_FIXTURE_1_MODEL);
        addTestAirplaneToDatabase(AIRPLANE_FIXTURE_2_ID, AIRPLANE_FIXTURE_2_MODEL);
        // start the Swing application
        application("com.boracompany.airplanes.app.swing.WarehouseSwingApp")
                .withArgs("--mongo-host=" + containerIpAddress, "--mongo-port=" + mappedPort.toString(),
                        "--db-name=" + DB_NAME, "--db-collection=" + COLLECTION_NAME)
                .start();
        // get a reference of its JFrame
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Airplane View".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot());
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testOnStartAllDatabaseElementsAreShown() {
        assertThat(window.list().contents())
                .anySatisfy(e -> assertThat(e).contains(AIRPLANE_FIXTURE_1_ID, AIRPLANE_FIXTURE_1_MODEL))
                .anySatisfy(e -> assertThat(e).contains(AIRPLANE_FIXTURE_2_ID, AIRPLANE_FIXTURE_2_MODEL));
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("idTextBox").enterText("15");
        window.textBox("modelTextBox").enterText("new airplane");
        window.button(JButtonMatcher.withText("Add")).click();
        assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains("15", "new airplane"));
    }

    @Test
    @GUITest
    public void testAddButtonError() {
        window.textBox("idTextBox").enterText(AIRPLANE_FIXTURE_1_ID);
        window.textBox("modelTextBox").enterText("new one");
        window.button(JButtonMatcher.withText("Add")).click();
        assertThat(window.label("errorLabel").text()).contains(AIRPLANE_FIXTURE_1_ID, AIRPLANE_FIXTURE_1_MODEL);
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        window.list("airplaneList").selectItem(Pattern.compile(".*" + AIRPLANE_FIXTURE_1_MODEL + ".*"));
        window.button(JButtonMatcher.withText("Delete")).click();
        assertThat(window.list().contents()).noneMatch(e -> e.contains(AIRPLANE_FIXTURE_1_MODEL));
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {

        window.list("airplaneList").selectItem(Pattern.compile(".*" + AIRPLANE_FIXTURE_1_MODEL + ".*"));

        removeTestAirplaneFromDatabase(AIRPLANE_FIXTURE_1_ID);

        window.button(JButtonMatcher.withText("Delete")).click();

        assertThat(window.label("errorLabel").text()).contains(AIRPLANE_FIXTURE_1_ID, AIRPLANE_FIXTURE_1_MODEL);
    }

    private void addTestAirplaneToDatabase(String id, String model) {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
                .insertOne(new Document().append("id", id).append("model", model));
    }

    private void removeTestAirplaneFromDatabase(String id) {
        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).deleteOne(Filters.eq("id", id));
    }

}
