package simu.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simu.framework.Tapahtuma;
import simu.framework.Tapahtumalista;
import simu.framework.Trace;

class OmaMoottoriTest {

    private OmaMoottori moottori;
    private Tapahtumalista tapahtumalista;

    @BeforeEach
    void setUp() {
        Trace.setTraceLevel(Trace.Level.INFO);
        tapahtumalista = new Tapahtumalista();
        moottori = new OmaMoottori();
        if (moottori.getAllServicePointsList().isEmpty()) {
            moottori.();
        }
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
        assertTrue(customerAdded, "");
    }


}
