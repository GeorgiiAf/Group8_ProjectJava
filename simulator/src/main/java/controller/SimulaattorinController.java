package controller;

//  REWORK  THIS

import java.awt.Point;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import simu.model.OmaMoottori;
import simu.model.Palvelupiste;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
THAT SHOULD BE THE MAIN CONTROLLER
REWORK
*/

public class SimulaattorinController {

    public Label inputErrorLabel;
    public Label earnedMoneyLabel;
    public Label servedRegularCarsLabel;
    public Label servedElectricCarsLabel;
    public Button slowDownButton;
    public Button speedUpButton;
    public Slider partsWaitingTimeSlider;
    public Slider arrivalTimeSlider5;
    public Slider arrivalTimeSlider6;
    private IKontrolleriForV kontrolleri;
    private OmaMoottori moottori;
    private boolean simulationRunning = false;
    private Thread simulationThread;
    private AnimationTimer animationTimer;
    private boolean paused = false;

    // Maps for service point and customer configurations
    private final Map<String, Integer> servicePointsMap = new LinkedHashMap<>();
    private final Map<String, Double> customerTypesMap = new LinkedHashMap<>();
    private final Map<String, Integer> maxSPoints = new LinkedHashMap<>();

//    private AnimationTimer animationTimer;
    private Map<String, Point2D> servicePointPositions = new LinkedHashMap<>();

    @FXML private TextField aika;
    @FXML private TextField viive;
    @FXML private Label tulos;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private Canvas carRepairCanvas; // Canvas for drawing service points

    @FXML private Spinner<Integer> arrivalSpots;
    @FXML private Spinner<Integer> diagnosticsSpots;
    @FXML private Spinner<Integer> partsSpots;
    @FXML private Spinner<Integer> maintenanceSpots;
    @FXML private Spinner<Integer> carReadySpots;

    @FXML private TextField regularCarServiceCost;
    @FXML private TextField electricCarServiceCost;
    @FXML private TextField partsCost;

    @FXML private Canvas naytto;
    @FXML private Button showResultsButton;
    @FXML private Button helpButton;
    @FXML private Canvas workshopCanvas;
    @FXML private ListView<TextFlow> logTextArea;

    @FXML private Slider arrivalTimeSlider;
    @FXML private Slider diagnosticsTimeSlider;
    @FXML private Slider partsTimeSlider;
    @FXML private Slider maintenanceTimeSlider;
    @FXML private Slider carReadyTimeSlider;

    private GraphicsContext gc;

    public SimulaattorinController(Canvas workshopCanvas) {
        this.workshopCanvas = workshopCanvas;
    }

    @FXML
    private void initialize() {
        if (showResultsButton != null) {
            showResultsButton.setOnAction(_ -> handleShowResultsButton());
        }

        if (helpButton != null) {
            helpButton.setOnAction(_ -> handleHelpButton());
        }

        if (startButton != null) {
            startButton.setOnAction(_ -> handleStartButton());
        }

        if (stopButton != null) {
            stopButton.setOnAction(_ -> handleStopButton());
        }

        if (speedUpButton != null) {
            speedUpButton.setOnAction(_ -> handleSpeedUpButton());
        }
        if (slowDownButton != null) {
            slowDownButton.setOnAction(_ -> handleSlowDownButton());
        }

        if (workshopCanvas != null) {
            gc = workshopCanvas.getGraphicsContext2D();
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());
            drawServicePointsOnWorkshopCanvas();
        }

        //   maxSPoints.put("CarArrives", 10);
        //   maxSPoints.put("Diagnostics", 10);
        //  maxSPoints.put("Parts", 10);
        //  maxSPoints.put("CarReady", 10);
        // maxSPoints.put("PartsOrdered", 15);
        // maxSPoints.put("SimpleMaintenance", 30);
        // maxSPoints.put("CarReady", 25);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
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
        System.out.println("Start button clicked");


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

        if (moottori == null) {
            moottori = new OmaMoottori();
        }


        Trace.setTraceLevel(Level.INFO);

