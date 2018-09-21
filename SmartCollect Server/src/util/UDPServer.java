package util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import model.Dumpster;

public class UDPServer implements Runnable {
	private DatagramSocket serverSocket;
	
	public UDPServer(int serverPort, String serverIP) throws UnknownHostException, SocketException {
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
				Dumpster dumpster = (Dumpster) ois.readObject();
				ois.close();
				
				System.out.println("Client: " + dumpster.getIdNumber());
				System.out.println("Trash Quantity: " + dumpster.getTrashQuantity());
				System.out.println("# # # # #");
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DatagramSocket getServerSocket() {
		return serverSocket;
	}
}
