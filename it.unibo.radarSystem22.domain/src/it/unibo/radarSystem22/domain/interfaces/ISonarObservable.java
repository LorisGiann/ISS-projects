package it.unibo.radarSystem22.domain.interfaces;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ISonarObservable  extends ISonar{
	  //void register( IObserver obs );
	  //void unregister( IObserver obs );
	void registerForDistance( Consumer<IDistance> callback );
	void unregisterForDistance( Consumer<IDistance> callback );
}
