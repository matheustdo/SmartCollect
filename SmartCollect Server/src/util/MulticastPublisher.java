package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Matheus Teles
 */
public class MulticastPublisher {
	private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;
 
    /**
     * Send a multicast message.
     * @param multicastMessage Multicast message.
     * @param groupIp Multicast group ip.
     * @param groupPort Multicast group port.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public void multicast(String multicastMessage, String groupIp, int groupPort) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName(groupIp);
        buf = multicastMessage.getBytes();
 
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, groupPort);
        socket.send(packet);
        socket.close();
    }
}
