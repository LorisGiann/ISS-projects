package it.unibo.radarSystem22.domain.interfaces;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ISonarObservable_lambda  extends ISonar{
	  //void register( IObserver obs );
	  //void unregister( IObserver obs );
	void register( Consumer<IDistance> callback );
	void unregister( Consumer<IDistance> callback );
}
