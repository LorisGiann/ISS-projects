package it.unibo.radarSystem22.domain.models;
 
import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.concrete.SonarConcrete;
import it.unibo.radarSystem22.domain.interfaces.*;
import it.unibo.radarSystem22.domain.mock.SonarMock;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;



public abstract class SonarModel implements ISonar{  

protected  IDistance curVal = new Distance(90);
//NOTE: Accesses and updates to the memory cells corresponding to fields of any 
// type except long or double are guaranteed to be atomic

	protected boolean stopped = true;

	protected SonarModel() {//hidden costructor, to force setup
		ColorsOut.out("SonarModel | calling (specialized) sonarSetUp ", ColorsOut.BLUE );
		//sonarSetUp();   now in the solnar itself
	}

	public static ISonar create() {
		if( DomainSystemConfig.simulation )  return createSonarMock();
		else  return createSonarConcrete();		
	}

	public static ISonar createSonarMock() {
		ColorsOut.out("createSonarMock", ColorsOut.BLUE);
		return new SonarMock();
	}	
	public static ISonar createSonarConcrete() {
		ColorsOut.out("createSonarConcrete", ColorsOut.BLUE);
		return new SonarConcrete();
	}
	
	protected void updateDistance( int d ) { //called by concrete and mock objects on new measure
		curVal = new Distance( d ); //immutable values, no need for syncronization
		ColorsOut.out("SonarModel | updateDistance "+ d, ColorsOut.BLUE);
	}	

	//protected abstract void sonarSetUp(); //  now in the sonar itself
 	protected abstract void sonarProduce();

	@Override
	public boolean isActive() {
		//ColorsOut.out("SonarModel | isActive "+ (! stopped), ColorsOut.GREEN);
		return ! stopped;
	}
	
	@Override
	public IDistance getDistance() {
		return curVal;
	}
	
	@Override
	public void activate() {
		curVal = new Distance( 90 );
 		ColorsOut.out("SonarModel | activate" );
		stopped = false;
		new Thread() {
			public void run() {
				while( ! stopped  ) {
					//Colors.out("SonarModel | call produce", Colors.GREEN);
					sonarProduce(  );
					BasicUtils.delay(2); //allow some time for the new value to be read by other threads
				}
				ColorsOut.out("SonarModel | ENDS" );
		    }
		}.start();
	}
 	
	@Override
	public void deactivate() {
		ColorsOut.out("SonarModel | deactivate", ColorsOut.BgYellow );
		stopped = true;
	}

}

  

