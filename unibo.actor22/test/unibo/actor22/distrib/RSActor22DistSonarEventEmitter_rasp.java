package unibo.actor22.distrib;


import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.ActorLocal;
import unibo.actor22.annotations.ActorRemote;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.EventObserver;
import unibo.actor22.common.RadarSystemConfig;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.context.EnablerContextForActors;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.events.EventMsgHandler;
 
 
 
/*
 * Questo sistema ipotizza che led e sonar siano attori 
 * con cui interagire  a scambio di messaggi
 */
@ActorLocal(name =     {ApplData.ledName, ApplData.sonarName}, 
		implement = {unibo.actor22.common.LedActor.class, unibo.actor22.common.SonarObservableActor.class})
@ActorRemote(name =   {ApplData.controllerName, EventMsgHandler.myName}, 
		//LOCAL TEST ON PC
		//host=    {"localhost", "localhost"},
		//port=    { ""+(ApplData.ctxPort+1), ""+(ApplData.ctxPort+1)},
		//protocol={ "TCP", "TCP" })
		//REMOTE PC FROM RASPBERRY
		host=    {"192.168.1.110", "192.168.1.110"}, //PC
		port=    { ""+ApplData.ctxPort, ""+ApplData.ctxPort},
		protocol={ "TCP", "TCP" })
public class RSActor22DistSonarEventEmitter_rasp {
	private EnablerContextForActors ctx;
 
	public void doJob() {
		ColorsOut.outappl(this.getClass().getName() + " | Start", ColorsOut.BLUE);
		configure();
		CommUtils.aboutThreads("Before execute - ");
		//CommUtils.waitTheUser();
		execute();
		//terminate();
	}
	
	protected void configure() {
		DomainSystemConfig.simulation   =  false; //true; //
		DomainSystemConfig.ledGui       =  false; //true; //
		DomainSystemConfig.sonarDistanceMax = 400;
		DomainSystemConfig.tracing      = true;			
 		CommSystemConfig.protcolType    = ProtocolType.tcp;
		CommSystemConfig.tracing        = false;
		ProtocolType protocol 		    = CommSystemConfig.protcolType;
		RadarSystemConfig.sonarObservable = true;
		
		//Qak22Context.initContext();
		ctx = new EnablerContextForActors( "ctx",ApplData.ctxPort,ApplData.protocol);
		
		Qak22Context.handleLocalActorDecl(this);
 		Qak22Context.handleRemoteActorDecl(this);
 	}
	
	protected void execute() {
		ColorsOut.outappl(this.getClass().getName() + " | execute", ColorsOut.MAGENTA);
		ctx.activate();
	} 
	
	public void terminate() {
		CommUtils.delay(30000);
		System.exit(0);
	}
	
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new RSActor22DistSonarEventEmitter_rasp().doJob();
		CommUtils.aboutThreads("Before end - ");
	}

}

/*
 * Threads:
 */
