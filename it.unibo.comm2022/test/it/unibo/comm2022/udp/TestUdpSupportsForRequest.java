package it.unibo.comm2022.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.comm2022.common.NaiveApplHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.BasicUtils;
import it.unibo.comm2022.utils.ColorsOut;


public class TestUdpSupportsForRequest {
private UdpServer server;
public static final int testPort = 8111; 


	//@Before
	public void up() {
	}
	
	//@After
	public void down() {
		//if( server != null ) server.deactivate();
	}	
	
	protected void startTheServer(String name) {
		server = new UdpServer(name, testPort, new NaiveApplHandler("naiveH") );
		server.activate();		
	}
	
	protected void stopTheServer() {
		server.deactivate();	
		BasicUtils.delay(100); //wait for socket to unregister from the system
	}
	
	@Test 
	public void testManyConns() {
		ColorsOut.out(" -------------- testManyConns");		
		startTheServer("manyConnsServer");
		 try {
		     Interaction2021 conn  = UdpClientSupport.connect("localhost", testPort);
			 for( int i=1; i<=3;i++) {
				//Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
				String msg = "hello"+i  ;
				System.out.println("testManyConns | forward the msg=" + msg + " on conn:" + conn);	 
				conn.forward(msg);
				BasicUtils.delay(100);
				String answer = conn.receiveMsg();
				System.out.println("testManyConns | fanswer=" + answer + " on conn:" + conn);	 
				BasicUtils.delay(100);
			 }//for
		 } catch (Exception e) {
			 fail();
		 }
		BasicUtils.delay(1000);
		stopTheServer();
		ColorsOut.out(" -------------- testManyConns BYE");
	}
	
	@Test 
	public void testSingleClient() throws Exception {
		ColorsOut.out(" -------------- testSingleClient");
		startTheServer("oneClientServer");
 		//Create a connection
		new ClientDoingRequest().doWork("client1");		
		System.out.println("tesSingleClient BYE");
		stopTheServer();
		ColorsOut.out(" -------------- testSingleClient BYE");
	}
	
	
	@Test 
	public void testManyClients() throws Exception {
		ColorsOut.out(" -------------- testManyClients");
		startTheServer("manyClientsServer");
		new ClientDoingRequest().doWork("client1");
		new ClientDoingRequest().doWork("client2");
		new ClientDoingRequest().doWork("client3");
		System.out.println("testManyClients BYE");
		stopTheServer();
		ColorsOut.out(" -------------- testManyClients BYE");
	}
	
	@Test 
	public void testManyRequests() throws Exception {
		ColorsOut.out(" -------------- testManyRequests");
		NaiveApplHandler handler = new NaiveApplHandler("naiveHMod") {
			int i = 0;
			@Override
			public void elaborate(String message, Interaction2021 conn) {
				System.out.println(name + " | elaborate " + message + " conn=" + conn);
				BasicUtils.delay(100); //taking some time to reply
				this.sendMsgToClient("answerTo_"+message, conn);
				i++;
		  	}
		};
		server = new UdpServer("manyRequestsServer", testPort, handler );
		server.activate();
		
		Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
		
		int i=0;
		for(i=0; i<4; i++) {
			String request = "hello_"+i;
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(request);
		}
		for(i=0; i<4; i++) {
			String answer = conn.receiveMsg();
			System.out.println("client1" + " | receives the answer: " +answer );
			assertTrue(answer!=null);
		}
		stopTheServer();
		ColorsOut.out(" -------------- testManyRequests BYE");
	}
	
	@Test 
	public void serverClose() throws Exception {
		ColorsOut.out(" -------------- serverClose");
		NaiveApplHandler handler = new NaiveApplHandler("naiveHMod") {
			int i = 0;
			@Override
			public void elaborate(String message, Interaction2021 conn) {
				System.out.println(name + " | elaborate " + message + " conn=" + conn);
				BasicUtils.delay(100); //taking some time to reply
				this.sendMsgToClient("answerTo_"+message, conn);
				assertEquals(1,server.connectionsMap.size()); //1 client connected
				i++;
				if(i==2) {
					try {
						conn.close();
					} catch (Exception e) {
						e.printStackTrace();
						fail();
					}
					try {
						conn.forward("ciao");
						fail(); //connection is closed
					} catch (Exception e) {/*OK*/}
					assertEquals(0,server.connectionsMap.size()); //no other client connected
				}
		  	}
		};
		server = new UdpServer("serverClose", testPort, handler );
		server.activate();
		
		Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
		
		int i=0;
		for(i=0; i<2; i++) {
			String request = "hello_"+i;
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(request);
		}
		for(i=0; i<2; i++) {
			String answer = conn.receiveMsg();
			System.out.println("client1" + " | receives the answer: " +answer );
			assertTrue(answer!=null);
		}
		BasicUtils.delay(400); //give the server the time to make his tests
		
		String request = "hello_"+i;
		//conn.forward(request); //this will actually work, because if no read are done the object have no way of knowing if a close packet did arrived or not
		assertEquals(null, conn.receiveMsg());
		try{
			conn.forward(request);
			fail();
		}catch(Exception e) {/*OK*/}
		
		//try with a new connection
		
		conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
		
		System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
		conn.forward(request);
		
		String answer = conn.receiveMsg();
		System.out.println("client1" + " | receives the answer: " +answer );
		assertTrue(answer!=null);
		
		stopTheServer();
		ColorsOut.out(" -------------- serverClose BYE");
	}
	
	@Test
	public void closeConnFromClient() throws Exception {
		ColorsOut.out(" -------------- closeConnFromClient");
		NaiveApplHandler handler = new NaiveApplHandler("naiveHMod") {
			int i = 0;
			@Override
			public void elaborate(String message, Interaction2021 conn) {
				System.out.println(name + " | elaborate " + message + " conn=" + conn);
				BasicUtils.delay(100); //taking some time to reply
				this.sendMsgToClient("answerTo_"+message, conn);
				i++;
				serverConn = conn;
		  	}
		};
		server = new UdpServer("closeConnFromClient", testPort, handler );
		server.activate();
		
		Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
		int i=0;
		for(i=0; i<2; i++) {
			String request = "hello_"+i;
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(request);
		}
		for(i=0; i<2; i++) {
			String answer = conn.receiveMsg();
			System.out.println("client1" + " | receives the answer: " +answer );
			assertTrue(answer!=null);
		}
		
		assertEquals(1,server.getNumConnections());
		Interaction2021 previousServerConn = handler.serverConn;
		conn.close();
		BasicUtils.delay(20);
		assertEquals(0,server.getNumConnections()); //if connection has been closed, no connections should be left
		
		conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
		
		for(i=2; i<4; i++) {
			String request = "hello_"+i;
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(request);
		}
		for(i=2; i<4; i++) {
			String answer = conn.receiveMsg();
			System.out.println("client1" + " | receives the answer: " +answer );
			assertTrue(answer!=null);
		}
		
		assertEquals(1,server.getNumConnections()); //this connection has still not been closed by the server
		assertNotSame(handler.serverConn, previousServerConn);
		
		stopTheServer();
		ColorsOut.out(" -------------- closeConnFromClient BYE");
	}
	
}
