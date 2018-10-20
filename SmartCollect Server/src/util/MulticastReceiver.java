package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * @author Matheus Teles
 */
public class MulticastReceiver implements Runnable {
	protected MulticastSocket socket;
    protected byte[] buf = new byte[256];
    InetAddress multicastIP;
    
    /**
     * Constructor for MulticastReceiver
     * @param multicastPort Multicast group Port
     * @param multicastIP Multicast group IP
     * @throws IOException 
     */
    public MulticastReceiver(int multicastPort, String multicastIP) throws IOException {
    	this.multicastIP = InetAddress.getByName(multicastIP);
    	this.socket = new MulticastSocket(multicastPort);
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
	            String received = new String(packet.getData(), 0, packet.getLength());
	            System.out.println(received);
	            if ("end".equals(received)) {
	                break;
	            }	            
	        }
	        socket.leaveGroup(multicastIP);
            socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}        
	}
}
