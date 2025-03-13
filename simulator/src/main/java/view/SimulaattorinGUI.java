package view;

import controller.Kontrolleri;
import controller.SimulaattorinController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simu.framework.Trace;
import simu.framework.Trace.Level;

/**
 * Represents the main GUI application for the simulator.
 */
public class SimulaattorinGUI extends Application {

    private Kontrolleri kontrolleri;

    /**
     * Initializes the application.
     */
    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
    }

    /**
     * Starts the application and sets up the primary stage.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            kontrolleri = new Kontrolleri();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/simulator.fxml"));
            Pane root = loader.load();

            SimulaattorinController controller = loader.getController();
            controller.setKontrolleri(kontrolleri);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Simulator");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}