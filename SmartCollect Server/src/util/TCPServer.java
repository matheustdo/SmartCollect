package util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class TCPServer extends Observable implements Runnable {
	private Object inObj;
	private Object outObj;
	private ServerSocket serverSocket;
	
	public TCPServer(Object outObj, int serverPort, String serverIP) throws IOException {
		this.inObj = new Object();
		this.outObj = outObj;
		this.serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName(serverIP));
	}
	
	public void run() {
		while(!serverSocket.isClosed()) {		
			try {
				Socket clientSocket = serverSocket.accept();
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.flush();
				oos.writeObject(outObj);
				//oos.close();
			    //clientSocket.close();
				setChanged();
				notifyObservers();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public ServerSocket getServerSocket()  {
		return serverSocket;
	}

	public Object getInObj() {
		return inObj;
	}

	public void setOutObj(Object outObj) {
		this.outObj = outObj;
	}
}
