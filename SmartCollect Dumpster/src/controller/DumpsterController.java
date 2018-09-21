package controller;

import java.net.SocketException;
import java.net.UnknownHostException;

import model.Dumpster;
import model.DumpsterType;
import util.UDPClient;

public class DumpsterController {
    private Dumpster dumpster;
    
    /**
     * Creates a dumpster
     * @param idNumber Identification number of the dumpster
     * @param maxCapacity Max capacity that the dumpster fits
     * @param address Dumpster street address
     * @param type Dumpster type
     */
    public void createDumpster(int idNumber, double maxCapacity, String address, String type) {
    	if(type.equals("Trash can")) {
    		dumpster = new Dumpster(idNumber, maxCapacity, address, DumpsterType.CAN);
    	} else {
    		dumpster = new Dumpster(idNumber, maxCapacity, address, DumpsterType.STATION);
    	}
    }
    
    /**
     * Updates the trash quantity from a percentual value
     * @param percentualTrashQuantity Percentual trash value
     * @return Trash quantity in a double
     */
    public double updateTrashQuantity(double percentualTrashQuantity) {
    	dumpster.setTrashQuantity((dumpster.getMaxCapacity()*percentualTrashQuantity)/100);
    	return dumpster.getTrashQuantity();
    }
    
    /**
     * Turn server on
     * @param serverPort Server port
     * @param serverIP Server IP
     * @throws UnknownHostException UnknownHostException exception
     * @throws SocketException SocketException exception
     */
    public void turnServerOn(int serverPort, String serverIP) throws UnknownHostException, SocketException {
		Runnable runnableClient;
		runnableClient = new UDPClient(serverPort, serverIP, dumpster);
		Thread threadClient =  new Thread(runnableClient);
		threadClient.start();
	}
}
