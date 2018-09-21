package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient implements Runnable {
	private final InetAddress serverIP;
	private final int serverPort;
	private final DatagramSocket socket;
	private final Object obj;
	
	public UDPClient(int serverPort, String serverIP, Object obj) throws UnknownHostException, SocketException {
		this.serverIP = InetAddress.getByName(serverIP);
		this.serverPort = serverPort;
		this.obj = obj;
		socket = new DatagramSocket();
	}
	
	@Override
	public void run() {
		try {
            while (!socket.isClosed()) {
            	socket.setSoTimeout(3000);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                oos.close();
                byte[] objData = baos.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(objData, objData.length, serverIP, serverPort);
                socket.send(datagramPacket);
            }
		} catch (IOException e) {	}
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
}
