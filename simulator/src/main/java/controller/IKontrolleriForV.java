package controller;


public interface IKontrolleriForV {

    double getTotalEarnings();
    int getServedRegularCars();
    int getServedElectricCars();
    int getRejectedCustomers();

    void setTotalEarnings(double earnings);
    void setServedRegularCars(int count);
    void setServedElectricCars(int count);
    void setRejectedCustomers(int count);

    void kaynnistaSimulointi();
    void nopeuta();
    void hidasta();
}