package simu.model;

import simu.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

public class Palvelupiste {

	private final LinkedList<Asiakas> jono = new LinkedList<>();
	private final ContinuousGenerator generator;
	private final Tapahtumalista tapahtumalista;
	private final TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;
	private int vaihe = 1;

	private boolean varattu = false;

	public Palvelupiste(ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi) {
		this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = tyyppi;
	}

	public void lisaaJonoon(Asiakas a) {
		jono.add(a);
	}

	public Asiakas otaJonosta() {
		varattu = false;
		return jono.poll();
	}

	public void aloitaPalvelu() {
		if (!jono.isEmpty()) {
			Asiakas asiakas = jono.peek();
			Trace.out(Trace.Level.INFO, "Aloitetaan palvelu asiakkaalle " + asiakas.getId() + " vaiheessa " + vaihe);

			varattu = true;
			double palveluaika = generator.sample();
			tapahtumalista.lisaa(new Tapahtuma(skeduloitavanTapahtumanTyyppi, Kello.getInstance().getAika() + palveluaika));

			asiakas.setVaiheaika(vaihe, palveluaika);
		}
	}

	public boolean onVarattu() {
		return varattu;
	}

	public boolean onJonossa() {
		return !jono.isEmpty();
	}

	public void setVaihe(int uusiVaihe) {
		if (uusiVaihe >= 1 && uusiVaihe <= 4) {
			this.vaihe = uusiVaihe;
		}
	}
}
