package it.unibo.radarSystem22.domain.test;

import static org.junit.Assert.*;

import java.util.Observable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.IObserver;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_traditional;
import it.unibo.radarSystem22.domain.mock.LedMock;
import it.unibo.radarSystem22.domain.mock.SonarMock;
import it.unibo.radarSystem22.domain.mock.SonarMockObservable_traditional;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;

public class TestSonarMockObservable_traditional {

		private ISonarObservable_traditional sonar;
		
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
			sonar = new SonarMockObservable_traditional();
		}
		
		@After
		public void teardown() {
			sonar.deactivate();
		}
		
		@Test
		public void test() {
			DomainSystemConfig.sonarDelay = 50;
			
			sonar.register( new IObserver() {
				@Override
				public void update(String s){
					callbackLastVal=Integer.parseInt(s);
					countCallbacks++;
					System.out.println("Callback: "+s);
				}

				@Override
				public void update(Observable o, Object arg) {
					// TODO Auto-generated method stub
					
				}
			} );
			
			sonar.activate();
			
			BasicUtils.delay(DomainSystemConfig.sonarDelay/2);
			
			assertEquals(1, countCallbacks);
			assertEquals(90, callbackLastVal);
			
			BasicUtils.delay(DomainSystemConfig.sonarDelay);
			
			assertEquals(2, countCallbacks);
			assertTrue(callbackLastVal!=90);
		}
		

}
