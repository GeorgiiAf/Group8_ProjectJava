package simu.model;

import simu.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

public class Palvelupiste {
	private final int maxEngineers;
	private int availableEngineers;
	private final LinkedList<Asiakas> jono = new LinkedList<>(); // Tietorakennetoteutus
	private final ContinuousGenerator generator;
	private final Tapahtumalista tapahtumalista;
	private final TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;


	//JonoStartegia strategia; //optio: asiakkaiden järjestys
	
	private boolean varattu = false;
	private double totalBusyTime = 0;
	private double lastStartTime = 0;


	public Palvelupiste(int maxEngineers, ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi){
        this.maxEngineers = maxEngineers;
		this.availableEngineers = maxEngineers;
        this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = tyyppi;
	}


	public void lisaaJonoon(Asiakas a) {
		jono.add(a);
		a.setSaapumisaika(Kello.getInstance().getAika());
		Trace.out(Trace.Level.INFO, "Asiakas " + a.getId() + " lisättiin jonoon palvelupisteeseen " + skeduloitavanTapahtumanTyyppi);
	}


	public Asiakas otaJonosta(){
		if (varattu) {
			double endTime = Kello.getInstance().getAika();
			totalBusyTime += (endTime - lastStartTime);
		}
		availableEngineers++;
		varattu = false;
		return jono.poll();
	}

	public void yritaAloitaPalvelu() {
		Trace.out(Trace.Level.INFO, "Attempting to start service at " + skeduloitavanTapahtumanTyyppi + ". Engineers: " + availableEngineers + ", Queue: " + jono.size());
		if (availableEngineers > 0 && !jono.isEmpty()) {
			availableEngineers--;

			varattu = true;
			lastStartTime = Kello.getInstance().getAika();
			double palveluAika = generator.sample();
			Trace.out(Trace.Level.INFO, "PALVELUAIKA-> "+ palveluAika);
			Tapahtuma t = new Tapahtuma(skeduloitavanTapahtumanTyyppi, Kello.getInstance().getAika() + palveluAika, this.jono.getLast());
			tapahtumalista.lisaa(t);
		}
	}
	//TODO remove
	public void palveluValmis() {
		availableEngineers++;
		Trace.out(Trace.Level.INFO, "Engineer freed at " + skeduloitavanTapahtumanTyyppi + " Available: " + availableEngineers);
		yritaAloitaPalvelu();
	}


//	public void aloitaPalvelu(){ //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
//		if (jono.isEmpty()) return;
//
//		Trace.out(Trace.Level.INFO, "Aloitetaan uusi palvelu asiakkaalle " + jono.peek().getId());
//
//		varattu = true;
//		lastStartTime = Kello.getInstance().getAika();
//		double palveluaika = generator.sample();
//		tapahtumalista.lisaa(new Tapahtuma(skeduloitavanTapahtumanTyyppi,Kello.getInstance().getAika()+palveluaika));
//	}



	public boolean onVarattu(){
		return varattu;
	}


	public boolean onJonossa(){
		return !jono.isEmpty();
	}

	public int getQueueSize() {
		return this.jono.size();
	}

	public double getUtilizationRate() {
		double totalTime = Kello.getInstance().getAika();
		return totalBusyTime / totalTime;
	}


}
