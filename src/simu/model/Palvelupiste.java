package simu.model;

import simu.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

public class Palvelupiste {

	private final LinkedList<Asiakas> jono = new LinkedList<>(); // Tietorakennetoteutus
	private final ContinuousGenerator generator;
	private final Tapahtumalista tapahtumalista;
	private final TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;


	//JonoStartegia strategia; //optio: asiakkaiden järjestys
	
	private boolean varattu = false;
	private double totalBusyTime = 0;
	private double lastStartTime = 0;


	public Palvelupiste(ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi){
		this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = tyyppi;
				
	}


	public void lisaaJonoon(Asiakas a){   // Jonon 1. asiakas aina palvelussa
		jono.add(a);
		
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


}
