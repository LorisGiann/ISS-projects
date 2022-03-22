package it.unibo.comm2022.udp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.BasicUtils;
import it.unibo.comm2022.utils.ColorsOut;
  

public class UdpConnection implements Interaction2021{
	
public static final int MAX_PACKET_LEN = 1025;
protected DatagramSocket socket;
protected UdpEndpoint endpoint;
protected boolean closed;
protected static final String closeMsg = "_CLOSE_";

private DatagramPacket packet = null;

	public UdpConnection( DatagramSocket socket, UdpEndpoint endpoint) throws Exception {
		closed = false;
		this.socket = socket;
		this.endpoint = endpoint;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		if(closed) {throw new IllegalStateException("This connection has previously been closed!");}
		//ColorsOut.out( "UdpConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );	
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, endpoint.getAddress(), endpoint.getPort());
	        socket.send(packet);
			//Colors.out( "UdpConnection | has sent   " + msg, Colors.ANSI_YELLOW );	 
		} catch (IOException e) {
			//Colors.outerr( "UdpConnection | sendALine ERROR " + e.getMessage());	 
			throw e;
		}	
	}

	@Override
	public String request(String msg)  throws Exception {
		forward(  msg );
		String answer = receiveMsg();
		return answer;
	}
	
	@Override
	public void reply(String msg) throws Exception {
		forward(msg);
	} 
	
	@Override
	public String receiveMsg() throws Exception  {
		if(closed) {throw new IllegalStateException("This connection has previously been closed!");}
		String line;
 		try {
			if(closed) {
				line = null; //UdpApplMessageHandler will terminate
			}else {
				byte[] buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				line = new String(packet.getData(), 0, packet.getLength());
				if(closeMsg.equals(line)) {
					line = null;
					closed = true;
					ColorsOut.out("UdpConnection | server " + endpoint + " is closing connection");
				}
			}
 			return line;		
		} catch ( Exception e ) {
			//ColorsOut.outerr( "UdpConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		//return null;
			throw e;
		}		
	}

	@Override
	public void close() throws IOException {
		if(closed) return;
		closed = true;
		ColorsOut.out("UdpConnection | closing connection with server" + endpoint);
		sendClosePacket();
		socket.close(); //socket on client have to be closed
	}
	
	protected void sendClosePacket() throws IOException { //packet signaling the endpoint that the connection is now closed
		byte[] buf = closeMsg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, endpoint.getAddress(), endpoint.getPort());
        socket.send(packet);
	}



}
