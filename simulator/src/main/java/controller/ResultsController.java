package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the results window.
 */
public class ResultsController {

    @FXML
    private Label totalEarningsLabel;

    @FXML
    private Label servedRegularCarsLabel;

    @FXML
    private Label servedElectricCarsLabel;

    @FXML
    private Label rejectedCustomersLabel;

    private Stage stage;

    /**
     * Sets the results to be displayed in the results window.
     *
     * @param totalEarnings the total earnings from the simulation
     * @param servedRegularCars the number of served regular cars
     * @param servedElectricCars the number of served electric cars
     * @param rejectedCustomers the number of rejected customers
     */
    public void setResults(double totalEarnings, int servedRegularCars, int servedElectricCars, int rejectedCustomers) {
        totalEarningsLabel.setText(String.format("%.2f $", totalEarnings));
        servedRegularCarsLabel.setText(String.valueOf(servedRegularCars));
        servedElectricCarsLabel.setText(String.valueOf(servedElectricCars));
        rejectedCustomersLabel.setText(String.valueOf(rejectedCustomers));
    }

    /**
     * Sets the stage for the results window.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the close button action to close the results window.
     */
    @FXML
    private void handleCloseButton() {
        if (stage != null) {
            stage.close();
        }
    }
}