import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simu.framework.Tapahtuma;
import simu.framework.Tapahtumalista;
import simu.framework.Trace;
import simu.model.OmaMoottori;
import simu.model.TapahtumanTyyppi;

class OmaMoottoriTest {

    private TestableOmaMoottori moottori;
    private Tapahtumalista tapahtumalista;

    private static class TestableOmaMoottori extends OmaMoottori {
        @Override
        public void alustukset() {
            super.alustukset();
        }

        @Override
        public void suoritaTapahtuma(Tapahtuma tapahtuma) {
            super.suoritaTapahtuma(tapahtuma);
        }
    }

    @BeforeEach
    void setUp() {
        Trace.setTraceLevel(Trace.Level.INFO);
        tapahtumalista = new Tapahtumalista();
        moottori = new TestableOmaMoottori();
        moottori.setAllServiceTime(10, 15, 20, 10, 10); // Set service times
        moottori.setSpotValues(10, 15, 20, 10, 10); // Set spot values
        moottori.alustukset(); // Initialize the motor
    }

    @Test
    void testInitialization() {
        moottori.alustukset();
        assertEquals(0, moottori.getServedRegularCars());
        assertEquals(0, moottori.getServedElectricCars());
        assertEquals(0.0, moottori.calculateTotalEarnings());
    }

    @Test
    void testSetSimulationTime() {
        moottori.setSimulointiAika(100.0);
        assertThrows(IllegalArgumentException.class, () -> moottori.setSimulointiAika(-5.0));
    }

    @Test
    void testSetPrices() {
        moottori.setAllPrices(200, 400, 100);
        assertEquals(200, moottori.calculateTotalEarnings(), 0.01);
    }

    @Test
    void testCarArrival() {
        Tapahtuma tapahtuma = new Tapahtuma(TapahtumanTyyppi.CAR_ARRIVES, 0, null);
        moottori.suoritaTapahtuma(tapahtuma);
        boolean customerAdded = moottori.getAllServicePointsList().get(0).stream()
                .anyMatch(p -> p.getQueueSize() > 0);
        assertTrue(customerAdded, "Customer should be added to the arrival queue");
    }
}