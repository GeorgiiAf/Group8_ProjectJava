package simu.model;

import simu.framework.*;


public class Asiakas {
	private double saapumisaika;
	private double poistumisaika;
	private int id;
	private static int i = 1;
	private static double sum = 0;

	private final boolean isElectricCar;
	private final boolean isNoPartsNeeded;

	private int currentQueueIndex; //Where this customer is

	public Asiakas(boolean isElectricCar, boolean isNoPartsNeeded) {
        this.isElectricCar = isElectricCar;
		this.isNoPartsNeeded = isNoPartsNeeded;
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
    double baseCost = 100.0; // Базовая стоимость

    // Если машина электрическая, стоимость выше
    if (isElectricCar()) {
        baseCost += 50.0;
    }

    // Если были заказаны запчасти, добавляем стоимость
    if (!isNoPartsNeeded()) {
        baseCost += 200.0;
    }

    // Время пребывания также влияет на стоимость
    double timeSpent = getPoistumisaika() - getSaapumisaika();
    baseCost += timeSpent * 2.0;

    return baseCost;
}
}
