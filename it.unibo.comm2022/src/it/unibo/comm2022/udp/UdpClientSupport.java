package it.unibo.comm2022.udp;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.ColorsOut;


public class UdpClientSupport {
//	private Socket socket;
	
	public static Interaction2021 connect(String host, int port) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		//socket.setSoTimeout(10);
        InetAddress address = InetAddress.getByName(host);
        UdpEndpoint server = new UdpEndpoint(address, port);
		Interaction2021 conn  =  new UdpConnection(socket, server);
		return conn;
 	}
 
}
