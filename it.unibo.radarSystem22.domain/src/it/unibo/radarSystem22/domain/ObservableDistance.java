package it.unibo.radarSystem22.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.IObservableDistance;
import it.unibo.radarSystem22.domain.interfaces.IObserver;

public class ObservableDistance implements IObservableDistance {

	protected IDistance d;
	protected Set<IObserver> observers = new HashSet<IObserver>();
	
	public ObservableDistance(IDistance d) {
		this.d = d;
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
	public void update(IDistance newVal) {
		d = newVal;
		for(IObserver observer : observers) observer.update(d.toString());
	}

	@Override
	public IDistance getDistance() {
		return d;
	}

}
