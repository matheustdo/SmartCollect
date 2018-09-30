package controller;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import util.TCPClient;

public class DriverController extends Observable implements Observer {
	private TCPClient runnableTcpClient;
	private String route;
	
	public void turnClientOn(int serverPort, String serverIP, int position) throws InterruptedException, IOException {
		runnableTcpClient = new TCPClient(serverPort, serverIP, position);
		Thread threadClient =  new Thread(runnableTcpClient);		
		threadClient.start();	
		runnableTcpClient.addObserver(this);
	}
	
	public String getRoute() {		
		return route;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof TCPClient) {
			route = ((TCPClient) o).getObjReceived().toString();
			setChanged();
			notifyObservers();
		}
	}
	
	public void setTcpOutObject(Object obj) {
		runnableTcpClient.setOutObj(obj);
	}
}
