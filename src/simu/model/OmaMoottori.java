package simu.model;

import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

import java.util.*;

public class OmaMoottori extends Moottori{
	
	private Saapumisprosessi saapumisprosessi;
	private Palvelupiste[] palvelupisteet;
	private List<Asiakas> customers = new ArrayList<>();

/*
	private final int totalSpots;
	private final int electricSpots;
	private int availableSpots;
	private int availableElectricSpots;
	private Queue<Asiakas> regularQueue = new LinkedList<>();
	private Queue<Asiakas> electricQueue = new LinkedList<>();
*/

	public OmaMoottori(){

	//	this.totalSpots = 25;
	//	this.electricSpots = 4;

		palvelupisteet = new Palvelupiste[3];

		palvelupisteet[0]=new Palvelupiste(new Normal(10,6), tapahtumalista, TapahtumanTyyppi.DIAGNOSTIC_DONE);
		palvelupisteet[1]=new Palvelupiste(new Normal(10,10), tapahtumalista, TapahtumanTyyppi.PARTS_ORDERED);
		palvelupisteet[2]=new Palvelupiste(new Normal(5,3), tapahtumalista, TapahtumanTyyppi.CAR_READY);

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
				handleArrival(t);
				break;

			case DIAGNOSTIC_DONE:
				handleDiagnostics(t);
				break;

			case PARTS_ORDERED:
				a = palvelupisteet[1].otaJonosta();
				palvelupisteet[2].lisaaJonoon(a);
				break;

			case WAITING_FOR_PARTS:
				handleParts(t);
				break;

			case CAR_READY:
				a = palvelupisteet[2].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika());
				a.raportti();
				break;
		}
	}

	private void handleArrival(Tapahtuma t) {
		Asiakas uusiAsiakas =
				new newCustomer(25.0, 10.0)
					.buildCustomer();
		palvelupisteet[0].lisaaJonoon(uusiAsiakas);
		Palvelupiste p;
		if (uusiAsiakas.isElectricCar()) {
			//TODO logic
		} else {

		}

		saapumisprosessi.generoiSeuraava();

	}

	private void handleDiagnostics(Tapahtuma t) {
		Asiakas a = palvelupisteet[0].otaJonosta();

		if(a.isNoPartsNeeded()){
			palvelupisteet[2].lisaaJonoon(a);
		}
		else{
			palvelupisteet[1].lisaaJonoon(a);
		}
	}

	private void handleParts(Tapahtuma t) {
		//FIXME move asiakas from diagnostics. asiakas must be available
		Asiakas c = palvelupisteet[0].otaJonosta();

		//TODO rest of the process
		Palvelupiste p = palvelupisteet[1];
		p.lisaaJonoon(c);

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

	
}
