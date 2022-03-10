package it.unibo.radarSystem22.domain.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.mock.LedMock;
import it.unibo.radarSystem22.domain.mock.SonarMock;
import it.unibo.radarSystem22.domain.utils.ColorsOut;

public class TestSonarMock {

		private ISonar sonar;
		private long startTime;
		
		private static final int TOLLERANCE_MS = 20;
		
		@Before
		public void init() {
			sonar = new SonarMock();
			startTime = System.currentTimeMillis();
			sonar.activate();
			System.out.println("Up");
		}
		
		@After
		public void teardown() {
			sonar.deactivate();
			System.out.println("Down");
		}
		
		@Test
		public void test() {
			int d = sonar.getDistance().getVal();
			long currentTime = System.currentTimeMillis();
			int nTests = 0;
			assertEquals(90,d); //make sure we start from the beginning
			while (d > 0) {
				//ColorsOut.out("SonarConsumerForTesting | d=" + d);
				d = sonar.getDistance().getVal();
				currentTime = System.currentTimeMillis();
				//calculate the interval we are currently in, and see if the distance value is compatible with that
				long interval = (currentTime-startTime)/SonarMock.changeTimeInterval;
				int expectedDistance = (interval<90) ? (int)(90-interval) : 0;
				int msFromIntervalStart = (int) ((currentTime-startTime)%SonarMock.changeTimeInterval);
				int msFromIntervalEnd = SonarMock.changeTimeInterval-msFromIntervalStart;
				//just some sanity check on the test algorithm
				assertTrue("was "+expectedDistance,expectedDistance>=0);
				assertTrue("was "+msFromIntervalStart,msFromIntervalStart>=0);
				assertTrue("was "+msFromIntervalEnd,msFromIntervalEnd>=0);
				
				int minDistance = expectedDistance;
				int maxDistance = expectedDistance;
				if(msFromIntervalStart<=TOLLERANCE_MS) maxDistance+=1;
				if(msFromIntervalEnd<=TOLLERANCE_MS) minDistance-=1;
				assertTrue(minDistance+" <= d("+d+") <= "+maxDistance,d>=minDistance&&d<=maxDistance);
				nTests++;
			}
			assertEquals(0,d);
			assertTrue("not enought tests",nTests>10);
		}
		

}
