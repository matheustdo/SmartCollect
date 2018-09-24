package controller;

import java.io.IOException;
import java.util.StringTokenizer;

import util.TCPClient;

public class DriverController {
	private Runnable runnableTcpClient;
	
	public void turnClientOn(int serverPort, String serverIP) throws InterruptedException, IOException {
		runnableTcpClient = new TCPClient(serverPort, serverIP);
		Thread threadClient =  new Thread(runnableTcpClient);		
		threadClient.start();
	}
	
	public String getRoute() {
		return ((TCPClient)runnableTcpClient).getObjReceived().toString();
	}
}
