package unibo.actor22.common;

import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.*;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

/*
 * Il controller conosce SOLO I NOMI dei dispositivi 
 * (non ha riferimenti ai dispositivi-attori)
 */
public class ControllerObservableActor extends QakActor22{
	private static final int ITERATIONS = 1000;
	private static final int TIME_BETWEEN_ITERATIONS = 100;
	protected int numIter = -1;
	protected int state = -1;
	protected boolean on = true;
	boolean ledRequiredState;

	public ControllerObservableActor(String name  ) {
		super(name);
	}

	@Override
	protected void handleMsg(IApplMessage msg) {  
		if( msg.isReply() ) {
			elabAnswer(msg);
		}else { 
			elabCmd(msg) ;	
		}
	}

	protected void elabCmd(IApplMessage msg) {
		String msgCmd = msg.msgContent();
		ColorsOut.outappl( getName()  + " | elabCmd=" + msgCmd, ColorsOut.GREEN);
		if(msg.msgSender().equals(ApplData.sonarName) && msg.msgId().equals("update")) {
			newDistance(new Distance(msg.msgContent()));
		}
		switch( msgCmd ) {
			case ApplData.cmdActivate : {
				doControllerWork();
				break;
			}
			default:break;
		}		
	}
	
	protected void doControllerWork() {
		if( numIter==-1 ) {
			CommUtils.aboutThreads(getName()  + " |  Before doControllerWork on=" + on );
			forward( ApplData.activateSonar );
			request( ApplData.isActiveSonar );
			numIter=0;
		}
	}

	protected void elabAnswer(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabAnswer numIter=" + numIter + " "+ msg, ColorsOut.MAGENTA);
		if(numIter==0) {
			if(msg.msgSender().equals(ApplData.sonarName)) {
				if(msg.msgContent()=="false") {
					ColorsOut.outappl(getName() + " | sonar initialization failed ", ColorsOut.RED);
					stop();
				}
				numIter=1;
				request( ApplData.reqDistanceSonar );
			}else {
				ColorsOut.outappl(getName() + " | unexpected message ", ColorsOut.RED);
			}
		} else {
			if(msg.msgSender().equals(ApplData.sonarName) && msg.msgId().equals("update")) {
				newDistance(new Distance(msg.msgContent()));
			}
		}
	}

	protected void stop() {
		forward( ApplData.deactivateSonar ); //spegnimento
		System.exit(0);
	}
	
	protected void newDistance(IDistance dist) {
		if( dist.getVal() < DomainSystemConfig.DLIMIT) {
			ledRequiredState = true;
			forward( ApplData.turnOnLed ); //accesione
		} else {
			ledRequiredState = false;
			forward( ApplData.turnOffLed ); //spegnimento
		}
		//request(getStateRequest);
		IApplMessage msg = ApplData.buildDispatch(ApplData.controllerName, "cmd", dist.toString(), ApplData.radarName);
		forward( msg );
		numIter ++;
		if (numIter > ITERATIONS) stop();
	}

}
