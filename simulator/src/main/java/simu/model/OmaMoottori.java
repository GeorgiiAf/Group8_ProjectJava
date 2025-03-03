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
	ArrayList<Palvelupiste> simpleMaintenance = new ArrayList<>();

	ArrayList<ArrayList<Palvelupiste>> allServicePoints = new ArrayList<>();

	private double totalEarnings = 0.0;
	private int servedRegularCars = 0;
	private int servedElectricCars = 0;
	private int rejectedCustomers = 0;

	private boolean simulointiLoppu = false;
	private double simulointiaika;
	private long viive;

	public OmaMoottori(){

		saapumisprosessi = new Saapumisprosessi(new Negexp(3), tapahtumalista, TapahtumanTyyppi.CAR_ARRIVES);


	}


	public ArrayList<ArrayList<Palvelupiste>> getAllServicePointsList() {
		return allServicePoints;
	}


	private ArrayList<Palvelupiste> createPalvelupiste(int amount, int mechanics, ContinuousGenerator serviceTime, TapahtumanTyyppi tapahtumanTyyppi){
		ArrayList<Palvelupiste> servicePoints = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			servicePoints.add(new Palvelupiste(mechanics, serviceTime, tapahtumalista, tapahtumanTyyppi));
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
		this.simpleMaintenance.clear();

		this.arrival.addAll(createPalvelupiste(15, 10, new Normal(10, 2), TapahtumanTyyppi.CAR_ARRIVES));
		this.diagnostics.addAll(createPalvelupiste(10, 5, new Normal(30, 10), TapahtumanTyyppi.DIAGNOSTIC_DONE));
		this.parts.addAll(createPalvelupiste(4, 5, new Normal(60, 30), TapahtumanTyyppi.PARTS_ORDERED));
		this.simpleMaintenance.addAll(createPalvelupiste(10, 10, new Normal(30, 15), TapahtumanTyyppi.SIMPLE_MAINTENANCE));
		this.carReady.addAll(createPalvelupiste(10, 5, new Normal(15, 2), TapahtumanTyyppi.CAR_READY));

		this.allServicePoints.add(arrival);
		this.allServicePoints.add(diagnostics);
		this.allServicePoints.add(parts);
		this.allServicePoints.add(simpleMaintenance);
		this.allServicePoints.add(carReady);

	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
		if (simulointiLoppu) {
			return;
		}
		switch ((TapahtumanTyyppi) t.getTyyppi()) {
			case CAR_ARRIVES:
				handleArrival(t);
				break;

			case DIAGNOSTIC_DONE:
				handleDiagnostics(t);
				break;

			case PARTS_ORDERED:
				handleParts(t);
				break;

			case SIMPLE_MAINTENANCE:
				handleMaintenance(t);
				break;

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

		Palvelupiste q = shortestQueue(this.diagnostics);
		q.lisaaJonoon(uusiAsiakas);
		uusiAsiakas.setCurrentQueueIndex(this.diagnostics.indexOf(q));

		saapumisprosessi.generoiSeuraava(uusiAsiakas);
	}

	private void handleDiagnostics(Tapahtuma t) {
		Asiakas a = diagnostics.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " diagnostics complete. Needs parts: " + !a.isNoPartsNeeded());

		if (a.isNoPartsNeeded()) {

			Palvelupiste q = shortestQueue(this.simpleMaintenance);
			q.lisaaJonoon(a);
			a.setCurrentQueueIndex(this.simpleMaintenance.indexOf(q));
			Trace.out(Trace.Level.INFO, "Customer " + a.getId() + " moved to SIMPLE_MAINTENANCE queue.");
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

	private void handleMaintenance(Tapahtuma t) {
		Asiakas c = this.simpleMaintenance.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();

		Palvelupiste q = shortestQueue(this.carReady);
		q.lisaaJonoon(c);

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " simple maintenance complete.");

		c.setCurrentQueueIndex(this.carReady.indexOf(q));

		Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " moved to CAR_READY queue. Queue size: " + q.getQueueSize());
	}

	private void handleCarReady(Tapahtuma t) {
		Asiakas a = this.carReady.get(t.getAsiakas().getCurrentQueueIndex()).otaJonosta();
		a.setPoistumisaika(Kello.getInstance().getAika());

		totalEarnings += a.calculateServiceCost();
		if (a.isElectricCar()) {
			servedElectricCars++;
		} else {
			servedRegularCars++;
		}

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

					p.aloitaPalvelu();
				}
			}
		}
	}

	@Override
	protected void tulokset() {
		System.out.println("Simulointi päättyi kello " + Kello.getInstance().getAika());
		System.out.println("Total earnings: " + totalEarnings);
		System.out.println("Served regular cars: " + servedRegularCars);
		System.out.println("Served electric cars: " + servedElectricCars);
		System.out.println("Rejected customers: " + rejectedCustomers);
	}

	public double calculateTotalEarnings() {
		return totalEarnings;
	}

	public int getServedRegularCars() {
		return servedRegularCars;
	}

	public int getServedElectricCars() {
		return servedElectricCars;
	}

	public int getRejectedCustomers() {
		return rejectedCustomers;
	}


	public void setSimulointiLoppu(boolean lopeta) {
		this.simulointiLoppu = lopeta;
	}
	public void setSimulointiAika(double aika) {
		if (aika <= 0) {
			throw new IllegalArgumentException("Simulation time must be greater than 0");
		}
		this.simulointiaika = aika;
	}


	public void setViive(long viive) {
		if (viive < 0) {
			throw new IllegalArgumentException("Delay must be non-negative");
		}
		this.viive = viive;
	}

	public long getViive() {
		return viive;
	}
}
