package simu.model;

import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OmaMoottori extends Moottori {

	private Saapumisprosessi saapumisprosessi;
	private Palvelupiste[] palvelupisteet;

	public OmaMoottori() {
		palvelupisteet = new Palvelupiste[4];

		palvelupisteet[0] = new Palvelupiste(new Normal(10, 5), tapahtumalista, TapahtumanTyyppi.ORDER_RECEIVED);
		palvelupisteet[1] = new Palvelupiste(new Normal(15, 7), tapahtumalista, TapahtumanTyyppi.CHECK_AVAILABILITY);
		palvelupisteet[2] = new Palvelupiste(new Normal(8, 4), tapahtumalista, TapahtumanTyyppi.RESERVE_STORAGE);
		palvelupisteet[3] = new Palvelupiste(new Normal(12, 6), tapahtumalista, TapahtumanTyyppi.PAYMENT);

		saapumisprosessi = new Saapumisprosessi(new Negexp(20, 10), tapahtumalista, TapahtumanTyyppi.ORDER_RECEIVED);
	}

	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava();
	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
		Asiakas a;
		switch ((TapahtumanTyyppi) t.getTyyppi()) {

			case ORDER_RECEIVED:
				a = new Asiakas();
				palvelupisteet[0].lisaaJonoon(a);
				saapumisprosessi.generoiSeuraava();
				break;

			case CHECK_AVAILABILITY:
				a = (Asiakas) palvelupisteet[0].otaJonosta();
				if (isStorageAvailable()) {
					palvelupisteet[1].lisaaJonoon(a);
				} else {
					putInQueueForWaiting(a);
				}
				break;

			case RESERVE_STORAGE:
				a = (Asiakas) palvelupisteet[1].otaJonosta();
				reserveStorageForCustomer(a);
				palvelupisteet[2].lisaaJonoon(a);
				break;

			case PAYMENT:
				a = (Asiakas) palvelupisteet[2].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika()); // Записываем время ухода клиента
				a.raportti();
				break;
		}
	}

	@Override
	protected void yritaCTapahtumat() {
		for (Palvelupiste p : palvelupisteet) {
			if (!p.onVarattu() && p.onJonossa()) {
				p.aloitaPalvelu();
			}
		}
	}

	@Override
	protected void tulokset() {
		System.out.println("Simulointi päättyi kello " + Kello.getInstance().getAika());
		System.out.println("Tulokset ... puuttuvat vielä");
	}


	private boolean isStorageAvailable() {
		return Math.random() > 0.5;
	}

	private void putInQueueForWaiting(Asiakas a) {
		Trace.out(Trace.Level.INFO, "Asiakas " + a.getId() + " joutui jonoon odottamaan.");
	}

	private void reserveStorageForCustomer(Asiakas a) {
		Trace.out(Trace.Level.INFO, "Asiakas " + a.getId() + " varasi varastotilan.");
	}



}
