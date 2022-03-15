package it.unibo.radarSystem22.domain.test;

import it.unibo.radarSystem22.domain.concrete.SonarConcrete;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.BasicUtils;

public class TestConcreteSonar {

	public static void main(String[] args) {
		ISonar s = new SonarConcrete();
		s.activate();
		for(int i = 0; i<10; i++) {
			System.out.println(s.getDistance());
			BasicUtils.delay(1000);
		}
		s.deactivate();
	}

}
