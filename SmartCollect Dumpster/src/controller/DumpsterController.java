package controller;

import java.net.SocketException;
import java.net.UnknownHostException;

import model.Dumpster;
import model.DumpsterType;
import util.UDPClient;

public class DumpsterController {
    private Dumpster dumpster;
    private Runnable runnableUdpClient;
    
    /**
     * Creates a dumpster
     * @param idNumber Identification number of the dumpster
     * @param maxCapacity Max capacity that the dumpster fits
     * @param address Dumpster street address
     * @param type Dumpster type
     */
    public void createDumpster(int idNumber, int regionIdNumber, double maxCapacity, String type) {
    	if(type.equals("Trash can")) {
    		dumpster = new Dumpster(idNumber, regionIdNumber, maxCapacity, DumpsterType.CAN);
    	} else {
    		dumpster = new Dumpster(idNumber, regionIdNumber, maxCapacity, DumpsterType.STATION);
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
     * @throws InterruptedException 
     */
    public void turnClientOn(int serverPort, String serverIP) throws UnknownHostException, SocketException, InterruptedException {
    	runnableUdpClient = new UDPClient(serverPort, serverIP, dumpster);
		Thread threadClient =  new Thread(runnableUdpClient);		
		threadClient.start();
		Thread.sleep(50);
		((UDPClient)runnableUdpClient).setObj(dumpster.toString());
	}
    
    /**
     * Change clientObj
     */
    public void changeClientObj() {
    	((UDPClient)runnableUdpClient).setObj(dumpster.toString());
    }
}
