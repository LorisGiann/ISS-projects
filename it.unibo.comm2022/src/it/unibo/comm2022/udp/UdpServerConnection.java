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
import it.unibo.comm2022.utils.ColorsOut;
  

public class UdpServerConnection extends UdpConnection{
private Map<UdpEndpoint,UdpServerConnection> connMap;

//producer/consumer syncronization
private DatagramPacket packet = null;
private Semaphore waitToEnterNewPacket = new Semaphore(1); //handle() waits until a packet is still waiting for being processed
private Semaphore waitToConsumeNewPacket = new Semaphore(0); //receiveMsg() waits until a packet have arrived

	public UdpServerConnection( DatagramSocket socket, UdpEndpoint client, Map<UdpEndpoint,UdpServerConnection> connMap ) throws Exception {
		super(socket, client);
		this.connMap = connMap;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		//ColorsOut.out( "UdpServerConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );
		if(super.closed) {throw new IllegalStateException("This connection has previously been closed!");}
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, super.endpoint.getAddress(), super.endpoint.getPort());
	        socket.send(packet);
			//Colors.out( "UdpServerConnection | has sent   " + msg, Colors.ANSI_YELLOW );	 
		} catch (IOException e) {
			//Colors.outerr( "UdpServerConnection | sendALine ERROR " + e.getMessage());	 
			throw e;
		}	
	}
	
	@Override
	public String receiveMsg() throws Exception  {
		String line;
 		try {
 			if(!super.closed) waitToConsumeNewPacket.acquire(); //blocking if connection not closed
 			if(super.closed) return null; //UdpApplMessageHandler will terminate
			line = new String(packet.getData(), 0, packet.getLength());
			packet = null;
			waitToEnterNewPacket.release();
 			return line;		
		} catch ( Exception e ) {
			//ColorsOut.outerr( "UdpServerConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		//return null;
			throw e;
		}		
	}

	@Override
	public void close() throws IOException {
		if(super.closed) return;
		ColorsOut.out("UdpServerConnection | server is closing connection with client " + super.endpoint);
		localClose();
		sendClosePacket();
		//no socket to close on server, other present or future connections may need it
    }
	
	private void localClose() {
		connMap.remove(super.endpoint); //new packects will now use a new connection object and a new message handler. No other packet will reach this class!
		super.closed = true;
	}
	
	//handle packets that are received from server
	public void handle(DatagramPacket packet) {
		if(super.closed) {throw new IllegalStateException("This connection has previously been closed!");}
		try {
			waitToEnterNewPacket.acquire();
			this.packet = packet;
			if(packet.getLength()==closeMsg.length() && closeMsg.equals(new String(packet.getData(), 0, packet.getLength()))) {
				ColorsOut.out("UdpServerConnection | client " + super.endpoint + " is closing connection");
				localClose();
			}
			waitToConsumeNewPacket.release(); // enable receiveMsg()
		} catch (InterruptedException e) {}
	}



}
