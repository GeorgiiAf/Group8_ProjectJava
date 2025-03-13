package controller;

import simu.model.SimulationListener;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
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
import java.util.*;

/**
 * Controller class for the simulation application.
 */
public class SimulaattorinController implements SimulationListener {

    public Label inputErrorLabel;
    public Label earnedMoneyLabel;
    public Label servedRegularCarsLabel;
    public Label servedElectricCarsLabel;
    public Button slowDownButton;
    public Button speedUpButton;
    public Label rejectedCustomers;
    private IKontrolleriForV kontrolleri;
    private OmaMoottori moottori;
    private boolean simulationRunning = false;
    private Thread simulationThread;
    private AnimationTimer animationTimer;
    private boolean paused = false;

    private final Map<String, Integer> servicePointsMap = new LinkedHashMap<>();
    private final Map<String, Integer> maxPointsSpinner = new LinkedHashMap<>();
    private final Map<String, Integer> prices = new LinkedHashMap<>();

    private final Map<String, Double> animationScales = new HashMap<>();
    private final Map<String, Point2D> servicePointPositions = new LinkedHashMap<>();


    @FXML
    private TextField aika;
    @FXML
    private Label speedLabel;
    @FXML
    private Label tulos;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Canvas carRepairCanvas; // Canvas for drawing service points

    @FXML
    private Spinner<Integer> arrivalSpots;
    @FXML
    private Spinner<Integer> diagnosticsSpots;
    @FXML
    private Spinner<Integer> partsSpots;
    @FXML
    private Spinner<Integer> maintenanceSpots;
    @FXML
    private Spinner<Integer> carReadySpots;

    @FXML
    private Slider defaultCostSlider;
    @FXML
    private Slider electricCostSlider;
    @FXML
    private Slider partsCostSlider;
    @FXML
    private Label defaultCostLabel;
    @FXML
    private Label electricCostLabel;
    @FXML
    private Label partsCostLabel;

    @FXML
    private Button showResultsButton;
    @FXML
    private Button helpButton;
    @FXML
    private ListView<TextFlow> logTextArea;
    @FXML
    private ListView<TextFlow> infoBox;

    @FXML
    private Slider arrivalTimeSlider;
    @FXML
    private Slider diagnosticsTimeSlider;
    @FXML
    private Slider partsTimeSlider;
    @FXML
    private Slider maintenanceTimeSlider;
    @FXML
    private Slider carReadyTimeSlider;

    private GraphicsContext gc;

    /**
     * Initializes the controller class.
     */
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

