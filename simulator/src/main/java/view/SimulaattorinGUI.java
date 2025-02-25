package view;

import controller.*;
import controller.Kontrolleri;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simu.framework.Trace;
import simu.framework.Trace.Level;

import java.text.DecimalFormat;



public class SimulaattorinGUI extends Application implements ISimulaattorinUI {

    //Kontrollerin esittely (tarvitaan käyttöliittymässä)
    private IKontrolleriForV kontrolleri;

    // Käyttöliittymäkomponentit:
    private TextField aika;
    private TextField viive;
    private Label tulos;
    private Label aikaLabel;
    private Label viiveLabel;
    private Label tulosLabel;

    private Button kaynnistaButton;
    private Button hidastaButton;
    private Button nopeutaButton;

    private IVisualisointi naytto;


    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
    }

    @Override
    public void start(Stage primaryStage) {
        // Käyttöliittymän rakentaminen
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


    @Override
    public double getAika() {
        return Double.parseDouble(aika.getText());
    }

    @Override
    public long getViive() {
        return Long.parseLong(viive.getText());
    }

    @Override
    public void setLoppuaika(double aika) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        this.tulos.setText(formatter.format(aika));
    }


    @Override
    public IVisualisointi getVisualisointi() {
        return naytto;
    }
}