        simulationThread = new Thread(() -> {

            int arrivalTime = (int) arrivalTimeSlider.getValue();
            int diagnosticsTime = (int) diagnosticsTimeSlider.getValue();
            int partsTime = (int) partsTimeSlider.getValue();
            int maintenanceTime = (int) maintenanceTimeSlider.getValue();
            int carReadyTime = (int) carReadyTimeSlider.getValue();

            moottori.setAllServiceTime(
                    arrivalTime,
                    diagnosticsTime,
                    partsTime,
                    maintenanceTime,
                    carReadyTime
            );

            try {
                simulationRunning = true;
                moottori.setSimulointiaika(simulationTime);
                moottori.setViive(delay);
                moottori.setSpotValues(
                        Integer.parseInt(String.valueOf(arrivalSpots)),
                        Integer.parseInt(String.valueOf(diagnosticsSpots.getValue())),
                        Integer.parseInt(String.valueOf(partsSpots.getValue())),
                        Integer.parseInt(String.valueOf(maintenanceSpots.getValue())),
                        Integer.parseInt(String.valueOf(carReadySpots.getValue()))
                );
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

        pauseButton.setOnAction(event -> pause(moottori));

        animationTimer.start();
        simulationThread.start();
    }


    @FXML
    private void handleStopButton() {
        if (!simulationRunning || moottori == null) {
            return;
        }

        moottori.setSimulointiLoppu(true);

        try {
            simulationThread.join(1000);
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

    public void drawServicePointsOnWorkshopCanvas() {
        if (gc != null) {
            gc.clearRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());

            double startX = 50;
            double startY = 100;
            double columnWidth = 100;
            double columnHeight = 200;
            double circleDiameter = 20;
            double spacing = 10;

            for (Map.Entry<String, Integer> entry : servicePointsMap.entrySet()) {
                String pointType = entry.getKey();
                int queueSize = entry.getValue();

                gc.setStroke(Color.BLACK);
                gc.strokeRect(startX, startY, columnWidth, columnHeight);

                double circleY = startY + spacing;
                for (int i = 0; i < queueSize; i++) {
                    double circleX = startX + (columnWidth - circleDiameter) / 2;
                    gc.setFill(Color.BLUE);
                    gc.fillOval(circleX, circleY, circleDiameter, circleDiameter);
                    circleY += circleDiameter + spacing;
                }

                gc.setFill(Color.BLACK);
                gc.fillText(pointType, startX + 10, startY + columnHeight + 20);

                startX += columnWidth + 50;
            }
        }
    }

    public void updateVisualisation() {
        if (gc == null || moottori == null) {
            return;
        }
        gc.clearRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());

        ArrayList<ArrayList<Palvelupiste>> allServicePoints = moottori.getAllServicePointsList();
        if (allServicePoints == null || allServicePoints.isEmpty()) {
            return;
        }

        servicePointsMap.clear();
        for (int i = 0; i < allServicePoints.size(); i++) {
            String pointType = "ServicePoint" + (i + 1);
            int queueSize = allServicePoints.get(i).size();
            servicePointsMap.put(pointType, queueSize);
        }

        drawServicePointsOnWorkshopCanvas();
    }

    public void drawQueues(int[] queueSizes) {
        if (gc != null) {
            gc.setFill(Color.ORANGE);
            double x = 50;
            double y = 200;

            for (int queueSize : queueSizes) {
                double queueHeight = queueSize * 5;
                gc.fillRect(x, y - queueHeight, 50, queueHeight);
                x += 100;
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


    @FXML
    private void handleSpeedUpButton() {
        if (moottori != null) {
            long currentDelay = moottori.getViive();
            if (currentDelay > 100) {
                moottori.setViive(currentDelay - 100);
            }
        }
    }

    @FXML
    private void handleSlowDownButton() {
        if (moottori != null) {
            long currentDelay = moottori.getViive();
            moottori.setViive(currentDelay + 100);
        }
    }

    private void drawTypeLabel(GraphicsContext gc, String pointType, double y) {
        double xLeftEdge = 5;
        gc.setFill(Color.BLACK);
        gc.fillText(pointType, xLeftEdge, y-15);
    }


    public void drawAllServicePointsOnCarRepairCanvas() {
        GraphicsContext gc = carRepairCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, carRepairCanvas.getWidth(), carRepairCanvas.getHeight());

        double yStep = 75;
        int typeIndex = 0;

        for (Map.Entry<String, Integer> entry : maxSPoints.entrySet()) {
            String pointType = entry.getKey();
            int totalPoints = 30;
            int activatedPoints = 10;
            double y = yStep * (typeIndex + 1);

            if (pointType.equals("CarArrives") && typeIndex > 0) {
                System.out.println("Drawing");
                y += 2;
            } else if (pointType.equals("DiagnosticsDone") && typeIndex > 0) {
                y += 85;
            } else if (typeIndex > 0) {
                y += yStep;
            }

            drawServicePointsOnCarRepairCanvas(gc, pointType, activatedPoints, totalPoints, y);
            drawTypeLabel(gc, pointType, y - 15);

            typeIndex++;
        }
    }

    public void drawServicePointsOnCarRepairCanvas(GraphicsContext gc, String pointType, int activatedCount, int totalPoints, double yStart) {
        double circleDiameter = 15.0; // Diameter of the circle
        double spacingX = 28.0;
        double spacingY = 30.0;
        int pointsPerRow = 20;

        int currentRow = 0;
        for (int i = 0; i < totalPoints; i++) {
            int rowPosition = i % pointsPerRow;
            if (i > 0 && rowPosition == 0) {
                currentRow++;
            }

            double x = spacingX * (rowPosition + 1);
            double yOffset = yStart + spacingY * currentRow;

            if (i < activatedCount) {
                gc.setFill(Color.web("#A0B8F7"));
            } else {
                gc.setFill(Color.GREY);
            }

            gc.fillOval(x - circleDiameter / 2, yOffset - circleDiameter / 2, circleDiameter, circleDiameter);

            String key = pointType + "#" + i;
            servicePointPositions.put(key, new Point2D(x, yOffset));
        }
    }

    private void pause(OmaMoottori m) {
        paused = !paused;
        m.setPause();
        Trace.out(Trace.Level.INFO, "PAUSE PRESSED");
        if (paused) {
            logging("Simulation paused");
        } else {
            logging("Simulation resumed");
        }
    }

    public void logging(String s) {
        Platform.runLater(() -> {
            LocalTime currentTime = LocalTime.now();
            String timeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            Text timeText = new Text(timeString + "  ");
            timeText.setStyle("-fx-font-weight: bold;");

            Text messageText = new Text(s);

            TextFlow textFlow = new TextFlow(timeText, messageText);

            logTextArea.getItems().add(textFlow);

            if (logTextArea.getItems().size() > 100) {
                logTextArea.getItems().removeFirst();
            }

            logTextArea.scrollTo(logTextArea.getItems().size() - 1);
        });
    }

    // later add this button
    public void handleSetMechanics(ActionEvent actionEvent) {
    }

    public void drawAllServicePoints() {
        drawAllServicePointsOnCarRepairCanvas();
    }
}