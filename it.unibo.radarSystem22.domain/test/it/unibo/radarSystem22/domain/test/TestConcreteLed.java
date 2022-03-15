package it.unibo.radarSystem22.domain.test;

import it.unibo.radarSystem22.domain.concrete.LedConcrete;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.utils.BasicUtils;

public class TestConcreteLed {

	public static void main(String[] args) {
		ILed led = new LedConcrete();
		for(int i=0; i<100; i++) {
			led.turnOn();
			BasicUtils.delay(500);
			led.turnOff();
			BasicUtils.delay(500);
		}

	}

}
