package it.unibo.radarSystem22.domain.models;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.ObservableDistance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.IObservableDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_lambda;
import it.unibo.radarSystem22.domain.interfaces.ISonar_observableDistance;
import it.unibo.radarSystem22.domain.utils.ColorsOut;

public abstract class SonarModel_observableDistance extends SonarModel implements ISonar_observableDistance {

	private static final int DELTA_UPDATE = 0; //new value is considered only if the difference with the already registered one is greter than this value. 0 means updated only if changed by one or grater
	
	IObservableDistance val;
	
	public SonarModel_observableDistance() {
		val = new ObservableDistance(curVal);
	}
	
	@Override
	public void activate() {
		val.update(curVal);
		super.activate();
	}
	
	@Override
	protected void updateDistance( int d ) { //called by concrete and mock objects on new measure
		if(Math.abs(curVal.getVal()-d)>DELTA_UPDATE) {
			curVal = new Distance( d ); //immutable values, no need for syncronization
			ColorsOut.out("SonarModel | updateDistance "+ d, ColorsOut.BLUE);
			val.update(curVal);
		}
	}
	
	@Override
	public IObservableDistance getObservableDistance() {
		return val;
	}

}
