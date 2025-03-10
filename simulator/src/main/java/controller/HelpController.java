package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller for the help window.
 */
public class HelpController {

    @FXML
    private TextArea helpTextArea;

    /**
     * Initializes the help window with instructions.
     */
    @FXML
    private void initialize() {
        String instructionText = """
                Welcome to the program!

                1. Enter the simulation parameters in the left panel.
                2. Click 'Start' to begin the simulation.
                3. Use the 'Pause' and 'Stop' buttons to control the simulation.
                4. The results will be displayed in the right panel.

                To speed up or slow down the simulation, use the 'Speed ​​Up' and 'Slow Down' buttons.
                To change the delay, use the 'Delay' slider.

                If you have any questions, contact the developer.""";
        helpTextArea.setText(instructionText);
    }

    /**
     * Handles the close button action to close the help window.
     */
    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) helpTextArea.getScene().getWindow();
        stage.close();
    }
}