package it.unibo.radarSystem22.domain.interfaces;

import java.util.function.Consumer;

public interface IObservableDistance {
	void register( IObserver obs );
	void unregister( IObserver obs );
	void update(IDistance newVal);
	IDistance getDistance();
}
