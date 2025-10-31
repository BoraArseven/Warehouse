package com.boracompany.airplanes.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.boracompany.airplanes.controller.WarehouseController;
import com.boracompany.airplanes.model.Airplane;

@RunWith(GUITestRunner.class)
public class AirplaneSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private AirplaneSwingView airplaneSwingView;

    private AutoCloseable closeable;

    @Mock
    private WarehouseController warehouseController;

    @Override
    protected void onSetUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        GuiActionRunner.execute(() -> {
            airplaneSwingView = new AirplaneSwingView();
            airplaneSwingView.setWarehouseController(warehouseController);
            return airplaneSwingView;
        });
        window = new FrameFixture(robot(), airplaneSwingView);
        window.show(); // shows the frame to test

    }

    @Override
    protected void onTearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testStart() {
        GuiActionRunner.execute(() -> airplaneSwingView.start());
        verify(warehouseController).allAirplanes();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        window.label(JLabelMatcher.withText("id"));
        window.textBox("idTextBox").requireEnabled();
        window.label(JLabelMatcher.withText("model"));
        window.textBox("modelTextBox").requireEnabled();
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
        window.list("airplaneList");
        window.button(JButtonMatcher.withText("Delete")).requireDisabled();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("modelTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireEnabled();
    }

    @Test
    public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture idTextBox = window.textBox("idTextBox");
        JTextComponentFixture nameTextBox = window.textBox("modelTextBox");

        idTextBox.enterText("1");
        nameTextBox.enterText(" ");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();

        idTextBox.setText("");
        nameTextBox.setText("");

        idTextBox.enterText(" ");
        nameTextBox.enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
    }

    @Test
    public void testDeleteButtonShouldBeEnabledOnlyWhenAnAirplaneIsSelected() {
        GuiActionRunner.execute(() -> airplaneSwingView.getListAirplanesModel().addElement(new Airplane("1", "test")));
        window.list("airplaneList").selectItem(0);
        JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
        deleteButton.requireEnabled();
        window.list("airplaneList").clearSelection();
        deleteButton.requireDisabled();
    }

    @Test
    public void testsShowAllAirplanesShouldAddAirplaneDescriptionsToTheList() {
        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        GuiActionRunner.execute(() -> airplaneSwingView.showAllAirplanes(Arrays.asList(airplane1, airplane2)));
        String[] listContents = window.list().contents();
        assertThat(listContents).containsExactly("1 - test1", "2 - test2");
    }

    @Test
    public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
        Airplane airplane = new Airplane("1", "test1");
        GuiActionRunner.execute(() -> airplaneSwingView.showError("error message", airplane));
        window.label("errorLabel").requireText("error message: 1 - test1");
    }

    @Test
    public void testShowErrorAirplaneNotFound() {
        // setup
        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Airplane> listAirplanesModel = airplaneSwingView.getListAirplanesModel();
            listAirplanesModel.addElement(airplane1);
            listAirplanesModel.addElement(airplane2);
        });
        GuiActionRunner.execute(() -> airplaneSwingView.showErrorAirplaneNotFound("error message", airplane1));
        window.label("errorLabel").requireText("error message: 1 - test1");
        assertThat(window.list().contents()).containsExactly("2 - test2");
    }

    @Test
    public void testAirplaneAddedShouldAddTheAirplaneToTheListAndResetTheErrorLabel() {
        GuiActionRunner.execute(() -> airplaneSwingView.airplaneAdded(new Airplane("1", "test1")));
        String[] listContents = window.list().contents();
        assertThat(listContents).containsExactly("1 - test1");
        window.label("errorLabel").requireText(" ");
    }

    @Test
    public void testAirplaneRemovedShouldRemoveTheAirplaneFromTheListAndResetTheErrorLabel() {
        // setup
        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Airplane> listStudentsModel = airplaneSwingView.getListAirplanesModel();
            listStudentsModel.addElement(airplane1);
            listStudentsModel.addElement(airplane2);
        });
        // execute
        GuiActionRunner.execute(() -> airplaneSwingView.airplaneRemoved(new Airplane("1", "test1")));
        // verify
        String[] listContents = window.list().contents();
        assertThat(listContents).containsExactly("2 - test2");
        window.label("errorLabel").requireText(" ");
    }

    @Test
    public void testAddButtonShouldDelegateToSchoolControllerNewStudent() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("modelTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        verify(warehouseController).newAirplane(new Airplane("1", "test"));
    }

    @Test
    public void testDeleteButtonShouldDelegateToSchoolControllerDeleteStudent() {
        Airplane airplane1 = new Airplane("1", "test1");
        Airplane airplane2 = new Airplane("2", "test2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Airplane> listStudentsModel = airplaneSwingView.getListAirplanesModel();
            listStudentsModel.addElement(airplane1);
            listStudentsModel.addElement(airplane2);
        });
        window.list("airplaneList").selectItem(1);
        window.button(JButtonMatcher.withText("Delete")).click();
        verify(warehouseController).deleteAirplane(airplane2);
    }
}
