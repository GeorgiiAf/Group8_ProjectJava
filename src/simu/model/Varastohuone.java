package simu.model;

public class Varastohuone {
    private int id;
    private double koko;
    private boolean varattu;

    public Varastohuone(int id, double koko) {
        this.id = id;
        this.koko = koko;
        this.varattu = false;
    }

    public int getId() {
        return id;
    }

    public double getKoko() {
        return koko;
    }

    public boolean isVarattu() {
        return varattu;
    }

    public void setVarattu(boolean varattu) {
        this.varattu = varattu;
    }
}
