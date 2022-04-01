package it.unibo.radarSystem22.domain.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable;
import it.unibo.radarSystem22.domain.mock.LedMock;
import it.unibo.radarSystem22.domain.mock.SonarMock;
import it.unibo.radarSystem22.domain.mock.SonarMockObservable;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;

public class TestSonarMockObservable {

		private ISonarObservable sonar;
		
		private int countCallbacks;
		private int callbackLastVal;
		
		@BeforeClass
		public static void initOnce() {
			DomainSystemConfig.simulation = true;
			DomainSystemConfig.sonarObservable = true;
		}
		
		@Before
		public void init() {
			countCallbacks = 0;
			sonar = new SonarMockObservable();
		}
		
		@After
		public void teardown() {
			sonar.deactivate();
		}
		
		@Test
		public void test() {
			DomainSystemConfig.sonarDelay = 50;
			
			sonar.registerForDistance( (d)->{callbackLastVal=d.getVal(); countCallbacks++; System.out.println("Callback: "+d);} );
			
			sonar.activate();
			
			BasicUtils.delay(DomainSystemConfig.sonarDelay/2);
			
			assertEquals(1, countCallbacks);
			assertEquals(90, callbackLastVal);
			
			BasicUtils.delay(DomainSystemConfig.sonarDelay);
			
			assertEquals(2, countCallbacks);
			assertTrue(callbackLastVal!=90);
		}
		

}
