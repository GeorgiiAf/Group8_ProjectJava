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

public class SimulaattorinGUI extends Application {

    private Kontrolleri kontrolleri;
    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
    }

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