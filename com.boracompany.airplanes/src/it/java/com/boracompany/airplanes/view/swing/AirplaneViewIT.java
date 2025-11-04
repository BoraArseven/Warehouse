package com.boracompany.airplanes.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.guice.AirplaneSwingMongoDefaultModule;
import com.boracompany.airplanes.model.Airplane;
import com.boracompany.airplanes.repository.mongo.AirplaneMongoRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class AirplaneViewIT extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    @Inject
    private static MongoClient mongoClient;

    @Inject
    private AirplaneSwingView airplaneSwingView;

    @Inject
    private AirplaneMongoRepository airplaneRepository;

    // this will be retrieved from the view
    private WarehouseController warehouseController;

    private FrameFixture window;

    @SuppressWarnings("deprecation")
    @Override
    protected void onSetUp() {

        mongoClient = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
        final Module moduleForTesting = Modules.override(new AirplaneSwingMongoDefaultModule())
                .with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(MongoClient.class).toInstance(mongoClient);
                    }
                });

        final Injector injector = Guice.createInjector(moduleForTesting);

        GuiActionRunner.execute(() -> {
            injector.injectMembers(this);
            // explicit empty the database through the repository
            for (Airplane airplane : airplaneRepository.findAll()) {
                airplaneRepository.delete(airplane.getId());
            }
            warehouseController = airplaneSwingView.getWarehouseController();
            return airplaneSwingView;
        });
        window = new FrameFixture(robot(), airplaneSwingView);
        window.show(); // shows the frame to test
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testAllAirplanes() {

        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        airplaneRepository.save(airplane1);
        airplaneRepository.save(airplane2);
        GuiActionRunner.execute(() -> warehouseController.allAirplanes());
        // and verify that the view's list is populated
        assertThat(window.list().contents()).containsExactly("1 - test1", "2 - test2");
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("modelTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        assertThat(window.list().contents()).containsExactly("1 - test");
    }

    @Test
    @GUITest
    public void testAddButtonError() {
        airplaneRepository.save(new Airplane("1", "existing"));
        window.textBox("idTextBox").enterText("1");
        window.textBox("modelTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        assertThat(window.list().contents()).isEmpty();
        window.label("errorLabel").requireText("Already existing airplane with id 1: 1 - existing");
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        GuiActionRunner.execute(() -> warehouseController.newAirplane(new Airplane("1", "toremove")));
        window.list().selectItem(0);
        window.button(JButtonMatcher.withText("Delete")).click();
        assertThat(window.list().contents()).isEmpty();
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        Airplane airplane = new Airplane("1", "non existent");
        GuiActionRunner.execute(() -> airplaneSwingView.getListAirplanesModel().addElement(airplane));
        window.list().selectItem(0);
        window.button(JButtonMatcher.withText("Delete")).click();
        assertThat(window.list().contents()).isEmpty();
        window.label("errorLabel").requireText("No existing airplane with id 1: 1 - non existent");
    }
}
