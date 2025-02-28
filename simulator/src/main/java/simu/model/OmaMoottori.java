package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

import java.util.*;

public class OmaMoottori extends Moottori{
	
	private Saapumisprosessi saapumisprosessi;
	private List<Asiakas> customers = new ArrayList<>();

	ArrayList<Palvelupiste> arrival = new ArrayList<>();
	ArrayList<Palvelupiste> diagnostics = new ArrayList<>();
	ArrayList<Palvelupiste> parts = new ArrayList<>();
	ArrayList<Palvelupiste> carReady = new ArrayList<>();

	ArrayList<ArrayList<Palvelupiste>> allServicePoints = new ArrayList<>();

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

		saapumisprosessi = new Saapumisprosessi(new Negexp(3), tapahtumalista, TapahtumanTyyppi.CAR_ARRIVES);


	}

    private ArrayList<Palvelupiste> createPalvelupiste(int amount, int enginners, ContinuousGenerator serviceTime, TapahtumanTyyppi tapahtumanTyyppi){
		ArrayList<Palvelupiste> servicePoints = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			servicePoints.add(new Palvelupiste(enginners, serviceTime, tapahtumalista, tapahtumanTyyppi));
		}
		return servicePoints;
	}
	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(new newCustomer(25.0, 10.0).buildCustomer());
		this.arrival.clear();
		this.diagnostics.clear();
		this.parts.clear();
		this.carReady.clear();

		this.arrival.addAll(createPalvelupiste(15, 10, new Normal(10, 2), TapahtumanTyyppi.CAR_ARRIVES));
		this.diagnostics.addAll(createPalvelupiste(10, 5, new Normal(30, 10), TapahtumanTyyppi.DIAGNOSTIC_DONE));
		this.parts.addAll(createPalvelupiste(4, 5, new Normal(60, 30), TapahtumanTyyppi.PARTS_ORDERED));
		this.carReady.addAll(createPalvelupiste(10, 20, new Normal(15, 2), TapahtumanTyyppi.CAR_READY));

		this.allServicePoints.add(arrival);
		this.allServicePoints.add(diagnostics);
		this.allServicePoints.add(parts);
		this.allServicePoints.add(carReady);

	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
		switch ((TapahtumanTyyppi) t.getTyyppi()) {
			case CAR_ARRIVES:
				handleArrival(t);
				break;

			case DIAGNOSTIC_DONE:
				handleDiagnostics(t);
				break;

				//TODO

			case PARTS_ORDERED:
				handleParts(t);
				break;

//			case WAITING_FOR_PARTS:
//				handleParts(t);
//				break;

			case CAR_READY:
				Trace.out(Trace.Level.INFO, "CAR READY");
				handleCarReady(t);
				break;
		}
	}

	private void handleArrival(Tapahtuma t) {
		Asiakas uusiAsiakas =
				new newCustomer(25.0, 10.0)
					.buildCustomer();
		customers.add(uusiAsiakas);
//		if (uusiAsiakas.isElectricCar()) {
//			shortestQueuePalvelupiste = palvelupisteet[2];
//		} else {
//			// Assign regular cars to the shortest queue
//			shortestQueuePalvelupiste = shortestQueue(palvelupisteet);
//		}

		Palvelupiste q = shortestQueue(this.diagnostics);
		q.lisaaJonoon(uusiAsiakas);
		uusiAsiakas.setCurrentQueueIndex(this.diagnostics.indexOf(q));

		saapumisprosessi.generoiSeuraava(uusiAsiakas);
	}

	private void handleDiagnostics(Tapahtuma t) {
		Asiakas a = diagnostics.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " diagnostics complete. Needs parts: " + !a.isNoPartsNeeded());

		if (a.isNoPartsNeeded()) {

			Palvelupiste q = shortestQueue(this.carReady);
			q.lisaaJonoon(a);
			a.setCurrentQueueIndex(this.carReady.indexOf(q));
			Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " moved to CAR_READY queue.");
		} else {

			Palvelupiste q = shortestQueue(this.parts);
			q.lisaaJonoon(a);
			a.setCurrentQueueIndex(this.parts.indexOf(q));
			Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " moved to PARTS_ORDERED queue. Queue size: " + q.getQueueSize());
		}
	}

	private void handleParts(Tapahtuma t) {
		Asiakas c = this.parts.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();

		Palvelupiste q = shortestQueue(this.carReady);
		q.lisaaJonoon(c);

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " parts ordering complete.");

		c.setCurrentQueueIndex(this.carReady.indexOf(q));

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " moved to CAR_READY queue. Queue size: " + q.getQueueSize());

	}

	private void handleCarReady(Tapahtuma t) {
		Asiakas a = this.carReady.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		a.setPoistumisaika(Kello.getInstance().getAika());
		a.raportti();
	}

	public Palvelupiste shortestQueue(ArrayList<Palvelupiste> servicePointArr) {
		if (servicePointArr.isEmpty()) {
			throw new IllegalStateException("No service points available in the list???");
		}
		Palvelupiste shortest = servicePointArr.getFirst();
		int minQueueSize = shortest.getQueueSize();

		for (int i = 1; i < servicePointArr.size(); i++) {
			Palvelupiste current = servicePointArr.get(i);
			int currentQueueSize = current.getQueueSize();
			if (currentQueueSize < minQueueSize) {
				shortest = current;
				minQueueSize = currentQueueSize;
			}
		}

		return shortest;
	}

	@Override
	protected void yritaCTapahtumat(){
		for (ArrayList<Palvelupiste> servicePointList : allServicePoints) {
			for (Palvelupiste p : servicePointList) {
				if (!p.onVarattu() && p.onJonossa()) {

					p.yritaAloitaPalvelu();
				}
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
