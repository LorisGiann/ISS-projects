package it.unibo.radarSystem22.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ConnClient extends RadarUsageMain {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;
    DatagramPacket packet;

    public ConnClient(String addr) throws Exception {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);   // set the timeout in millisecounds.
        address = InetAddress.getByName(addr);
    }

    public String sendReq() throws IOException {
        boolean received = false;
        do{        // recieve data until timeout
            try {
            	buf = "req".getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 8080);
            	socket.send(packet);
            	
                packet = new DatagramPacket(buf, buf.length);
            	socket.receive(packet);
            	received = true;
            }catch (SocketTimeoutException e) {
                System.out.println("Timeout reached!!! " + e);
            }
        }while(!received);
        
        String result = new String(packet.getData(), 0, packet.getLength());
        return result;
    }

    public void close() {
        socket.close();
    }
}
