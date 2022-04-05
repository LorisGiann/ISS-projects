package it.unibo.radarSystem22.domain.mock;

import java.util.function.Consumer;

import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_lambda;
import it.unibo.radarSystem22.domain.interfaces.ISonar_observableDistance;
import it.unibo.radarSystem22.domain.models.SonarModelObservable_lambda;
import it.unibo.radarSystem22.domain.models.SonarModel_observableDistance;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;

public class SonarMock_observableDistance extends SonarModel_observableDistance implements ISonar_observableDistance {
//same code of SonarMockObservable_traditional
	
	private long startTime = -1;
	
	public SonarMock_observableDistance() {
		curVal = new Distance(90);
	}
	
	/*@Override
	protected void sonarSetUp() {
		curVal = new Distance(90);
		ColorsOut.out("SonarMock | sonarSetUp curVal="+curVal);
	}*/

	@Override
	protected void sonarProduce() {
		if( DomainSystemConfig.testing ) {	//produces always the same value
			updateDistance( DomainSystemConfig.testingDistance );
		}else{
			if(startTime<0)startTime=System.currentTimeMillis();
			boolean interval = (System.currentTimeMillis()-startTime)>DomainSystemConfig.sonarDelay;
			if(interval) {
				updateDistance(100);
			}else {
				updateDistance(curVal.getVal());
			}
		}
	}

}
