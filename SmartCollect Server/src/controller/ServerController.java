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
import java.util.StringTokenizer;

import model.Dumpster;
import model.DumpsterType;
import util.TCPServer;
import util.UDPServer;

public class ServerController implements Observer {
	private int serverPort,
				trashCansQuantity,
				transferStationsQuantity,
				motoristsQuantity;
	private String serverIp;
	private UDPServer runnableUdpServer;
	private TCPServer runnableTcpServer;
	private Map<Integer, Dumpster> dumpsters;
	
	public ServerController() {
		trashCansQuantity = 0;
		transferStationsQuantity = 0;
		motoristsQuantity = 0;
		dumpsters = new HashMap<Integer, Dumpster>();		
	}
	
	public void turnUdpServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(serverPort, serverIp);
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();
		runnableUdpServer.addObserver(this);
	}
	
	public void turnTcpServerOn() throws IOException {
		runnableTcpServer = new TCPServer(serverPort, serverIp);
		Thread threadTCPServer =  new Thread(runnableTcpServer);
		threadTCPServer.start();
		runnableTcpServer.addObserver(this);
	}
	
	public String getServerIp() {		
		return ((UDPServer)runnableUdpServer).getDatagramSocket().getLocalAddress().getHostAddress();
	}
	
	public int getServerPort() {
		return ((UDPServer)runnableUdpServer).getDatagramSocket().getLocalPort();
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
        bw.write("#SmartCollect server properties\r\n" + "server-ip: " + serverIp + "\r\nserver-port: " + Integer.toString(serverPort));
        bw.close();
        fw.close();
	}

	public void readServerConfigFile() throws IOException {
		File file = new File("server.properties");
		
		if(!file.exists()) {
			createServerConfigFile(file);
		} else {
			FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);  
            br.readLine();
            String ipLine = br.readLine().replaceAll(" ", "");
            serverIp = ipLine.replace("server-ip:", "");
            String portLine = br.readLine().replaceAll(" ", "");
            serverPort = Integer.parseInt(portLine.replace("server-port:", ""));
            br.close();
            fr.close();
		}
	}	

	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof UDPServer) {
			if(((UDPServer) subject).getObj() instanceof Dumpster) {
				dumpsters.put(((Dumpster)((UDPServer) subject).getObj()).getIdNumber(), ((Dumpster)((UDPServer) subject).getObj()));
			} else if (((UDPServer) subject).getObj() instanceof String){
				String obj = (String) ((UDPServer) subject).getObj();
				StringTokenizer st = new StringTokenizer(obj);
				int id = Integer.parseInt(st.nextToken());
				Double trashQuantity = Double.parseDouble(st.nextToken());
				Dumpster dumpster;
				
				if(dumpsters.containsKey(id)) {
					dumpster = dumpsters.get(id);
					dumpster.setTrashQuantity(trashQuantity);				
					double capacity = dumpster.getMaxCapacity();
					DumpsterType type = dumpster.getType();				
					dumpster = new Dumpster(id, trashQuantity, capacity, type);
					dumpsters.put(id, dumpster);
				} else {
					dumpsters.put(id, new Dumpster(id));
				}				 
			}			
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
				} else if (dumpster.getType().equals(DumpsterType.STATION)){
					transferStationsQuantity++;
				}
			}
		}
	}
}
