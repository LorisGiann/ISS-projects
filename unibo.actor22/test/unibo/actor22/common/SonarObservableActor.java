package unibo.actor22.common;
 
import java.util.Observable;

import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MsgUtil;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.IObserver;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.QakActor22;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import it.unibo.radarSystem22.domain.DeviceFactory;
import it.unibo.radarSystem22.domain.concrete.SonarConcreteObservable_traditional;
import it.unibo.radarSystem22.domain.interfaces.ISonarObservable_traditional;
import it.unibo.radarSystem22.domain.mock.SonarMockObservable_traditional;


/*
 * Funge da interprete di comandi e richieste
 */
public class SonarObservableActor extends QakActor22{
	private ISonarObservable_traditional sonar;
	IObserver observer = new IObserver() {
		@Override
		public void update(Observable arg0, Object arg1) {}
		@Override
		public void update(String value) {
			IApplMessage newValMsg = ApplData.buildDispatch(ApplData.sonarName, "update", value, ApplData.controllerName);
			forward(newValMsg);
		}
	};

	public SonarObservableActor(String name) {
		super(name);
		if( DomainSystemConfig.simulation)  {
			sonar = new SonarMockObservable_traditional();
		}else { 
			sonar = new SonarConcreteObservable_traditional();
		}
	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		CommUtils.aboutThreads(getName()  + " |  Before doJob - ");
		ColorsOut.out( getName()  + " | doJob " + msg, ColorsOut.CYAN);
		if( msg.isRequest() ) elabRequest(msg);
		else elabCmd(msg);
	}

	protected void elabCmd(IApplMessage msg) {
		String msgCmd = msg.msgContent();
 		switch( msgCmd ) {
			case ApplData.cmdActivate  : {
				sonar.activate();
				sonar.register(observer);
				break;
			}
			case ApplData.cmdDectivate : {
				sonar.unregister(observer);
				sonar.deactivate();
				break;
			}
			default: ColorsOut.outerr(getName()  + " | unknown " + msgCmd);
		}
	}
 
	protected void elabRequest(IApplMessage msg) {
		String msgReq = msg.msgContent();
		//ColorsOut.out( getName()  + " | elabRequest " + msgCmd, ColorsOut.CYAN);
		switch( msgReq ) {
			case ApplData.reqIsActive  :{
				boolean b = sonar.isActive();
				IApplMessage reply = MsgUtil.buildReply(getName(), ApplData.reqIsActive, ""+b, msg.msgSender());
				ColorsOut.out( getName()  + " | reply= " + reply, ColorsOut.CYAN);
 				sendReply(msg, reply );				
				break;
			}
			case ApplData.reqDistance  :{
				String res = sonar.getDistance().toString();
				IApplMessage reply = MsgUtil.buildReply(getName(), ApplData.reqDistance, ""+res, msg.msgSender());
				ColorsOut.out( getName()  + " | reply= " + reply, ColorsOut.CYAN);
 				sendReply(msg, reply );				
				break;
			}
 			default: ColorsOut.outerr(getName()  + " | unknown " + msgReq);
		}
	}

}
