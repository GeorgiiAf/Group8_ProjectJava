package simu.model;

import simu.framework.ITapahtumanTyyppi;

// TODO:
// Tapahtumien tyypit määritellään simulointimallin vaatimusten perusteella
public enum TapahtumanTyyppi implements ITapahtumanTyyppi{
	ORDER_RECEIVED,
	CHECK_AVAILABILITY,
	RESERVE_STORAGE,
	PAYMENT,
	ORDER_COMPLETED;

}
