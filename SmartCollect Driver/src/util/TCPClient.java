package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

public class TCPClient extends Observable implements Runnable {
	int serverPort;
	String serverIP;
	private Object inObj;
	private Object outObj;
	
	public TCPClient(int serverPort, String serverIP, Object outObj) throws IOException {
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		this.inObj = new Object();
		this.outObj = outObj;
	}
	
	public void run() {
		while(true) {
			try {
				Socket socket = new Socket(InetAddress.getByName(serverIP), serverPort);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(outObj);
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				inObj = ois.readObject();
				setChanged();
				notifyObservers();
				socket.close();
				Thread.sleep(1000);				
			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();
			}			
		}		
	}

	public Object getObjReceived() {
		return inObj;
	}

	public void setOutObj(Object outObj) {
		this.outObj = outObj;
	}
}
