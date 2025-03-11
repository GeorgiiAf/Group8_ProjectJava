package simu.framework;			// I DKN  REWORK/DELETE

	public abstract class Moottori {

		private double simulointiaika = 0;

		private Kello kello;

		protected Tapahtumalista tapahtumalista;
		private long viive = 100;
		private boolean paused = false;
		private boolean stopped = false;



	public Moottori(){

		kello = Kello.getInstance(); // Otetaan kello muuttujaan yksinkertaistamaan koodia
		
		tapahtumalista = new Tapahtumalista();
		
		// Palvelupisteet luodaan simu.model-pakkauksessa Moottorin aliluokassa 
		
		
	}


	public void setSimulointiaika(double aika) {
		simulointiaika = aika;
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

	public void setPause() {
		paused = !paused;
		if (!paused) {
			synchronized (this) {
				notify();
			}
		}
	}


	public void aja(){
		alustukset(); // luodaan mm. ensimmäinen tapahtuma
		while (simuloidaan() && !stopped){
			
			Trace.out(Trace.Level.INFO, "\nA-vaihe: kello on " + nykyaika());
			kello.setAika(nykyaika());
			
			Trace.out(Trace.Level.INFO, "\nB-vaihe:" );
			suoritaBTapahtumat();
			
			Trace.out(Trace.Level.INFO, "\nC-vaihe:" );
			yritaCTapahtumat();

			synchronized (this) {
				try {
					if(paused){
						wait();
					}
					Thread.sleep(viive > 0 ? viive : 100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

			}
		}
		tulokset();
		kello.resetAika();
	}
	
	private void suoritaBTapahtumat(){
		while (tapahtumalista.getSeuraavanAika() == kello.getAika()){
			suoritaTapahtuma(tapahtumalista.poista());
		}
	}

	private double nykyaika(){
		return tapahtumalista.getSeuraavanAika();
	}
	
	private boolean simuloidaan(){
		return kello.getAika() < simulointiaika;
	}

	public boolean stopSimulation(){
		return stopped = !stopped;
	}

	protected abstract void suoritaTapahtuma(Tapahtuma t);  // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	protected abstract void yritaCTapahtumat();	// Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void alustukset(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void tulokset(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa



	public Kello getKello() {
			return Kello.getInstance();
		}
}