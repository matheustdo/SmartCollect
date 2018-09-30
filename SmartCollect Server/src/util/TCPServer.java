package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class TCPServer extends Observable implements Runnable {
	private Object inObj;
	private Object outObj;
	private ServerSocket serverSocket;
	
	public TCPServer(int serverPort, String serverIP) throws IOException {
		this.serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName(serverIP));
		this.inObj = new Object();
		this.outObj = new Object();
	}
	
	public void run() {
		while(!serverSocket.isClosed()) {		
			try {
				Socket clientSocket = serverSocket.accept();
				inObj = (new ObjectInputStream(clientSocket.getInputStream())).readObject();
				setChanged();
				notifyObservers();
				(new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(outObj);			
				clientSocket.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}	
		}
	}

	public Object getInObj() {
		return inObj;
	}

	public void setOutObj(Object outObj) {
		this.outObj = outObj;
	}	
}
