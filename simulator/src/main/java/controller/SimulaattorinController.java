package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.awt.Point;
import java.io.IOException;
import java.util.List;


/*

THAT SHOULD BE THE MAIN CONTOLLER

 */


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
    private Canvas workshopCanvas;

    private GraphicsContext gc;



    @FXML
    private void initialize() {
        if (showResultsButton != null) {
            showResultsButton.setOnAction(event -> handleShowResultsButton());
        }

        if (workshopCanvas != null) {
            gc = workshopCanvas.getGraphicsContext2D();
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());
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


    public void drawServicePoints() {
        if (gc != null) {
            gc.setFill(Color.BLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);

            double x = 50;
            double y = 100;
            double width = 100;
            double height = 50;

            for (int i = 0; i < 3; i++) {
                gc.fillRect(x, y, width, height);
                gc.strokeRect(x, y, width, height);
                x += 150;
            }
        }
    }

    public void drawQueues(int[] queueSizes) {
        if (gc != null) {
            gc.setFill(Color.RED);
            double x = 50;
            double y = 200;

            for (int i = 0; i < queueSizes.length; i++) {
                double queueHeight = queueSizes[i] * 10;
                gc.fillRect(x, y - queueHeight, 100, queueHeight);
                x += 150;
            }
        }
    }

    public void drawCustomers(List<Point> customerPositions) {
        if (gc != null) {
            gc.setFill(Color.GREEN);
            for (Point p : customerPositions) {
                gc.fillOval(p.x, p.y, 10, 10);
            }
        }
    }

}