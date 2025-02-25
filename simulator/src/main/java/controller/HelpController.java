package controller;

/*
Contoller for help window


 */


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

    public class HelpController {

        @FXML
        private TextArea helpTextArea;

        @FXML
        private void initialize() {
            String instructionText = "Welcome to the program!\n\n"
                    + "1. Enter the simulation parameters in the left panel.\n"
                    + "2. Click 'Start' to begin the simulation.\n"
                    + "3. Use the 'Pause' and 'Stop' buttons to control the simulation.\n"
                    + "4. The results will be displayed in the right panel.\n\n"
                    + "To speed up or slow down the simulation, use the 'Speed ​​Up' and 'Slow Down' buttons.\n"
                    + "To change the delay, use the 'Delay' slider.\n\n"
                    + "If you have any questions, contact the developer.";
            helpTextArea.setText(instructionText);
        }

        @FXML
        private void handleCloseButton() {
            Stage stage = (Stage) helpTextArea.getScene().getWindow();
            stage.close();
        }
    }


