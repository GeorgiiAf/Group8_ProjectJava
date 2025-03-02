package controller;

//  REWORK  THIS



import javafx.animation.AnimationTimer;
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
import simu.framework.Trace;
import simu.framework.Trace.Level;
import simu.model.OmaMoottori;
import simu.model.Palvelupiste;


import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
THAT SHOULD BE THE MAIN CONTROLLER


REWORK
*/

public class SimulaattorinController {

    public Label inputErrorLabel;
    private IKontrolleriForV kontrolleri;
    private OmaMoottori moottori;
    private boolean simulationRunning = false;
    private Thread simulationThread;
    private AnimationTimer animationTimer;

    @FXML private TextField aika;
    @FXML private TextField viive;
    @FXML private Label tulos;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;


    @FXML private Canvas naytto;
    @FXML private Button showResultsButton;
    @FXML private Button helpButton;
    @FXML private Canvas workshopCanvas;

    private GraphicsContext gc;

    @FXML
    private void initialize() {
        if (showResultsButton != null) {
            showResultsButton.setOnAction(event -> handleShowResultsButton());
        }

        if (helpButton != null) {
            helpButton.setOnAction(event -> handleHelpButton());
        }

        if (startButton != null) {
            startButton.setOnAction(event -> handleStartButton());
        }

        if (stopButton != null) {
            stopButton.setOnAction(event -> handleStopButton());
        }

        if (workshopCanvas != null) {
            gc = workshopCanvas.getGraphicsContext2D();
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());
            drawServicePoints();
        }

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println("Updating visualization...");
                updateVisualisation();
            }
        };
    }

    public void setKontrolleri(IKontrolleriForV kontrolleri) {
        this.kontrolleri = kontrolleri;
    }

    public double getAika() {
        try {
            return Double.parseDouble(aika.getText());
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid simulation time");
            return 0;
        }
    }

    public long getViive() {
        try {
            return Long.parseLong(viive.getText());
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid delay value");
            return 0;
        }
    }

    public void setLoppuaika(double aika) {
        tulos.setText(String.format("%.2f", aika));
    }

    public void tyhjennaVisualisointi() {
        if (naytto != null) {
            naytto.getGraphicsContext2D().clearRect(0, 0, naytto.getWidth(), naytto.getHeight());
        }
    }

    public void uusiAsiakas() {
        updateVisualisation();
    }

    private void showErrorMessage(String message) {
        if (inputErrorLabel != null) {
            inputErrorLabel.setText(message);
            inputErrorLabel.setVisible(true);
        } else {
            System.err.println(message);
        }
    }

    @FXML
    private void handleStartButton() {
        double simulationTime = getAika();
        long delay = getViive();
        System.out.println("Start button clicked"); // Отладочный вывод


        if (simulationTime <= 0 || delay < 0) {
            showErrorMessage("Please enter valid values");
            return;
        }

        if (simulationRunning) {
            showErrorMessage("Simulation is already running");
            return;
        }

        if (inputErrorLabel != null) {
            inputErrorLabel.setVisible(false);
        }

        Trace.setTraceLevel(Level.INFO);

        simulationThread = new Thread(() -> {
            try {
                simulationRunning = true;
                moottori.setSimulointiaika(simulationTime);
                moottori.setViive(delay);
                moottori.aja();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                simulationRunning = false;

                javafx.application.Platform.runLater(() -> {
                    setLoppuaika(moottori.getKello().getAika());
                    if (startButton != null) startButton.setDisable(false);
                    if (stopButton != null) stopButton.setDisable(true);
                    animationTimer.stop();
                });
            }
        });


        if (startButton != null) startButton.setDisable(true);
        if (stopButton != null) stopButton.setDisable(false);

        animationTimer.start();
        simulationThread.start();
    }

    public void setMoottori(OmaMoottori moottori) {
        this.moottori = moottori;
    }

    @FXML
    private void handleStopButton() {
        if (!simulationRunning || moottori == null) {
            return;
        }

        moottori.setSimulointiLoppu(true);

        try {
            simulationThread.join(1000); // Даем потоку 1 секунду на завершение
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (startButton != null) startButton.setDisable(false);
        if (stopButton != null) stopButton.setDisable(true);

        animationTimer.stop();
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


            if (moottori != null) {
                updateResultsFromMoottori();
            }

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




    private void updateResultsFromMoottori() {
        if (moottori == null || kontrolleri == null) {
            return;
        }

        kontrolleri.setTotalEarnings(moottori.calculateTotalEarnings());
        kontrolleri.setServedRegularCars(moottori.getServedRegularCars());
        kontrolleri.setServedElectricCars(moottori.getServedElectricCars());
        kontrolleri.setRejectedCustomers(moottori.getRejectedCustomers());
    }

    public void drawServicePoints() {
        if (gc != null) {
            gc.clearRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());

            gc.setFill(Color.BLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);

            double x = 50;
            double y = 50;
            double width = 100;
            double height = 50;

            gc.setFill(Color.LIGHTSKYBLUE);
            gc.fillRect(x, y, width, height);
            gc.strokeRect(x, y, width, height);
            gc.setFill(Color.BLACK);
            gc.fillText("Arrival", x + 30, y + 30);

            y += 100;
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(x, y, width, height);
            gc.strokeRect(x, y, width, height);
            gc.setFill(Color.BLACK);
            gc.fillText("Diagnostics", x + 20, y + 30);

            y += 100;
            gc.setFill(Color.LIGHTYELLOW);
            gc.fillRect(x, y, width, height);
            gc.strokeRect(x, y, width, height);
            gc.setFill(Color.BLACK);
            gc.fillText("Parts", x + 30, y + 30);

            y += 100;
            gc.setFill(Color.LIGHTCORAL);
            gc.fillRect(x, y, width, height);
            gc.strokeRect(x, y, width, height);
            gc.setFill(Color.BLACK);
            gc.fillText("Car Ready", x + 20, y + 30);
        }
    }

    public void updateVisualisation() {
        if (gc == null || moottori == null) {
            return;
        }

        drawServicePoints();

        ArrayList<ArrayList<Palvelupiste>> allServicePoints = moottori.getAllServicePointsList();
        if (allServicePoints == null || allServicePoints.isEmpty()) {
            return;
        }

        double x = 160;
        double baseY = 75;

        for (int i = 0; i < allServicePoints.size(); i++) {
            ArrayList<Palvelupiste> servicePoints = allServicePoints.get(i);
            Color queueColor;

            switch (i) {
                case 0: queueColor = Color.SKYBLUE; break;      // Arrival
                case 1: queueColor = Color.LIGHTGREEN; break;   // Diagnostics
                case 2: queueColor = Color.YELLOW; break;       // Parts
                case 3: queueColor = Color.CORAL; break;        // Car Ready
                default: queueColor = Color.GRAY;
            }

            double y = baseY + i * 100;

            for (int j = 0; j < servicePoints.size(); j++) {
                Palvelupiste servicePoint = servicePoints.get(j);
                int queueSize = servicePoint.getQueueSize();

                gc.setFill(queueColor);
                for (int k = 0; k < queueSize; k++) {
                    gc.fillOval(x + k * 15, y, 10, 10);
                }

                gc.setFill(Color.BLACK);
                gc.fillText("Q: " + queueSize, x + 180, y + 10);
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