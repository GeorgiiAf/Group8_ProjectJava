package dao;

import datasource.MariaDbJpaConnection;
import entity.SimulationResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

/**
 * Data Access Object (DAO) for managing simulation results.
 */
public class SimulationResultDao {

    /**
     * Saves a simulation result to the database.
     *
     * @param result the simulation result to save
     */
    public void saveSimulationResult(SimulationResult result) {
        EntityManager em = MariaDbJpaConnection.getInstance();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(result);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all simulation results from the database.
     *
     * @return a list of all simulation results
     */
    public List<SimulationResult> getAllSimulationResults() {
        EntityManager em = MariaDbJpaConnection.getInstance();
        return em.createQuery("SELECT r FROM SimulationResult r ORDER BY r.simulationTime DESC", SimulationResult.class)
                .getResultList();
    }
}