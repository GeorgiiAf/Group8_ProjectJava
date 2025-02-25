package controller;

public interface IKontrolleriForV {

    // Rajapinta, joka tarjotaan  käyttöliittymälle:

    public void kaynnistaSimulointi();
    public void nopeuta();
    public void hidasta();
    double getTotalEarnings();
    int getServedRegularCars();
    int getServedElectricCars();
    int getRejectedCustomers();
}
