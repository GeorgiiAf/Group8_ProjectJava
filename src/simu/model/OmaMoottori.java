package simu.model;

import simu.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OmaMoottori extends Moottori {

	private Saapumisprosessi saapumisprosessi;
	private Palvelupiste[] palvelupisteet;

	public OmaMoottori() {
		palvelupisteet = new Palvelupiste[4];  // Четыре этапа обслуживания

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
				palvelupisteet[0].lisaaJonoon(new Asiakas());
				saapumisprosessi.generoiSeuraava();
				break;

			case CHECK_AVAILABILITY:
				a = palvelupisteet[0].otaJonosta();
				palvelupisteet[1].lisaaJonoon(a);
				break;

			case RESERVE_STORAGE:
				a = palvelupisteet[1].otaJonosta();
				palvelupisteet[2].lisaaJonoon(a);
				break;

			case PAYMENT:
				a = palvelupisteet[2].otaJonosta();
				palvelupisteet[3].lisaaJonoon(a);
				break;

			case ORDER_COMPLETED:
				a = palvelupisteet[3].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika());
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
}
