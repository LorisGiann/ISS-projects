package it.unibo.radarSystem22.domain.models;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unibo.radarSystem22.domain.interfaces.IObserver;
import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_traditional;
import it.unibo.radarSystem22.domain.utils.ColorsOut;

public abstract class SonarModelObservable_traditional extends SonarModel implements ISonarObservable_traditional {

	private static final int DELTA_UPDATE = 0; //new value is considered only if the difference with the already registered one is greter than this value. 0 means updated only if changed by one or grater

	protected Set<IObserver> observers = new HashSet<IObserver>();
	
	@Override
	public void activate() {
		for(IObserver observer : observers) observer.update(curVal.toString());
		super.activate();
	}
	
	@Override
	public void register(IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregister(IObserver observer) {
		observers.remove(observer);
	}
	
	@Override
	protected void updateDistance( int d ) { //called by concrete and mock objects on new measure
		if(Math.abs(curVal.getVal()-d)>DELTA_UPDATE) {
			curVal = new Distance( d ); //immutable values, no need for syncronization
			ColorsOut.out("SonarModel | updateDistance "+ d, ColorsOut.BLUE);
			for(IObserver observer : observers) observer.update(curVal.toString());
		}
	}	

}
