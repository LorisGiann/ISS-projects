package it.unibo.radarSystem22.domain.mock;

import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;

public class SonarMock extends SonarModel implements ISonar {
	
	private long startTime = -1;
	
	@Override
	protected void sonarSetUp() {
		curVal = new Distance(90);
		ColorsOut.out("SonarMock | sonarSetUp curVal="+curVal);
	}

	@Override
	protected void sonarProduce() {
		if( DomainSystemConfig.testing ) {	//produces always the same value
			updateDistance( DomainSystemConfig.testingDistance );
		}else {
			if(startTime<0)startTime=System.currentTimeMillis();
			long interval = (System.currentTimeMillis()-startTime)/DomainSystemConfig.sonarDelay;
			if(interval<90) {
				updateDistance((int)(90-interval));
			}else {
				updateDistance(0);
			}
		}
	}

}
