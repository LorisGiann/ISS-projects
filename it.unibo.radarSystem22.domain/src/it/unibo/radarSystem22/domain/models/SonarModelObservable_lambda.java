package it.unibo.radarSystem22.domain.models;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_lambda;
import it.unibo.radarSystem22.domain.utils.ColorsOut;

public abstract class SonarModelObservable_lambda extends SonarModel implements ISonarObservable_lambda {

	private static final int DELTA_UPDATE = 0; //new value is considered only if the difference with the already registered one is greter than this value. 0 means updated only if changed by one or grater

	protected Set<Consumer<IDistance>> callbacks = new HashSet<Consumer<IDistance>>();
	
	@Override
	public void activate() {
		for(Consumer<IDistance> callback : callbacks) callback.accept(curVal);
		super.activate();
	}
	
	@Override
	public void register(Consumer<IDistance> callback) {
		callbacks.add(callback);
	}

	@Override
	public void unregister(Consumer<IDistance> callback) {
		callbacks.remove(callback);
	}
	
	@Override
	protected void updateDistance( int d ) { //called by concrete and mock objects on new measure
		if(Math.abs(curVal.getVal()-d)>DELTA_UPDATE) {
			curVal = new Distance( d ); //immutable values, no need for syncronization
			ColorsOut.out("SonarModel | updateDistance "+ d, ColorsOut.BLUE);
			for(Consumer<IDistance> callback : callbacks) callback.accept(curVal);
		}
	}	

}
