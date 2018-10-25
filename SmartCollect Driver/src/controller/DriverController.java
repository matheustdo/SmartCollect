package controller;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import model.SCMProtocol;
import util.TCPClient;

/**
 * @author Matheus Teles
 */
public class DriverController extends Observable implements Observer {
	private TCPClient runnableTcpClient;
	private String route;
	private String id;
	
	/**
	 * Turn tpc client on.
	 * @param serverPort Tcp server port.
	 * @param serverIP Tcp ip address.
	 * @param id Driver id.
	 * @param position Driver position.
	 * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public void turnClientOn(int serverPort, String serverIP) throws InterruptedException, IOException {
		runnableTcpClient = new TCPClient(serverPort, serverIP, SCMProtocol.CREATE + "");
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
		if(o instanceof TCPClient && ((TCPClient) o).getObjReceived() instanceof String) {
			StringTokenizer st = new StringTokenizer(((TCPClient) o).getObjReceived().toString());
			int action = Integer.parseInt(st.nextToken());
			
			if(action == SCMProtocol.INFO) {
				id = st.nextToken();				
			} else if (action == SCMProtocol.UPDATE) {
				route = (((TCPClient) o).getObjReceived().toString()).replaceFirst(SCMProtocol.UPDATE + " ", "");				
			}
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Set tcp output object.
	 * @param obj Output object.
	 */
	public void setTcpOutObject(String obj) {
		if(runnableTcpClient != null) {
			runnableTcpClient.setOutObj(SCMProtocol.PROCESS + " " + id + " " + obj);
		}
	}
	
	/**
	 * Forces the server to send a message.
	 */
	public void forceSending() {
		try {
			runnableTcpClient.send();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return if the driver has an id.
	 * @return If the driver has id.
	 */
	public boolean hasId() {
		return id != null;
	}	
}
