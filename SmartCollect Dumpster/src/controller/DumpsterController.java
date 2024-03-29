package controller;

import java.net.SocketException;
import java.net.UnknownHostException;

import model.Dumpster;
import model.DumpsterType;
import model.SCMProtocol;
import util.UDPClient;

/**
 * @author Matheus Teles
 */
public class DumpsterController {
    private Dumpster dumpster;
    private UDPClient runnableUdpClient;
    
    /**
     * Creates a dumpster.
     * @param idNumber Identification number of the dumpster.
     * @param maxCapacity Max capacity that the dumpster fits.
     * @param address Dumpster street address.
     * @param type Dumpster type.
     */
    public void createDumpster(int idNumber, double maxCapacity, String type) {
    	if(type.equals("Trash can")) {
    		dumpster = new Dumpster(idNumber, maxCapacity, DumpsterType.CAN);
    	} else {
    		dumpster = new Dumpster(idNumber, maxCapacity, DumpsterType.STATION);
    	}
    }
    
    /**
     * Updates the trash quantity from a percentual value.
     * @param percentualTrashQuantity Percentual trash value.
     * @return Trash quantity in a double.
     */
    public double updateTrashQuantity(double percentualTrashQuantity) {
    	dumpster.setTrashQuantity((dumpster.getMaxCapacity()*percentualTrashQuantity)/100);
    	return dumpster.getTrashQuantity();
    }
    
    /**
     * Turn server on.
     * @param serverPort Server port
     * @param serverIP Server IP
     * @throws UnknownHostException Indicate that the IP address of a host could not be determined.
     * @throws SocketException Indicate that there is an error creating or accessing a Socket.
     * @throws InterruptedException If the thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    public void turnClientOn(int serverPort, String serverIP) throws UnknownHostException, SocketException, InterruptedException {
    	runnableUdpClient = new UDPClient(serverPort, serverIP, SCMProtocol.CREATE + " " + dumpster.toString());
		Thread threadClient =  new Thread(runnableUdpClient);		
		threadClient.start();
		Thread.sleep(50);
		/* Change output object just to a string that contains the dumpster id and trash quantity */
		changeClientObj();
	}
    
    /**
     * Change the client object.
     */
    public void changeClientObj() {
    	((UDPClient)runnableUdpClient).setObj(SCMProtocol.UPDATE + " " + dumpster.toStringShort());
    }
}
