package simu.model;

import simu.framework.ITapahtumanTyyppi;

/**
 * Enum representing different types of events in the simulation.
 */
public enum TapahtumanTyyppi implements ITapahtumanTyyppi {
	/**
	 * Event type for when a car arrives.
	 */
	CAR_ARRIVES,

	/**
	 * Event type for when diagnostics are done.
	 */
	DIAGNOSTIC_DONE,

	/**
	 * Event type for when parts are ordered.
	 */
	PARTS_ORDERED,

	/**
	 * Event type for when a car is ready.
	 */
	CAR_READY,

	/**
	 * Event type for simple maintenance.
	 */
	SIMPLE_MAINTENANCE,

	/**
	 * Event type for arrival.
	 */
	ARRIVAL,

	/**
	 * Event type for waiting for parts.
	 */
	WAITING_FOR_PARTS
}