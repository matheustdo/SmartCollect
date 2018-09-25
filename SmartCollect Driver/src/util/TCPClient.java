package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

public class TCPClient extends Observable implements Runnable {
	private Object objReceived;
	private final Socket socket;
	
	public TCPClient(int serverPort, String serverIP) throws IOException {
		socket = new Socket(InetAddress.getByName(serverIP), serverPort);
		objReceived = new Object();
	}
	
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			objReceived = ois.readObject();
			setChanged();
			notifyObservers();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Object getObjReceived() {
		return objReceived;
	}
}
