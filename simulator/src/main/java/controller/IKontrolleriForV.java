package controller;


public interface IKontrolleriForV {

    //  ALSO MIGHT BE DELETED OR REWORKED

    double getTotalEarnings();
    int getServedRegularCars();
    int getServedElectricCars();
    int getRejectedCustomers();

    void setTotalEarnings(double earnings);
    void setServedRegularCars(int count);
    void setServedElectricCars(int count);
    void setRejectedCustomers(int count);

}