package controller;

//  REWORK  THIS

import java.awt.Point;
import simu.model.SimulationListener;

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
import javafx.util.converter.IntegerStringConverter;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import simu.model.OmaMoottori;
import simu.model.Palvelupiste;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
THAT SHOULD BE THE MAIN CONTROLLER
REWORK
*/

public class SimulaattorinController implements SimulationListener {

    public Label inputErrorLabel;
    public Label earnedMoneyLabel;
    public Label servedRegularCarsLabel;
    public Label servedElectricCarsLabel;
    public Button slowDownButton;
    public Button speedUpButton;
    public Slider partsWaitingTimeSlider;
    public Button setMechanicsButton;
    public TextField mechanicsCountField;
    public Slider arrivalTimeSlider5;
    public Slider arrivalTimeSlider6;
    public Label rejectedCustomers;
    private IKontrolleriForV kontrolleri;
    private OmaMoottori moottori;
    private boolean simulationRunning = false;
    private Thread simulationThread;
    private AnimationTimer animationTimer;
    private boolean paused = false;

    // Maps for service point and customer configurations
    private final Map<String, Integer> servicePointsMap = new LinkedHashMap<>();
    private final Map<String, Integer> maxPointsSpinner = new LinkedHashMap<>();
    private final Map<String, Integer> prices = new LinkedHashMap<>();

//    private AnimationTimer animationTimer;
    private Map<String, Point2D> servicePointPositions = new LinkedHashMap<>();

    int carServiceCostDefault = 150;
    int electricCarServiceCostDefault = 300;
    int partsCostDefault = 75;


    @FXML private TextField aika;
    @FXML private Label speedLabel;
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

    @FXML private Slider defaultCostSlider;
    @FXML private Slider electricCostSlider;
    @FXML private Slider partsCostSlider;
    @FXML private Label defaultCostLabel;
    @FXML private Label electricCostLabel;
    @FXML private Label partsCostLabel;

    @FXML private Canvas naytto;
    @FXML private Button showResultsButton;
    @FXML private Button helpButton;
    @FXML private Canvas workshopCanvas;
    @FXML private ListView<TextFlow> logTextArea;
    @FXML private ListView<TextFlow> infoBox;

    @FXML private Slider arrivalTimeSlider;
    @FXML private Slider diagnosticsTimeSlider;
    @FXML private Slider partsTimeSlider;
    @FXML private Slider maintenanceTimeSlider;
    @FXML private Slider carReadyTimeSlider;

    private GraphicsContext gc;

    @FXML
    private void initialize() {

        SpinnerValueFactory<Integer> factoryArrivalSpots =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        factoryArrivalSpots.setValue(8);

        SpinnerValueFactory<Integer> factoryDiagnosticsSpots =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        factoryDiagnosticsSpots.setValue(5);

        SpinnerValueFactory<Integer> factoryPartsSpots =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
        factoryPartsSpots.setValue(10);

        SpinnerValueFactory<Integer> factoryMaintenanceSpots =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
        factoryMaintenanceSpots.setValue(5);

        SpinnerValueFactory<Integer> factoryCarReadySpots =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
        factoryCarReadySpots.setValue(10);

        arrivalSpots.setValueFactory(factoryArrivalSpots);
        diagnosticsSpots.setValueFactory(factoryDiagnosticsSpots);
        partsSpots.setValueFactory(factoryPartsSpots);
        maintenanceSpots.setValueFactory(factoryMaintenanceSpots);
        carReadySpots.setValueFactory(factoryCarReadySpots);

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
            drawServicePoints();
        }

