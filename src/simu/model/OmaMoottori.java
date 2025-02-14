package simu.model;

import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

import java.util.LinkedList;
import java.util.Queue;

public class OmaMoottori extends Moottori{
	
	private Saapumisprosessi saapumisprosessi;

	private Palvelupiste[] palvelupisteet;

	private final int totalSpots = 25;
	private final int electricSpots = 4;
	private int availableSpots = totalSpots - electricSpots;
	private int availableElectricSpots = electricSpots;


	private final Queue<Asiakas> waitingQueue = new LinkedList<>();
	private Queue<Asiakas> regularQueue = new LinkedList<>();
	private Queue<Asiakas> electricQueue = new LinkedList<>();

	public OmaMoottori(){

		palvelupisteet = new Palvelupiste[3];

		palvelupisteet[0]=new Palvelupiste(new Normal(10,6), tapahtumalista, TapahtumanTyyppi.DIAGNOSTIC_DONE,10);
		palvelupisteet[1]=new Palvelupiste(new Normal(10,10), tapahtumalista, TapahtumanTyyppi.PARTS_ORDERED,8);
		palvelupisteet[2]=new Palvelupiste(new Normal(5,3), tapahtumalista, TapahtumanTyyppi.CAR_READY,7);

		saapumisprosessi = new Saapumisprosessi(new Negexp(15,5), tapahtumalista, TapahtumanTyyppi.CAR_ARRIVES);

	}


	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(); // Ensimmäinen saapuminen järjestelmään
	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
		Asiakas a;
		switch ((TapahtumanTyyppi) t.getTyyppi()) {
			case CAR_ARRIVES:
				Asiakas uusiAsiakas = new Asiakas();
				for (Palvelupiste p : palvelupisteet) {
					if (p.hasFreeSpot()) {
						p.lisaaJonoon(uusiAsiakas);
						break;
					}
				}
				saapumisprosessi.generoiSeuraava();
				break;
			case DIAGNOSTIC_DONE:
				a = palvelupisteet[0].otaJonosta();
				palvelupisteet[1].lisaaJonoon(a);
				break;

			case PARTS_ORDERED:
				a = palvelupisteet[1].otaJonosta();
				palvelupisteet[2].lisaaJonoon(a);
				break;

			case WAITING_FOR_PARTS:
				a = (Asiakas) palvelupisteet[1].otaJonosta();
				palvelupisteet[2].lisaaJonoon(a);
				break;

			case CAR_READY:
				a = palvelupisteet[2].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika());
				a.raportti();
				break;
		}
	}

	@Override
	protected void yritaCTapahtumat(){
		for (Palvelupiste p: palvelupisteet){
			if (!p.onVarattu() && p.onJonossa()){
				p.aloitaPalvelu();
			}
		}
	}

	@Override
	protected void tulokset() {
		System.out.println("Simulointi päättyi kello " + Kello.getInstance().getAika());
		System.out.println("Tulokset ... puuttuvat vielä");
	}

	private boolean osatSaatavilla(){
		return Math.random() < 0.7;
	}

	private double generateWaitingTime(){
		return Math.random() * 5 + 1;
	}

	private void freeSpot(boolean isElectricCar) {
		if (isElectricCar) {
			availableElectricSpots++;
		} else {
			availableSpots++;
		}

		if (!waitingQueue.isEmpty()) {
			Asiakas nextCustomer = waitingQueue.poll();
			addCustomer(nextCustomer);
		}
	}


	private boolean isElectricCar(Asiakas customer) {
		return customer.getId() % 2 == 0;
	}

	private boolean addCustomer(Asiakas asiakas) {
		if (isElectricCar(asiakas) && availableElectricSpots > 0) {
			availableElectricSpots--;
			palvelupisteet[0].lisaaJonoon(asiakas);
			Trace.out(Trace.Level.INFO, "Electric car Asiakas " + asiakas.getId() + " added.");
			return true;
		} else if (!isElectricCar(asiakas) && availableSpots > 0) {
			availableSpots--;
			palvelupisteet[0].lisaaJonoon(asiakas);
			Trace.out(Trace.Level.INFO, "Regular car Asiakas " + asiakas.getId() + " added.");
			return true;
		} else {
			waitingQueue.add(asiakas);
			Trace.out(Trace.Level.INFO, "Asiakas " + asiakas.getId() + " is waiting.");
			return false;
		}
	}
}