        if (carRepairCanvas != null) {
            gc = carRepairCanvas.getGraphicsContext2D();
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, carRepairCanvas.getWidth(), carRepairCanvas.getHeight());
        }

        maxPointsSpinner.put("CAR_ARRIVES", 40);
        maxPointsSpinner.put("DIAGNOSTIC_DONE", 30);
        maxPointsSpinner.put("PARTS_ORDERED", 30);
        maxPointsSpinner.put("SIMPLE_MAINTENANCE", 30);
        maxPointsSpinner.put("CAR_READY", 30);
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

    /**
     * Sets the controller for the simulation.
     *
     * @param kontrolleri the controller to set
     */
    public void setKontrolleri(IKontrolleriForV kontrolleri) {
        this.kontrolleri = kontrolleri;
    }


    @Override
    public void onLogMessage(String message) {
        logging(message);
    }

    /**
     * Gets the simulation time.
     *
     * @return the simulation time
     */

    public double getAika() {
        try {
            return Double.parseDouble(aika.getText());
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid simulation time");
            return 0;
        }
    }

    /**
     * Gets the delay time.
     *
     * @return the delay time
     */
    public long getViive() {
        try {
            return Long.parseLong(speedLabel.getText());
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid delay value");
            return 0;
        }
    }

    /**
     * Sets the end time of the simulation.
     *
     * @param aika the end time to set
     */
    public void setLoppuaika(double aika) {
        tulos.setText(String.format("%.2f", aika));
    }


    /**
     * Creates a new customer.
     */
    public void uusiAsiakas() {
        updateVisualisation();
    }

    /**
     * Shows an error message.
     *
     * @param message the error message to show
     */

    private void showErrorMessage(String message) {
        if (inputErrorLabel != null) {
            inputErrorLabel.setText(message);
            inputErrorLabel.setVisible(true);
        } else {
            System.err.println(message);
        }
    }

    /**
     * Handles the start button action.
     */
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

        moottori = new OmaMoottori();
        moottori.addSimulationListener(this);
        moottori.setSimulointiLoppu(false);

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

        pauseButton.setOnAction(_ -> pause(moottori));

        animationTimer.start();
        simulationThread.start();
    }

    /**
     * Handles the stop button action.
     */

    @FXML
    private void handleStopButton() {
        if (!simulationRunning || moottori == null || moottori.isSimulointiLoppu()) {
            return;
        }

        moottori.setSimulointiLoppu(true);
        moottori.stopSimulation();

        try {
            simulationThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (startButton != null) startButton.setDisable(false);
        if (stopButton != null) stopButton.setDisable(true);
        animationTimer.stop();
    }

    /**
     * Handles the help button action.
     */

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

    /**
     * Handles the show results button action.
     */

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

    /**
     * Updates the results from the motor.
     */

    private void updateResultsFromMoottori() {
        if (moottori == null || kontrolleri == null) {
            return;
        }
        kontrolleri.setTotalEarnings(moottori.calculateTotalEarnings());
        kontrolleri.setServedRegularCars(moottori.getServedRegularCars());
        kontrolleri.setServedElectricCars(moottori.getServedElectricCars());
        kontrolleri.setRejectedCustomers(moottori.getRejectedCustomers());
    }

    /**
     * Updates the visualization.
     */

    public void updateVisualisation() {
        if (gc == null || moottori == null) {
            return;
        }

        ArrayList<ArrayList<Palvelupiste>> allServicePoints = moottori.getAllServicePointsList();
        if (allServicePoints == null || allServicePoints.isEmpty()) {
            return;
        }
        gc.clearRect(0, 0, carRepairCanvas.getWidth(), carRepairCanvas.getHeight());

        drawAllServicePoints();

        for (ArrayList<Palvelupiste> servicePoints : allServicePoints) {
            int totalCustomers = 0;

            for (Palvelupiste servicePoint : servicePoints) {
                totalCustomers += servicePoint.getQueueSize();
                if (servicePoint.onVarattu()) {
                    totalCustomers++;
                }
            }

            for (int i = 0; i < servicePoints.size(); i++) {
                String key = servicePoints.get(0).getType().toUpperCase() + "#" + i;
                Point2D position = servicePointPositions.get(key);

                if (position != null) {
                    if (i < totalCustomers) {
                        initializeAnimation(key);

                        updateAnimationScale(key, 0.016); // Delta time for 60 FPS

                        double scale = animationScales.get(key);

                        gc.setFill(Color.RED);
                        double circleSize = 10 * scale;
                        gc.fillOval(
                                position.getX() - circleSize / 2,
                                position.getY() - circleSize / 2,
                                circleSize,
                                circleSize
                        );
                    } else {
                        animationScales.remove(key);
                    }
                }
            }
        }
    }

    /**
     * Handles the speed up button action.
     */

    @FXML
    private void handleSpeedUpButton() {
        if (moottori != null) {
            long currentDelay = moottori.getViive();
            if (currentDelay > 100) {
                moottori.setViive(currentDelay - 100);
            } else if (currentDelay > 10) {
                moottori.setViive(currentDelay - 10);
            }
            updateLabelWithValue((int) currentDelay, speedLabel);
        }
    }


    /**
     * Handles the slow down button action.
     */

    @FXML
    private void handleSlowDownButton() {
        if (moottori != null) {
            long currentDelay = moottori.getViive();
            if (currentDelay < 100) {
                moottori.setViive(100);
            } else {
                moottori.setViive(currentDelay + 100);
            }
            updateLabelWithValue((int) currentDelay, speedLabel);
        }
    }

    /**
     * Draws the type label.
     *
     * @param gc        the graphics context
     * @param pointType the point type
     * @param y         the y-coordinate
     */

    private void drawTypeLabel(GraphicsContext gc, String pointType, double y) {
        double xLeftEdge = 5;
        gc.setFill(Color.BLACK);
        gc.fillText(pointType, xLeftEdge, y - 15);
    }

    /**
     * Draws all service points.
     */

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

    /**
     * Draws the service points.
     *
     * @param gc             the graphics context
     * @param pointType      the point type
     * @param activatedCount the activated count
     * @param totalPoints    the total points
     * @param yStart         the y-coordinate start
     */

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

            String key = pointType.toUpperCase() + "#" + i;
            servicePointPositions.put(key, new Point2D(x, yOffset));
        }
    }

    /**
     * Pauses the simulation.
     *
     * @param m the motor
     */
    private void pause(OmaMoottori m) {
        paused = !paused;
        m.setPause();
        Trace.out(Trace.Level.INFO, "PAUSE PRESSED");
        boolean wasPaused = paused;
        paused = false;
        if (wasPaused) {
            logging("Simulation paused");
        } else {
            logging("Simulation resumed");
        }

        paused = wasPaused;
    }

    /**
     * Logs a message.
     *
     * @param s the message to log
     */

    public void logging(String s) {
        if (!paused) {

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
    }

    /**
     * Logs changes.
     *
     * @param s the changes to log
     */

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

    /**
     * Initializes the spinners.
     */

    private void initializeSpinners() {
        setupSpinner(arrivalSpots, "CAR_ARRIVES");
        setupSpinner(diagnosticsSpots, "DIAGNOSTIC_DONE");
        setupSpinner(partsSpots, "PARTS_ORDERED");
        setupSpinner(maintenanceSpots, "SIMPLE_MAINTENANCE");
        setupSpinner(carReadySpots, "CAR_READY");
    }

    /**
     * Sets up a spinner.
     *
     * @param spinner   the spinner
     * @param pointType the point type
     */

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

    /**
     * Updates the slider text.
     *
     * @param slider the slider
     */
    public void sliderTextUpdate(Slider slider) {
        slider.setOnMouseReleased(_ -> loggingChanges(slider.getId() + " -> " + slider.getValue()));
    }

    /**
     * Updates the prices.
     */

    private void updatePrices() {
        if (this.moottori != null) {
            double newDefaultCost = defaultCostSlider.getValue();
            double newElectricCost = electricCostSlider.getValue();
            double newPartsCost = partsCostSlider.getValue();

            moottori.setAllPrices(newDefaultCost, newElectricCost, newPartsCost);
        }
    }

    /**
     * Updates the label with a value.
     *
     * @param value the value
     * @param label the label
     */
    private static void updateLabelWithValue(int value, Label label) {
        label.setText(String.valueOf(value));
    }

    /**
     * Links a slider to a label.
     *
     * @param slider the slider
     * @param label  the label
     */

    public void linkSliderToLabel(Slider slider, Label label) {
        updateLabelWithValue((int) slider.getValue(), label);

        slider.setOnMouseReleased(_ -> {
            updatePrices();
            loggingChanges(slider.getId() + " -> " + slider.getValue());
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> updateLabelWithValue(newValue.intValue(), label));
    }


    /**
     * Initializes the animation.
     *
     * @param key the key
     */

    private void initializeAnimation(String key) {
        if (!animationScales.containsKey(key)) {
            animationScales.put(key, 0.0);
        }
    }

    /**
     * Updates the animation scale.
     *
     * @param key       the key
     * @param deltaTime the delta time
     */

    private void updateAnimationScale(String key, double deltaTime) {
        if (animationScales.containsKey(key)) {
            double currentScale = animationScales.get(key);
            if (currentScale < 1.0) {
                currentScale += deltaTime * 2;
                if (currentScale > 1.0) {
                    currentScale = 1.0;
                }
                animationScales.put(key, currentScale);
            }
        }
    }
}