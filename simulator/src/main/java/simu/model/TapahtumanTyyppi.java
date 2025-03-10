package simu.model;

import simu.framework.ITapahtumanTyyppi;

public enum TapahtumanTyyppi implements ITapahtumanTyyppi{
	CAR_ARRIVES, DIAGNOSTIC_DONE, PARTS_ORDERED, CAR_READY, SIMPLE_MAINTENANCE, ARRIVAL, WAITING_FOR_PARTS
}
