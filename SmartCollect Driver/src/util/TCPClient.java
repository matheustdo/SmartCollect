package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * @author Matheus Teles
 */
public class TCPClient extends Observable implements Runnable {
	private int serverPort;
	private String serverIP;
	private Object inObj;
	private Object outObj;
	
	/**
	 * Constructs a new tcp client.
	 * @param serverPort Server port.
	 * @param serverIP Server ip.
	 * @param outObj Output object.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public TCPClient(int serverPort, String serverIP, Object outObj) throws IOException {
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		this.inObj = new Object();
		this.outObj = outObj;
	}
	
	/**
	 * Thread run method.
	 */
	public void run() {
		while(true) {
			try {
				send();
				Thread.sleep(1000);
			} catch (InterruptedException | ClassNotFoundException | IOException e) {				
				e.printStackTrace();
				break;
			}
		}		
	}
	
	/**
	 * Send an object.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 * @throws UnknownHostException Unknown host exception.
	 * @throws ClassNotFoundException Class not found exception
	 */
	public void send() throws UnknownHostException, IOException, ClassNotFoundException {
		/* Connects to server socket */
		Socket socket;
		socket = new Socket(InetAddress.getByName(serverIP), serverPort);
		/* Send output object to server */
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(outObj);
		/* Receive input object from server */				
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		inObj = ois.readObject();
		/* Update observers */
		setChanged();
		notifyObservers();
		/* Close socket and sleep thread */
		socket.close();
	}

	/**
	 * Get input object.
	 * @return Input object.
	 */
	public Object getObjReceived() {
		return inObj;
	}

	/**
	 * Get output object.
	 * @param outObj Output object.
	 */
	public void setOutObj(Object outObj) {
		this.outObj = outObj;
	}
}
