package simu.model;

import simu.framework.*;

// TODO:
// Asiakas koodataan simulointimallin edellyttämällä tavalla (data!)
public class Asiakas {
	private double saapumisaika;
	private double poistumisaika;
	private int vuokraAika;
	private double maksuvaiheAika;
	private double varausvaiheAika;
	private double[] vaiheajat = new double[4];
	private int id;
	private static int i = 1;
	private static long sum = 0;
	private Varastohuone varasto;
	
	public Asiakas(){
	    id = i++;
	    
		saapumisaika = Kello.getInstance().getAika();
		Trace.out(Trace.Level.INFO, "Uusi asiakas nro " + id + " saapui klo "+saapumisaika);
	}

	public void setVaiheaika(int vaihe, double aika) {
		if (vaihe >= 1 && vaihe <= 4) {
			vaiheajat[vaihe - 1] = aika;
		}
	}



	public double getPoistumisaika() {
		return poistumisaika;
	}

	public void setPoistumisaika(double poistumisaika) {
		this.poistumisaika = poistumisaika;
	}

	public double getSaapumisaika() {
		return saapumisaika;
	}

	public void setSaapumisaika(double saapumisaika) {
		this.saapumisaika = saapumisaika;
	}
	


	public int getId() {
		return id;
	}
	
	public void raportti(){
		Trace.out(Trace.Level.INFO, "\nAsiakas "+id+ " valmis! ");
		Trace.out(Trace.Level.INFO, "Asiakas "+id+ " saapui: " +saapumisaika);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " poistui: " +poistumisaika);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " viipyi: " +(poistumisaika-saapumisaika));
		for (int i = 0; i < 4; i++) {
			Trace.out(Trace.Level.INFO, "Vaihe " + (i + 1) + " palveluaika: " + vaiheajat[i]);
		}

		sum += (poistumisaika-saapumisaika);
		double keskiarvo = sum/id;
		System.out.println("Asiakkaiden läpimenoaikojen keskiarvo tähän asti "+ keskiarvo);
	}

}
