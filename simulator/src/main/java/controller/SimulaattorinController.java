package controller;



/*

THAT SHOULD BE THE MAIN CONTOLLER

 */


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulaattorinController {

    public Label inputErrorLabel;
    private IKontrolleriForV kontrolleri;

    @FXML private TextField aika;
    @FXML private TextField viive;
    @FXML private Label tulos;

    @FXML private Canvas naytto;
    @FXML
    private Button showResultsButton;
    @FXML  private Button helpButton;



    @FXML
    private void initialize() {
        if (showResultsButton != null) {
            showResultsButton.setOnAction(event -> handleShowResultsButton());
        }
    }


    public void setKontrolleri(IKontrolleriForV kontrolleri) {
        this.kontrolleri = kontrolleri;
    }

    public double getAika() {
        return Double.parseDouble(aika.getText());
    }

    public long getViive() {
        return Long.parseLong(viive.getText());
    }

    public void setLoppuaika(double aika) {
        tulos.setText(String.format("%.2f", aika));
    }

    public void tyhjennaVisualisointi() {
        naytto.getGraphicsContext2D().clearRect(0, 0, naytto.getWidth(), naytto.getHeight());
    }

    public void uusiAsiakas() {
    }


    @FXML
    private void handleHelpButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Instruction");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleShowResultsButton() {
        if (kontrolleri == null) {
            System.err.println("Kontrolleri is not initialized!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/results.fxml"));
            VBox root = loader.load();

            ResultsController resultsController = loader.getController();


            resultsController.setResults(
                    kontrolleri.getTotalEarnings(),
                    kontrolleri.getServedRegularCars(),
                    kontrolleri.getServedElectricCars(),
                    kontrolleri.getRejectedCustomers()
            );

            Stage resultsStage = new Stage();
            resultsStage.setTitle("Simulation Results");
            resultsStage.setScene(new Scene(root));
            resultsStage.setResizable(false);

            resultsController.setStage(resultsStage);

            resultsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}