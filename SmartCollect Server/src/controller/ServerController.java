package controller;

import java.net.SocketException;
import java.net.UnknownHostException;

import util.UDPServer;

public class ServerController {
	
	private Runnable runnableUdpServer;
	
	public void turnServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(25565, "localhost");
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();		
	}
	
	public String getServerIp() {
		
		return ((UDPServer)runnableUdpServer).getServerSocket().getLocalAddress().getHostAddress();
	}
	
	public int getServerPort() {
		return ((UDPServer)runnableUdpServer).getServerSocket().getLocalPort();
	}
	
	public int getTrashCansQuantity() {
		return 0;
	}
	
	public int getTransferStationsQuantity() {
		return 0;
	}
	
	public int getMotoristsQuantity() {
		return 0;
	}
}
