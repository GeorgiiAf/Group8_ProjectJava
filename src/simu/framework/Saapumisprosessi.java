package simu.framework;
import eduni.distributions.*;
import simu.model.Asiakas;

public class Saapumisprosessi {
	
	private ContinuousGenerator generaattori;
	private Tapahtumalista tapahtumalista;
	private ITapahtumanTyyppi tyyppi;

	public Saapumisprosessi(ContinuousGenerator g, Tapahtumalista tl, ITapahtumanTyyppi tyyppi){
		this.generaattori = g;
		this.tapahtumalista = tl;
		this.tyyppi = tyyppi;
	}

	public void generoiSeuraava(Asiakas asiakas){
		Trace.out(Trace.Level.INFO, "Generoi seuraava");
		Tapahtuma t = new Tapahtuma(tyyppi, Kello.getInstance().getAika()+generaattori.sample(), asiakas);
		tapahtumalista.lisaa(t);
	}

}
