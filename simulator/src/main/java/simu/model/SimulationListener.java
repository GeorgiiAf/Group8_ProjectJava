package simu.model;

/**
 * Interface for listening to simulation events.
 */
public interface SimulationListener {

    /**
     * Called when a log message is generated in the simulation.
     *
     * @param message the log message
     */
    void onLogMessage(String message);
}