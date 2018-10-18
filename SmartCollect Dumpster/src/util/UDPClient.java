package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Matheus Teles
 */
public class UDPClient implements Runnable {
	private final InetAddress serverIP;
	private final int serverPort;
	private final DatagramSocket socket;
	private Object obj;	
	
	/**
	 * Constructs udp client.
	 * @param serverPort Server port.
	 * @param serverIP Server ip.
	 * @param obj Output object.
	 * @throws UnknownHostException Indicate that the IP address of a host could not be determined.
	 * @throws SocketException Indicate that there is an error creating or accessing a Socket.
	 */
	public UDPClient(int serverPort, String serverIP, Object obj) throws UnknownHostException, SocketException {
		this.serverIP = InetAddress.getByName(serverIP);
		this.serverPort = serverPort;
		this.obj = obj;
		socket = new DatagramSocket();
	}
	
	/**
	 * Thread run method.
	 */
	@Override
	public void run() {
		try {
            while (!socket.isClosed()) {            	
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                
                DatagramPacket datagramPacket;
                /* Send output object */
            	oos.writeObject(obj);
                oos.close();
                byte[] objData = baos.toByteArray();
            	datagramPacket = new DatagramPacket(objData, objData.length, serverIP, serverPort);
                socket.send(datagramPacket);
                System.out.println(obj);
                Thread.sleep(2000);
            }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set out object.
	 * @param obj Out object
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}
}
