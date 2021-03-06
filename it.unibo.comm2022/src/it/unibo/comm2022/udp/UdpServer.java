package it.unibo.comm2022.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.tcp.TcpApplMessageHandler;
import it.unibo.comm2022.tcp.TcpConnection;
import it.unibo.comm2022.utils.ColorsOut;
import it.unibo.comm2022.utils.CommSystemConfig;


public class UdpServer extends Thread{
private DatagramSocket socket;
private byte[] buf;
public Map<UdpEndpoint,UdpServerConnection> connectionsMap; //map a port to a specific connection object, if any
protected IApplMsgHandler userDefHandler;
protected String name;
protected boolean stopped = true;

 	public UdpServer( String name, int port,  IApplMsgHandler userDefHandler   ) {
		super(name);
		connectionsMap = new ConcurrentHashMap<UdpEndpoint,UdpServerConnection>();
	     try {
	  		this.userDefHandler   = userDefHandler;
	  		ColorsOut.out(getName() + " | costructor port=" + port, ColorsOut.BLUE  );  
			this.name             = getName();
			socket                = new DatagramSocket( port );
			socket.setSoTimeout(CommSystemConfig.serverTimeOut);
	     }catch (Exception e) { 
	     	 ColorsOut.outerr(getName() + " | costruct ERROR: " + e.getMessage());
	     }
	}
	
	@Override
	public void run() {
	      try {
		  	ColorsOut.out(getName() + " | STARTING ... ", ColorsOut.BLUE  );
			while( ! stopped ) {
				//Wait a packet				 
				//ColorsOut.out(getName() + " | waits on server port=" + port + " serversock=" + serversock );	 
				buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet); //this is the only serverside receive on this socket
				InetAddress address = packet.getAddress();
	            int port = packet.getPort();
	            UdpEndpoint client = new UdpEndpoint(address, port);
	            ColorsOut.out(getName() + " | received packet from " + client, ColorsOut.BLUE   ); 
	            UdpServerConnection conn = connectionsMap.get(client);
	            if(conn == null) {
	            	conn = new UdpServerConnection(socket, client, connectionsMap);
	            	connectionsMap.put(client, conn);
	            	//Create a message handler on the connection
			 		new UdpApplMessageHandler( userDefHandler, conn );	
	            }
	            conn.handle(packet); //packets are passed to the corresponding connection		 		
			}//while
		  }catch (Exception e) {  //Scatta quando la deactive esegue: serversock.close();
			  ColorsOut.out(getName() + " | probably socket closed: " + e.getMessage(), ColorsOut.GREEN);		 
		  }
	}
	
	public void activate() {
		if( stopped ) {
			stopped = false;
			this.start();
		}//else already activated
	}
 
	public void deactivate() {
		try {
			ColorsOut.out(getName()+" |  DEACTIVATE serversock=" +  socket);
			stopped = true;
			for(UdpServerConnection conn : connectionsMap.values()) {
				conn.close();
			}
			socket.close();
			connectionsMap.clear();
		} catch (Exception e) {
			ColorsOut.outerr(getName() + " | deactivate ERROR: " + e.getMessage());	 
		}
	}

	public int getNumConnections() {
		return connectionsMap.size();
	}
 
}
