package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import model.Driver;
import model.Dumpster;
import model.DumpsterType;
import util.TCPServer;
import util.UDPServer;

public class ServerController extends Observable implements Observer {
	private int udpServerPort,
				tcpServerPort,
				trashCansQuantity,
				transferStationsQuantity,
				minimunTrashPercentage;
	private String serverIp,
				   lastMessage;
	private UDPServer runnableUdpServer;
	private TCPServer runnableTcpServer;
	private Map<Integer, Dumpster> dumpsters;
	private Map<Integer, Driver> drivers;
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ServerController() {
		this.serverIp = "localhost";
		this.udpServerPort = 4065;
		this.tcpServerPort = 4066;
		this.trashCansQuantity = 0;
		this.transferStationsQuantity = 0;
		this.minimunTrashPercentage = 80;
		this.lastMessage = "";
		this.dumpsters = new HashMap<Integer, Dumpster>();
		this.drivers = new HashMap<Integer, Driver>();
	}
	
	public void turnUdpServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(udpServerPort, serverIp);
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();
		runnableUdpServer.addObserver(this);
	}
	
	public void turnTcpServerOn() throws IOException {
		runnableTcpServer = new TCPServer(tcpServerPort, serverIp);
		Thread threadTCPServer =  new Thread(runnableTcpServer);
		threadTCPServer.start();
		runnableTcpServer.addObserver(this);
	}
	
	public void updateDrivers() throws IOException {
		
	}
	
	public String getServerIp() {		
		return serverIp;
	}
	
	public int getUdpServerPort() {
		return udpServerPort;
	}
	
	public int getTcpServerPort() {
		return tcpServerPort;
	}
	
	public Map<Integer, Dumpster> getDumpsters() {
		return dumpsters;
	}
	
	private void createServerConfigFile(File file) throws IOException {		
		serverIp = "localhost";
		
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("#SmartCollect server properties\r\n" + "server-ip: " + serverIp +
        		 "\r\nudp-server-port: " + Integer.toString(udpServerPort) + "\r\ntcp-server-port: " +
        		 Integer.toString(tcpServerPort) + "\r\nminimun-trash-percentage: " + minimunTrashPercentage);
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
            String udpPortLine = br.readLine().replaceAll(" ", "");
            udpServerPort = Integer.parseInt(udpPortLine.replace("udp-server-port:", ""));
            String tcpPortLine = br.readLine().replaceAll(" ", "");
            tcpServerPort = Integer.parseInt(tcpPortLine.replace("tcp-server-port:", ""));
            String minimunTrashLine = br.readLine().replaceAll(" ", "");
            minimunTrashPercentage = Integer.parseInt(minimunTrashLine.replace("minimun-trash-percentage:", ""));
            br.close();
            fr.close();
		}
	}	

	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof UDPServer) {
			if(((UDPServer) subject).getObj() instanceof Dumpster) {
				Dumpster dumpster = (Dumpster)((UDPServer) subject).getObj();
				dumpsters.put(((Dumpster)((UDPServer) subject).getObj()).getIdNumber(), dumpster);
				this.lastMessage = "A " + dumpster.getTypeName() + " was created with ID " + dumpster.getIdNumber();
				setChanged();
				notifyObservers();
			} else if (((UDPServer) subject).getObj() instanceof String){
				String obj = (String) ((UDPServer) subject).getObj();
				StringTokenizer st = new StringTokenizer(obj);
				int id = Integer.parseInt(st.nextToken());
				Double trashQuantity = Double.parseDouble(st.nextToken());
				Dumpster dumpster;
				
				if(dumpsters.containsKey(id)) {
					dumpster = dumpsters.get(id);				
					int regionId = dumpster.getRegionIdNumber();
					double capacity = dumpster.getMaxCapacity();
					DumpsterType type = dumpster.getType();				
					dumpster = new Dumpster(id, regionId, trashQuantity, capacity, type);
					dumpsters.put(id, dumpster);
				} else {
					dumpster = new Dumpster(id);
					dumpsters.put(id, dumpster);
					this.lastMessage = "Unknow dumpster was created with ID " + dumpster.getIdNumber();
					setChanged();
					notifyObservers();
				}				 
			}			
		} else if (subject instanceof TCPServer) {
			if(((TCPServer) subject).getInObj() instanceof String) {			
				StringTokenizer st = new StringTokenizer((String) ((TCPServer) subject).getInObj());
				int id = Integer.parseInt(st.nextToken());
				int pos = Integer.parseInt(st.nextToken());
				String route = getRoute(pos, id);
				runnableTcpServer.setOutObj(route);
				
				if (!drivers.containsKey(id)) {
					this.lastMessage = "A driver was created at region id " + id;
					setChanged();
					notifyObservers();
				}
				
				drivers.put(id, new Driver(id, pos, route));
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
	
	public List<Driver> getDriversList() {
		Set<Integer> keys = drivers.keySet();
		List<Driver> driversList = new ArrayList<Driver>();
		for(Integer key : keys) {
			if(key != null) {
				driversList.add(drivers.get(key));
			}
		}
		return driversList;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}

	public int getTrashCansQuantity() {
		return trashCansQuantity;
	}
	
	public int getTransferStationsQuantity() {
		return transferStationsQuantity;
	}
	
	public int getDriversQuantity() {
		return drivers.size();
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
	
	public void saveServerData() throws IOException {
		File file = new File("server.dat");
		
		if(!file.exists()) {
			file.createNewFile();
		}		
		
		FileOutputStream fos = new FileOutputStream(file);			 
		ObjectOutputStream oos = new ObjectOutputStream(fos); 
		oos.writeObject(dumpsters); 
		oos.flush(); 
		oos.close(); 
		fos.flush(); 
		fos.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadServerData() throws IOException, ClassNotFoundException {
		File file = new File("server.dat");
		
		if(file.exists()) {
			FileInputStream fis = new FileInputStream(file);	 
			ObjectInputStream ois = new ObjectInputStream(fis);	 
			Object obj = ois.readObject();
			
			if(obj instanceof Map<?, ?>) {
				dumpsters = (Map<Integer, Dumpster>) obj;
			}
			
			ois.close(); 
			fis.close();
		}		
	}
	
	public void saveServerLog(String log) throws IOException {
		File dir = new File("logs");
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		Date date = new Date();
		int logNumber = 1;
		File file = new File("logs/" + dateFormat.format(date) + "-" + Integer.toString(logNumber) + ".log");
		
		while(file.exists()) {
			logNumber++;
			file = new File("logs/" + dateFormat.format(date) + "-" + Integer.toString(logNumber) + ".log");
		}		
				
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(log);
        bw.close();
	}
	
	public String getRoute(int pos, int region) {
		List<Dumpster> dumpstersList = getDumpstersList();
		List<Dumpster> restDumpstersList = new ArrayList<Dumpster>();
		String route = new String();
		int closer = 0, closerIndex = 0, before = pos, lastPrioritized = pos;
		
		/* Removes all dumpsters outside the region 
		for(int x = 0; x < dumpstersList.size(); x++) {		
			if(dumpstersList.get(x).getRegionIdNumber() != region) {
				dumpstersList.remove(x);
				x--;
			}
		}*/
		
		if(!dumpstersList.isEmpty()) {
			closer =  dumpstersList.get(0).getIdNumber();
		}
		
		/* Plans the route to the priority dumps */
		while(!dumpstersList.isEmpty()) {
			for(int j = 0; j < dumpstersList.size(); j++) {
				Dumpster dumpster = dumpstersList.get(j);
				if(dumpster.getType().equals(DumpsterType.CAN) && dumpster.getTrashPercentage() >= minimunTrashPercentage) {
					if(closer != getCloser(before, dumpster.getIdNumber(), closer)) {
						closer = dumpster.getIdNumber();
						closerIndex = j;
					}
				}
			}
			if(!dumpstersList.isEmpty()) {
				Dumpster closerDumpster = dumpstersList.get(closerIndex);
				before = closer;
				if(closerDumpster.getType().equals(DumpsterType.CAN) && closerDumpster.getTrashPercentage() >= minimunTrashPercentage) {
					route += closer + " ";
					lastPrioritized = closer;
				} else {
					restDumpstersList.add(closerDumpster);
				}
				
				dumpstersList.remove(closerIndex);
				if(!dumpstersList.isEmpty()) {
					closer = dumpstersList.get(0).getIdNumber();
					closerIndex = 0;
				}
			}
		}
		
		if(!restDumpstersList.isEmpty()) {
			before = lastPrioritized;
			closer =  restDumpstersList.get(0).getIdNumber();
			closerIndex = 0;
		}		

		/* Plans the route to the unpriotirized dumps */
		while(!restDumpstersList.isEmpty()) {
			for(int j = 0; j < restDumpstersList.size(); j++) {
				Dumpster dumpster = restDumpstersList.get(j);
				if(dumpster.getType().equals(DumpsterType.CAN)) {
					if(closer != getCloser(before, dumpster.getIdNumber(), closer)) {
						closer = dumpster.getIdNumber();
						closerIndex = j;
					}
				}		
			}
			if(!restDumpstersList.isEmpty()) {
				Dumpster closerDumpster = restDumpstersList.get(closerIndex);
				before = closer;
				if(closerDumpster.getType().equals(DumpsterType.CAN)) {
					route += "." + closer + " ";
				}
				restDumpstersList.remove(closerIndex);
				if(!restDumpstersList.isEmpty()) {
					closer = restDumpstersList.get(0).getIdNumber();
					closerIndex = 0;
				}
			}
		}
		
		return route;		
	}
	
	private int getCloser(int current, int a, int b) {
		if(Math.abs(current-a) <= Math.abs(current-b)) {
			return a;
		} else {
			return b;
		}
	}
}
