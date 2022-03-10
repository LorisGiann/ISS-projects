package it.unibo.radarSystem22.domain.mock;

import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.ColorsOut;

public class SonarMock extends SonarModel implements ISonar {

	public final static int changeTimeInterval = 50; //ms
	private long startTime;
	
	@Override
	protected void sonarSetUp() {
		curVal = new Distance(90);
		ColorsOut.out("SonarMock | sonarSetUp curVal="+curVal);
		startTime=System.currentTimeMillis();
	}

	@Override
	protected void sonarProduce() {
		long interval = (System.currentTimeMillis()-startTime)/changeTimeInterval;
		if(interval<90) {
			updateDistance((int)(90-interval));
		}else {
			updateDistance(0);
		}
	}

}
