package unibo.actor22.common;
 
import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MsgUtil;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.IRadarDisplay;
import unibo.actor22.QakActor22;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import it.unibo.radarSystem22.domain.DeviceFactory;


/*
 * Funge da interprete di comandi e richieste
 */
public class RadarActor extends QakActor22{
protected IRadarDisplay radar;

	public RadarActor(String name) {
		super(name);
		radar = DeviceFactory.createRadarGui();
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
 		try{
 			Integer.parseInt(msgCmd);
			radar.update(msgCmd, "60");
		}catch(NumberFormatException e) {
			ColorsOut.outerr(getName()  + " | unknown " + msgCmd);
		}
	}
 
	protected void elabRequest(IApplMessage msg) {
		String msgReq = msg.msgContent();
		//ColorsOut.out( getName()  + " | elabRequest " + msgCmd, ColorsOut.CYAN);
		switch( msgReq ) {
 			default: ColorsOut.outerr(getName()  + " | unknown " + msgReq);
		}
	}

}
