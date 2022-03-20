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
  

public class UdpConnection implements Interaction2021{
	
public static final int MAX_PACKET_LEN = 1025;
protected DatagramSocket socket;
protected UdpEndpoint endpoint;
protected boolean closed;

private DatagramPacket packet = null;

	public UdpConnection( DatagramSocket socket, UdpEndpoint endpoint) throws Exception {
		closed = false;
		this.socket = socket;
		this.endpoint = endpoint;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		//ColorsOut.out( "UdpConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );	 
		if (closed) { throw new Exception("The connection has been previously closed"); }
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
	public String receiveMsg()  {
		String line;
 		try {
			//socket.setSoTimeout(timeOut)
			if(closed) {
				line = null; //UdpApplMessageHandler will terminate
			}else {
				byte[] buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				line = new String(packet.getData(), 0, packet.getLength());
				packet = null;
			}
 			return line;		
		} catch ( Exception e ) {
			ColorsOut.outerr( "UdpConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		return null;
		}		
	}

	@Override
	public void close() {
		closed = true;
		socket.close();
	}



}
