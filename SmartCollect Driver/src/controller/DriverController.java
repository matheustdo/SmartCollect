package controller;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import util.TCPClient;

/**
 * @author Matheus Teles
 */
public class DriverController extends Observable implements Observer {
	private TCPClient runnableTcpClient;
	private String route;
	
	/**
	 * Turn tpc client on.
	 * @param serverPort Tcp server port.
	 * @param serverIP Tcp ip address.
	 * @param id Driver id.
	 * @param position Driver position.
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public void turnClientOn(int serverPort, String serverIP, String id, String position) throws InterruptedException, IOException {
		runnableTcpClient = new TCPClient(serverPort, serverIP, id + " " + position);
		Thread threadClient =  new Thread(runnableTcpClient);		
		threadClient.start();	
		runnableTcpClient.addObserver(this);
	}
	
	/**
	 * Get route.
	 * @return Route.
	 */
	public String getRoute() {		
		return route;
	}
	
	/**
	 * Updates view.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof TCPClient) {
			route = ((TCPClient) o).getObjReceived().toString();
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Set tcp output object.
	 * @param obj Output object.
	 */
	public void setTcpOutObject(Object obj) {
		runnableTcpClient.setOutObj(obj);
	}
}
