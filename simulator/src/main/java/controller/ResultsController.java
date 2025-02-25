package controller;


/*
Contoller for RESULTS  window


 */



import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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

    public void setResults(double totalEarnings, int servedRegularCars, int servedElectricCars, int rejectedCustomers) {
        totalEarningsLabel.setText(String.format("%.2f $", totalEarnings));
        servedRegularCarsLabel.setText(String.valueOf(servedRegularCars));
        servedElectricCarsLabel.setText(String.valueOf(servedElectricCars));
        rejectedCustomersLabel.setText(String.valueOf(rejectedCustomers));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCloseButton() {
        if (stage != null) {
            stage.close();
        }
    }
}