package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/**
 * @author Matheus Teles
 */
public class TCPServer extends Observable implements Runnable {
	private Object inObj;
	private Object outObj;
	private ServerSocket serverSocket;
	
	/**
	 * Constructs a new TCPServer.
	 * @param serverPort Server port.
	 * @param serverIP Server ip address.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public TCPServer(int serverPort, String serverIP) throws IOException {
		this.serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName(serverIP));
		this.inObj = new Object();
		this.outObj = null;
	}
	
	/**
	 * Thread run method.
	 */
	public void run() {
		while(!serverSocket.isClosed()) {		
			try {
				/* Wait for an client */ 
				Socket clientSocket = serverSocket.accept();
				/* Receives an object from client */
				inObj = (new ObjectInputStream(clientSocket.getInputStream())).readObject();
				/* Process the received object */
				setChanged();
				notifyObservers();
				/* Sends the object for client */
				if(outObj != null) {
					(new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(outObj);	
				}				
				/* Close socket */
				clientSocket.close();
				outObj = null;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}	
		}
	}

	/**
	 * Get input object.
	 * @return Input object.
	 */
	public Object getInObj() {
		return inObj;
	}

	/**
	 * Set output object.
	 * @param outObj Output object.
	 */
	public void setOutObj(Object outObj) {
		this.outObj = outObj;
	}	
	
	/**
	 * Get server port.
	 * @return Server port.
	 */
	public int getServerPort() {
		return serverSocket.getLocalPort();
	}
}
