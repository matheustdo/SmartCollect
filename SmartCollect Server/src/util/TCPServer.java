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
		this.inObj = new Object();
		this.outObj = new Object();
		this.serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName(serverIP));
	}
	
	public void run() {
		while(!serverSocket.isClosed()) {
			Socket clientSocket;			
			try {
				clientSocket = serverSocket.accept();
				
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				
				oos.flush();
				oos.writeObject(outObj);				
				oos.close();
				
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				ois.readObject();
				
				clientSocket.close();
				
				setChanged();
				notifyObservers();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public ServerSocket getServerSocket()  {
		return serverSocket;
	}
}
