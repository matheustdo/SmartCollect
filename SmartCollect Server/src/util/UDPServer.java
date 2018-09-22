package util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

public class UDPServer extends Observable implements Runnable {
	private Object obj;
	private DatagramSocket serverSocket;
	
	public UDPServer(int serverPort, String serverIP) throws UnknownHostException, SocketException {
		this.obj = new Object();
		this.serverSocket = new DatagramSocket(serverPort, InetAddress.getByName(serverIP));
	}
	
	@Override
	public void run() {
		byte[] receivedData = new byte[1024];
		while(!serverSocket.isClosed()) {
			DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			try {
				serverSocket.receive(receivedPacket);				
				ByteArrayInputStream bais = new ByteArrayInputStream(receivedPacket.getData());
				ObjectInputStream ois = new ObjectInputStream(bais);
				obj = ois.readObject();
				ois.close();
				setChanged();
				notifyObservers();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DatagramSocket getServerSocket() {
		return serverSocket;
	}

	public Object getObj() {
		return obj;
	}	
}
