package simu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import eduni.distributions.ContinuousGenerator;
import simu.framework.Tapahtumalista;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PalvelupisteTest {

    private Palvelupiste palvelupiste;
    private ContinuousGenerator generator;
    private Tapahtumalista tapahtumalista;

    @BeforeEach
    void setUp() {
        simu.framework.Trace.setTraceLevel(simu.framework.Trace.Level.INFO);
        generator = mock(ContinuousGenerator.class);
        tapahtumalista = mock(Tapahtumalista.class);
        palvelupiste = new Palvelupiste(2, generator, tapahtumalista, TapahtumanTyyppi.ARRIVAL);
    }

    @Test
    void testLisaaJonoon() {
        Asiakas asiakas = mock(Asiakas.class);
        palvelupiste.lisaaJonoon(asiakas);
        assertEquals(1, palvelupiste.getQueueSize());
    }

    @Test
    void testOtaJonosta() {
        Asiakas asiakas = mock(Asiakas.class);
        palvelupiste.lisaaJonoon(asiakas);
        Asiakas otettu = palvelupiste.otaJonosta();
        assertNotNull(otettu);
        assertEquals(0, palvelupiste.getQueueSize());
    }

    @Test
    void testAloitaPalvelu() {
        when(generator.sample()).thenReturn(5.0);
        Asiakas asiakas = mock(Asiakas.class);
        palvelupiste.lisaaJonoon(asiakas);
        palvelupiste.aloitaPalvelu();
        verify(tapahtumalista, times(1)).lisaa(any());
    }

    @Test
    void testOnJonossa() {
        assertFalse(palvelupiste.onJonossa());
        palvelupiste.lisaaJonoon(mock(Asiakas.class));
        assertTrue(palvelupiste.onJonossa());
    }

    @Test
    void testOnVarattu() {
        assertFalse(palvelupiste.onVarattu());
        when(generator.sample()).thenReturn(5.0);
        palvelupiste.lisaaJonoon(mock(Asiakas.class));
        palvelupiste.aloitaPalvelu();
        assertTrue(palvelupiste.onVarattu());
    }

    @Test
    void testFullServiceCycle() {
        when(generator.sample()).thenReturn(5.0);
        Asiakas asiakas = mock(Asiakas.class);
        palvelupiste.lisaaJonoon(asiakas);
        assertTrue(palvelupiste.onJonossa());
        palvelupiste.aloitaPalvelu();
        assertTrue(palvelupiste.onVarattu());
        Asiakas processed = palvelupiste.otaJonosta();
        assertEquals(asiakas, processed);
        assertFalse(palvelupiste.onJonossa());
        assertFalse(palvelupiste.onVarattu());
    }

    @Test
    void testMultipleCustomers() {
        when(generator.sample()).thenReturn(3.0);
        Asiakas asiakas1 = mock(Asiakas.class);
        Asiakas asiakas2 = mock(Asiakas.class);
        palvelupiste.lisaaJonoon(asiakas1);
        palvelupiste.lisaaJonoon(asiakas2);
        assertEquals(2, palvelupiste.getQueueSize());
        palvelupiste.aloitaPalvelu();
        palvelupiste.otaJonosta();
        assertEquals(1, palvelupiste.getQueueSize());
        palvelupiste.aloitaPalvelu();
        palvelupiste.otaJonosta();
        assertEquals(0, palvelupiste.getQueueSize());
    }


}
