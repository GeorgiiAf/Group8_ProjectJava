package datasource;

import jakarta.persistence.*;

/**
 * A singleton class that provides a connection to a MariaDB database using JPA.
 */

/*   CREATE DATABASE simulation_results_db;
GRANT ALL PRIVILEGES ON simulation_results_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
 */

public class MariaDbJpaConnection {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;

    public static synchronized EntityManager getInstance() {

        if (em==null) {
            if (emf==null) {
                emf = Persistence.createEntityManagerFactory("SimulationResult");
            }
            em = emf.createEntityManager();
        }
        return em;
    }
}