package simu.model;

import simu.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

public class Palvelupiste {

	private final LinkedList<Asiakas> jono = new LinkedList<>(); // Tietorakennetoteutus
	private final ContinuousGenerator generator;
	private final Tapahtumalista tapahtumalista;
	private final TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;


	private final int maxCapacity;
	private boolean varattu = false;
	private double totalBusyTime = 0;
	private double lastStartTime = 0;

	public Palvelupiste(ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi, int maxCapacity){
		this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = tyyppi;
		this.maxCapacity = maxCapacity;
				
	}


	public boolean lisaaJonoon(Asiakas a) {
		if (jono.size() < maxCapacity) {
			jono.add(a);
			Trace.out(Trace.Level.INFO, "Asiakas " + a.getId() + " lisätty jonoon.");
			return true;
		} else {
			Trace.out(Trace.Level.INFO, "Jonossa ei ole tilaa asiakkaalle " + a.getId() + ". Asiakas joutuu odottamaan.");
			return false;
		}
	}


	public Asiakas otaJonosta(){
		if (varattu) {
			double endTime = Kello.getInstance().getAika();
			totalBusyTime += (endTime - lastStartTime);
		}

		varattu = false;
		return jono.poll();
	}


	public void aloitaPalvelu(){ //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		if (jono.isEmpty()) return;

		Trace.out(Trace.Level.INFO, "Aloitetaan uusi palvelu asiakkaalle " + jono.peek().getId());
		
		varattu = true;
		lastStartTime = Kello.getInstance().getAika();
		double palveluaika = generator.sample();
		tapahtumalista.lisaa(new Tapahtuma(skeduloitavanTapahtumanTyyppi,Kello.getInstance().getAika()+palveluaika));
	}



	public boolean onVarattu(){
		return varattu;
	}


	public boolean onJonossa(){
		return !jono.isEmpty();
	}

	public double getUtilizationRate() {
		double totalTime = Kello.getInstance().getAika();
		return totalBusyTime / totalTime;
	}

	public boolean hasFreeSpot() {
		return jono.size() < maxCapacity;
	}


}