        maxPointsSpinner.put("CarArrives", 40);
        maxPointsSpinner.put("DiagnosticsDone", 30);
        maxPointsSpinner.put("Parts", 30);
        maxPointsSpinner.put("Maintenance", 30);
        maxPointsSpinner.put("CarReady", 30);
        servicePointsMap.put("CarArrives", arrivalSpots.getValue());
        servicePointsMap.put("DiagnosticsDone", diagnosticsSpots.getValue());
        servicePointsMap.put("Parts", partsSpots.getValue());
        servicePointsMap.put("Maintenance", maintenanceSpots.getValue());
        servicePointsMap.put("CarReady", carReadySpots.getValue());

        sliderTextUpdate(carReadyTimeSlider);
        sliderTextUpdate(diagnosticsTimeSlider);
        sliderTextUpdate(partsTimeSlider);
        sliderTextUpdate(maintenanceTimeSlider);
        sliderTextUpdate(carReadyTimeSlider);

        initializeSpinners();

        linkSliderToLabel(defaultCostSlider, defaultCostLabel);
        linkSliderToLabel(electricCostSlider, electricCostLabel);
        linkSliderToLabel(partsCostSlider, partsCostLabel);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateVisualisation();
            }
        };
        drawAllServicePoints();
    }

    public void setKontrolleri(IKontrolleriForV kontrolleri) {
        this.kontrolleri = kontrolleri;
    }


    @Override
    public void onLogMessage(String message) {
        logging(message);
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
            return Long.parseLong(speedLabel.getText());
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
            moottori.addSimulationListener(this);

        }
        moottori.setSimulointiLoppu(false);



        Trace.setTraceLevel(Level.INFO);

        simulationThread = new Thread(() -> {

            int arrivalTime = (int) arrivalTimeSlider.getValue();
            int diagnosticsTime = (int) diagnosticsTimeSlider.getValue();
            int partsTime = (int) partsTimeSlider.getValue();
            int maintenanceTime = (int) maintenanceTimeSlider.getValue();
            int carReadyTime = (int) carReadyTimeSlider.getValue();

            logging("Arrival time set to -> " + arrivalTime);

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
                        arrivalSpots.getValue(),
                        diagnosticsSpots.getValue(),
                        partsSpots.getValue(),
                        maintenanceSpots.getValue(),
                        carReadySpots.getValue()
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

        if (moottori != null) {
            String message = String.format(
                    "[Simulation Stopped] Simulation stopped by user.\n" +
                            "Total earnings: %.2f EUR\n" +
                            "Served regular cars: %d\n" +
                            "Served electric cars: %d\n" +
                            "Rejected customers: %d",
                    moottori.calculateTotalEarnings(),
                    moottori.getServedRegularCars(),
                    moottori.getServedElectricCars(),
                    moottori.getRejectedCustomers()
            );
            logging(message);
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

        gc.clearRect(0, 0, workshopCanvas.getWidth(), workshopCanvas.getHeight());

        drawServicePoints();

        ArrayList<ArrayList<Palvelupiste>> allServicePoints = moottori.getAllServicePointsList();
        if (allServicePoints == null || allServicePoints.isEmpty()) {
            return;
        }
        int[] queueSizes = new int[allServicePoints.size()];
        for (int i = 0; i < allServicePoints.size(); i++) {
            queueSizes[i] = allServicePoints.get(i).size();
        }
        drawQueues(queueSizes);

        List<Point> customerPositions = new ArrayList<>();
        for (ArrayList<Palvelupiste> servicePoints : allServicePoints) {
            for (Palvelupiste servicePoint : servicePoints) {
                customerPositions.add(new Point(100, 100));            }
        }
        drawCustomers(customerPositions);
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
            else if (currentDelay > 10) {
                moottori.setViive(currentDelay - 10);
            }
            updateLabelWithValue((int) currentDelay, speedLabel);
        }
    }

    @FXML
    private void handleSlowDownButton() {
        if (moottori != null) {
            long currentDelay = moottori.getViive();
            if (currentDelay < 100) {
                moottori.setViive(100);
            }
            else {
                moottori.setViive(currentDelay + 100);
            }
            updateLabelWithValue((int) currentDelay, speedLabel);
        }
    }

    private void drawTypeLabel(GraphicsContext gc, String pointType, double y) {
        double xLeftEdge = 5;
        gc.setFill(Color.BLACK);
        gc.fillText(pointType, xLeftEdge, y-15);
    }


    public void drawAllServicePoints() {
        GraphicsContext gc = carRepairCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, carRepairCanvas.getWidth(), carRepairCanvas.getHeight());

        double yStep = 75;
        int typeIndex = 0;

        for (Map.Entry<String, Integer> entry : maxPointsSpinner.entrySet()) {
            String pointType = entry.getKey();
            int totalPoints = entry.getValue();
            int activatedPoints = servicePointsMap.getOrDefault(pointType, 0);
            double y = yStep * (typeIndex + 1);

            drawServicePoints(gc, pointType, activatedPoints, totalPoints, y);
            drawTypeLabel(gc, pointType, y);

            typeIndex++;
        }
    }

    public void drawServicePoints(GraphicsContext gc, String pointType, int activatedCount, int totalPoints, double yStart) {
        double circleDiameter = 15.0;
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
                gc.setFill(Color.web("#26bd44"));
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
                logTextArea.getItems().remove(0);
            }

            logTextArea.scrollTo(logTextArea.getItems().size() - 1);
        });
    }

    public void loggingChanges(String s) {
        Platform.runLater(() -> {
            Text timeText = new Text();
            timeText.setStyle(
                    "-fx-font-family: 'Consolas';" +
                            "-fx-font-weight: bold;" +
                            "-fx-fill: green;"
            );

            Text messageText = new Text(s);
            messageText.setStyle(
                    "-fx-font-family: 'Consolas';" +
                            "-fx-fill: green;"
            );

            TextFlow textFlow = new TextFlow(timeText, messageText);

            infoBox.getItems().add(textFlow);

            if (infoBox.getItems().size() > 100) {
                infoBox.getItems().remove(0);
            }

            infoBox.scrollTo(infoBox.getItems().size() - 1);
        });
    }

    private void initializeSpinners() {
        setupSpinner(arrivalSpots, "CarArrives");
        setupSpinner(diagnosticsSpots, "DiagnosticsDone");
        setupSpinner(partsSpots, "Parts");
        setupSpinner(maintenanceSpots, "Maintenance");
        setupSpinner(carReadySpots, "CarReady");
    }

    private void setupSpinner(Spinner<Integer> spinner, String pointType) {
        int defaultValue = spinner.getValue();

        int maxPoints = maxPointsSpinner.getOrDefault(pointType, 0);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxPoints, defaultValue);
        spinner.setValueFactory(valueFactory);

        servicePointsMap.put(pointType, defaultValue);

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            servicePointsMap.put(pointType, newValue);


            drawAllServicePoints();
        });
    }

    public void sliderTextUpdate(Slider slider) {
        slider.setOnMouseReleased(event -> {
            loggingChanges(slider.getId() + " -> " + slider.getValue());
        });
    }

    private void updatePrices() {
        if(this.moottori != null) {
            double newDefaultCost = defaultCostSlider.getValue();
            double newElectricCost = electricCostSlider.getValue();
            double newPartsCost = partsCostSlider.getValue();

            moottori.setAllPrices(newDefaultCost, newElectricCost, newPartsCost);
        }
    }

    private static void updateLabelWithValue(int value, Label label) {
        label.setText(String.valueOf(value));
    }

    public void linkSliderToLabel(Slider slider, Label label) {
        updateLabelWithValue((int) slider.getValue(), label);

        slider.setOnMouseReleased(event -> {
            updatePrices();
            loggingChanges(slider.getId() + " -> " + slider.getValue());
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateLabelWithValue(newValue.intValue(), label);
        });
    }

    // later add this button
    public void handleSetMechanics(ActionEvent actionEvent) {
        return;
    }
}