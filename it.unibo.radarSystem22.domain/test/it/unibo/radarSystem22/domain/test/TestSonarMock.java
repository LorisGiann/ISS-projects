package it.unibo.radarSystem22.domain.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.mock.LedMock;
import it.unibo.radarSystem22.domain.mock.SonarMock;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;

public class TestSonarMock {

		private ISonar sonar;
		private long startTime;
		
		@BeforeClass
		public static void initOnce() {
			DomainSystemConfig.simulation = true;
		}
		
		@Before
		public void init() {
			sonar = new SonarMock();
		}
		
		@After
		public void teardown() {
			sonar.deactivate();
			System.out.println("Down");
		}
		
		@Test
		public void test() {
			DomainSystemConfig.sonarDelay = 50;
			
			int CHANGE_VAL_INTERVAL_LEN = DomainSystemConfig.sonarDelay;
			int TOLLERANCE_MS = CHANGE_VAL_INTERVAL_LEN/2;
			
			sonar.activate();
			startTime = System.currentTimeMillis();
			System.out.println("Up");
			
			int d = sonar.getDistance().getVal();
			long currentTime = System.currentTimeMillis();
			int nTests = 0;
			
			assertEquals(90,d); //make sure we start from the beginning
			while (d > 0) {
				BasicUtils.delay(CHANGE_VAL_INTERVAL_LEN/5);
				d = sonar.getDistance().getVal();
				currentTime = System.currentTimeMillis();
				
				//ColorsOut.out("SonarConsumerForTesting | currentTime=" + currentTime + " d=" + d);
				
				//calculate the interval we are currently in, and see if the distance value is compatible with that
				long interval = (currentTime-startTime)/CHANGE_VAL_INTERVAL_LEN;
				int expectedDistance = (interval<90) ? (int)(90-interval) : 0;
				int msFromIntervalStart = (int) ((currentTime-startTime)%CHANGE_VAL_INTERVAL_LEN);
				int msFromIntervalEnd = CHANGE_VAL_INTERVAL_LEN-msFromIntervalStart;
				
				//just some sanity check on the testing algorithm
				assertTrue("was "+expectedDistance, expectedDistance>=0);
				assertTrue("was "+msFromIntervalStart, msFromIntervalStart>=0);
				assertTrue("was "+msFromIntervalEnd, msFromIntervalEnd>=0);
				
				int minDistance = expectedDistance;
				int maxDistance = expectedDistance;
				if(msFromIntervalStart<=TOLLERANCE_MS) maxDistance+=1;
				if(msFromIntervalEnd<=TOLLERANCE_MS) minDistance-=1;
				assertTrue(
						"unmet condition: "+minDistance+" <= d("+d+") <= "+maxDistance+"; RelativeTime: "+(msFromIntervalStart),
						d>=minDistance&&d<=maxDistance);
				nTests++;
			}
			assertEquals(0,d);
			assertTrue("not enought tests!",nTests>10);
		}
		

}
