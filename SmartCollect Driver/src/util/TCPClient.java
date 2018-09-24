package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient implements Runnable {
	private Object objReceived;
	private final Socket socket;
	
	public TCPClient(int serverPort, String serverIP) throws IOException {
		socket = new Socket(InetAddress.getByName(serverIP), serverPort);
		objReceived = new Object();
	}
	
	@Override
	public void run() {
		while(!socket.isClosed()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				objReceived = ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Object getObjReceived() {
		return objReceived;
	}
}
