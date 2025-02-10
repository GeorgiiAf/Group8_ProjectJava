package simu.model;

public class Varasto {
    private Varastohuone[] varastohuoneet;

    public double getUsageRate() {
        int occupied = 0;
        for (Varastohuone vh : varastohuoneet) {
            if (vh.isVarattu()) {
                occupied++;
            }
        }
        return (double) occupied / varastohuoneet.length * 100;
    }

    public int getMaxWaitTime() {
        return 0;
    }
}
