package com.boracompany.airplanes.view.swing;

import static org.mockito.Mockito.verify;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.boracompany.airplanes.controller.WarehouseController;

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

}
