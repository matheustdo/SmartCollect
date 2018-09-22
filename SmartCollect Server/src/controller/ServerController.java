package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import model.Dumpster;
import model.DumpsterType;
import util.UDPServer;

public class ServerController implements Observer {
	private int serverPort,
				trashCansQuantity,
				transferStationsQuantity,
				motoristsQuantity;
	private String serverIp;
	private UDPServer runnableUdpServer;
	private Map<Integer, Dumpster> dumpsters;
	
	public ServerController() {
		trashCansQuantity = 0;
		transferStationsQuantity = 0;
		motoristsQuantity = 0;
		dumpsters = new HashMap<Integer, Dumpster>();		
	}
	
	public void turnServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(serverPort, serverIp);
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();
		runnableUdpServer.addObserver(this);
	}
	
	public String getServerIp() {		
		return ((UDPServer)runnableUdpServer).getServerSocket().getLocalAddress().getHostAddress();
	}
	
	public int getServerPort() {
		return ((UDPServer)runnableUdpServer).getServerSocket().getLocalPort();
	}
	
	public Map<Integer, Dumpster> getDumpsters() {
		return dumpsters;
	}
	
	private void createServerConfigFile(File file) throws IOException {
		serverPort = 4065;
		serverIp = "localhost";
		
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("server-ip: " + serverIp + "\r\nserver-port: " + Integer.toString(serverPort));
        bw.close();
        fw.close();
	}

	public void readServerConfigFile() throws IOException {
		File file = new File("serverconfig.xml");
		
		if(!file.exists()) {
			createServerConfigFile(file);
		} else {
			FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);  
            serverIp = br.readLine().replace("server-ip: ", "");
            serverPort = Integer.parseInt(br.readLine().replace("server-port: ", ""));
            br.close();
            fr.close();
		}
	}	

	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof UDPServer) {
			dumpsters.put(((Dumpster)((UDPServer) subject).getObj()).getIdNumber(), ((Dumpster)((UDPServer) subject).getObj()));
		}		
	}
	
	public List<Dumpster> getDumpstersList() {
		Set<Integer> keys = dumpsters.keySet();
		List<Dumpster> dumpstersList = new ArrayList<Dumpster>();
		for(Integer key : keys) {
			if(key != null) {			
				dumpstersList.add(dumpsters.get(key));
			}
		}
		return dumpstersList;		
	}
	
	public int getTrashCansQuantity() {
		return trashCansQuantity;
	}
	
	public int getTransferStationsQuantity() {
		return transferStationsQuantity;
	}
	
	public int getMotoristsQuantity() {
		return 0;
	}
	
	public void updateDumpstersCounters() { 
		Set<Integer> keys = dumpsters.keySet();
		trashCansQuantity = 0;
		transferStationsQuantity = 0;
		for(Integer key : keys) {
			if(key != null) {
				Dumpster dumpster = dumpsters.get(key);
				if(dumpster.getType().equals(DumpsterType.CAN)) {
					trashCansQuantity++;
				} else {
					transferStationsQuantity++;
				}
			}
		}
	}
}
