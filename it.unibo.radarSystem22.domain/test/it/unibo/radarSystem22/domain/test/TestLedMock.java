package it.unibo.radarSystem22.domain.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.mock.LedMock;

public class TestLedMock {

	private ILed led;
	
	@Before
	public void init() {
		led = new LedMock();
		System.out.println("Up");
	}
	
	@After
	public void teardown() {
		System.out.println("Down");
	}
	
	@Test
	public void testLedMock_turnOn() {
		led.turnOn();
		System.out.println("1");
		assertTrue(led.getState());
	}
	
	@Test
	public void testLedMock_turnOff() {
		led.turnOff();
		System.out.println("0");
		assertFalse(led.getState());
		//assertFalse(led.getState());
	}

}
