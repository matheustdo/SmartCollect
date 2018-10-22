package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;

/**
 * @author Matheus Teles
 */
public class MulticastReceiver extends Observable implements Runnable {
	private MulticastSocket socket;
	private byte[] buf = new byte[256];
    private InetAddress multicastIP;
    private String receivedStr;
    
    /**
     * Constructor for MulticastReceiver
     * @param multicastPort Multicast group Port
     * @param multicastIP Multicast group IP
     * @throws IOException 
     */
    public MulticastReceiver(int multicastPort, String multicastIP) throws IOException {
    	this.multicastIP = InetAddress.getByName(multicastIP);
    	this.socket = new MulticastSocket(multicastPort);
    	receivedStr = new String();
    }    
    
    /**
	 * Thread run method.
	 */
	@Override
	public void run() {
		try {			
	        socket.joinGroup(multicastIP);
	        while (true) {
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            receivedStr = new String(packet.getData(), 0, packet.getLength());
	            setChanged();
				notifyObservers();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}        
	}

	/**
	 * Get received string.
	 * @return Received string.
	 */
	public String getReceivedStr() {
		return receivedStr;
	}	
}
