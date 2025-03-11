package simu.model;

import simu.framework.*;


public class Asiakas {
	private double saapumisaika;
	private double poistumisaika;
	private final int id;
	public static int i = 1;
	private static double sum = 0;

	private final boolean isElectricCar;
	private final boolean isNoPartsNeeded;

	private double defaultCost;
	private double electricCost;
	private double partsCost;

	private int currentQueueIndex; //Where this customer is

	public Asiakas(boolean isElectricCar, boolean isNoPartsNeeded, double defaultCost, double electricCost, double partsCost) {
        this.isElectricCar = isElectricCar;
		this.isNoPartsNeeded = isNoPartsNeeded;
		this.defaultCost = defaultCost;
		this.electricCost = electricCost;
		this.partsCost = partsCost;
        id = i++;
	    
		saapumisaika = Kello.getInstance().getAika();
		Trace.out(Trace.Level.INFO, "Uusi asiakas nro " + id + " saapui klo "+saapumisaika);
	}

	public int getCurrentQueueIndex() {
		return currentQueueIndex;
	}

	public void setCurrentQueueIndex(int currentQueueIndex) {
		this.currentQueueIndex = currentQueueIndex;
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

	public boolean isElectricCar() {
		return this.isElectricCar;
	}

	public boolean isNoPartsNeeded() {
		return this.isNoPartsNeeded;
	}

	public int getId() {
		return id;
	}
	
	public void raportti(){
		Trace.out(Trace.Level.INFO, "\nAsiakas "+id+ " valmis! ");
		Trace.out(Trace.Level.INFO, "Asiakas "+id+ " saapui: " +saapumisaika);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " poistui: " +poistumisaika);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " viipyi: " +(poistumisaika-saapumisaika));
		sum += (poistumisaika-saapumisaika);
		double keskiarvo = sum/id;
		System.out.println("Asiakkaiden läpimenoaikojen keskiarvo tähän asti "+ keskiarvo);
	}

public double calculateServiceCost() {
    double baseCost = 0.0;

    if (isElectricCar()) {
        baseCost += electricCost;
    }
	else {
		baseCost += defaultCost;
	}

    if (!isNoPartsNeeded()) {
        baseCost += partsCost;
    }

    double timeSpent = getPoistumisaika() - getSaapumisaika();
    baseCost += timeSpent * 2.0;

    return baseCost;

	}
	public boolean isServiceCompleted() {
		return poistumisaika > 0;
	}
}
