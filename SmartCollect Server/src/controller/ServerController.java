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
import model.SCMProtocol;
import util.TCPServer;
import util.UDPServer;

/**
 * @author Matheus Teles
 */
public class ServerController extends Observable implements Observer {
	private int udpServerPort,
				tcpServerPort,
				trashCansQuantity,
				transferStationsQuantity,
				minimunTrashPercentage;
	private String serverIp,
				   lastMessage,
				   areaId;
	private UDPServer runnableUdpServer;
	private TCPServer runnableTcpServer;
	private Map<Integer, Dumpster> dumpsters;
	private Map<Integer, Driver> drivers;
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Constructs a new ServerController.
	 */
	public ServerController() {
		this.serverIp = "localhost";
		this.udpServerPort = 4065;
		this.tcpServerPort = 4066;
		this.trashCansQuantity = 0;
		this.transferStationsQuantity = 0;
		this.minimunTrashPercentage = 80;
		this.areaId = "0";
		this.lastMessage = "";
		this.dumpsters = new HashMap<Integer, Dumpster>();
		this.drivers = new HashMap<Integer, Driver>();
	}
	
	/**
	 * Turns on udp server.
	 * @throws UnknownHostException Indicates that the IP address of a host could not be determined.
	 * @throws SocketException Indicates that there is an error creating or accessing a Socket.
	 */
	public void turnUdpServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(udpServerPort, serverIp);
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();
		runnableUdpServer.addObserver(this);
	}
	
	/**
	 * Turns on tcp server.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public void turnTcpServerOn() throws IOException {
		runnableTcpServer = new TCPServer(tcpServerPort, serverIp);
		Thread threadTCPServer =  new Thread(runnableTcpServer);
		threadTCPServer.start();
		runnableTcpServer.addObserver(this);
	}
	
	/**
	 * Returns the server ip.
	 * @return Server ip.
	 */
	public String getServerIp() {		
		return runnableUdpServer.getServerAdress();
	}
	
	/**
	 * Returns the server host name.
	 * @return Server host name.
	 */
	public String getServerHostName() {
		return runnableUdpServer.getServerHostName();
	}
	
	/**
	 * Returns the udp server port.
	 * @return Udp server port.
	 */
	public int getUdpServerPort() {
		return runnableUdpServer.getServerPort();
	}
	
	/**
	 * Returns the tcp server port.
	 * @return Tcp server port.
	 */
	public int getTcpServerPort() {
		return runnableTcpServer.getServerPort();
	}
	
	/**
	 * Returns a hashmap that contains all dumpsters.
	 * @return Hashmap with all dumpsters.
	 */
	public Map<Integer, Dumpster> getDumpsters() {
		return dumpsters;
	}
	
	/**
	 * Generate a server config file with server ip, udp server port,
	 * tcp server port and a minimal trash percentage.
	 * @param file Config file.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	private void createServerConfigFile(File file) throws IOException {		
		serverIp = "localhost";
		
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("#SmartCollect server properties\r\n" + "server-ip: " + serverIp +
        		 "\r\nudp-server-port: " + Integer.toString(udpServerPort) + "\r\ntcp-server-port: " +
        		 Integer.toString(tcpServerPort) + "\r\nminimun-trash-percentage: " + minimunTrashPercentage +
        		 "\r\nserver-area-id: " + areaId);
        bw.close();
        fw.close();
	}
	
	/**
	 * Reads the config server file.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
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
            String areaIdLine = br.readLine().replaceAll(" ", "");
            areaId = areaIdLine.replace("server-area-id:", "");
            br.close();
            fr.close();
		}
	}	
	
	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof UDPServer) {
			if(((UDPServer) subject).getObj() instanceof String) {
				StringTokenizer st = new StringTokenizer(((UDPServer) subject).getObj().toString());
				int action = Integer.parseInt(st.nextToken());
				// The first object received creates a new dumpster
				if(action == SCMProtocol.CREATE) {
					int id = Integer.parseInt(st.nextToken());
					Double maxCapacity = Double.parseDouble(st.nextToken());
					DumpsterType dumpsterType = convertDumpsterType(st.nextToken());					
					Dumpster dumpster = new Dumpster(id, maxCapacity, dumpsterType);
					dumpsters.put(id, dumpster);
					this.lastMessage = "A " + dumpster.getTypeName() + " was created with ID " + id;
					setChanged();
					notifyObservers();
				} else if (action == SCMProtocol.UPDATE){
					int id = Integer.parseInt(st.nextToken());
					Double trashQuantity = Double.parseDouble(st.nextToken());
					Dumpster dumpster;
					
					if(dumpsters.containsKey(id)) {
						dumpster = dumpsters.get(id);
						dumpster = new Dumpster(id, dumpster.getMaxCapacity(), dumpster.getType(), trashQuantity);
						dumpsters.put(id, dumpster);
					} else {
						dumpster = new Dumpster(id);
						dumpsters.put(id, dumpster);
						this.lastMessage = "A" + dumpster.getTypeName() + " was created with ID " + id;
						setChanged();
						notifyObservers();
					}					
				}		
			}
		} else if (subject instanceof TCPServer) {
			if(((TCPServer) subject).getInObj() instanceof String) {		
				StringTokenizer st = new StringTokenizer(((TCPServer) subject).getInObj().toString());
				int action = Integer.parseInt(st.nextToken());
				
				if(action == SCMProtocol.PROCESS) {
					int id = Integer.parseInt(st.nextToken());
					int pos = Integer.parseInt(st.nextToken());
					String status = st.nextToken();
					String route = getRoute(pos);
					runnableTcpServer.setOutObj(SCMProtocol.UPDATE + " " + route);
					
					if (!drivers.containsKey(id)) {
						this.lastMessage = "A driver was created at region id " + id;
						setChanged();
						notifyObservers();
					}
					
					drivers.put(id, new Driver(id, pos, route, status));
				}
			}
		}
	}
	
	/**
	 * Convert a string to an dumpster type
	 * @param type String with dumpster type name
	 * @return A DumpsterType object
	 */
	private DumpsterType convertDumpsterType(String type) {
		if(type.equals("CAN")) {
			return DumpsterType.CAN;
		} else if (type.equals("STATION")) {
			return DumpsterType.STATION;
		} else {
			return DumpsterType.UNKNOW;
		}
	}
	
	/**
	 * Returns a list that contains all dumpsters.
	 * @return A dumpsters list.
	 */
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
	
	/**
	 * Returns a list that contains all drivers.
	 * @return A drivers list.
	 */
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
	
	/**
	 * Returns the last log message.
	 * @return Last log message.
	 */
	public String getLastMessage() {
		return lastMessage;
	}

	/**
	 * Returns the quantity of trash cans.
	 * @return Trash cans quantity.
	 */
	public int getTrashCansQuantity() {
		return trashCansQuantity;
	}
	
	/**
	 * Returns the quantity of transfer stations.
	 * @return Transfer stations quantity.
	 */
	public int getTransferStationsQuantity() {
		return transferStationsQuantity;
	}
	
	/**
	 * Returnts the quantity of drivers.
	 * @return Drivers quantity.
	 */
	public int getDriversQuantity() {
		return drivers.size();
	}
	
	/**
	 * Update dumpsters counters.
	 */
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
	
	/**
	 * Persists the system.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
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
	
	/**
	 * Loads server data.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 * @throws ClassNotFoundException Throws if the class is not found.
	 */
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
	
	/**
	 * Saves an file that contains a log.
	 * @param log Log string.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
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
	
	/**
	 * Returns a string with a route from an informed position and region.
	 * Dumpsters that has more than the minimun trash percentage are considered priority.
	 * Dumpsters that isn't piority has a dot before on route.
	 * @param pos Driver actual position.
	 * @param region Driver actual region.
	 * @return A string that contains a route.
	 */
	public String getRoute(int pos) {
		List<Dumpster> dumpstersList = getDumpstersList();
		List<Dumpster> restDumpstersList = new ArrayList<Dumpster>();
		String route = new String();
		int closer = 0, closerIndex = 0, before = pos, lastPrioritized = pos;
		
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
					/* Adds unpriotirizeds dumpsters on another list */
					restDumpstersList.add(closerDumpster);
				}
				
				dumpstersList.remove(closerIndex);
				if(!dumpstersList.isEmpty()) {
					closer = dumpstersList.get(0).getIdNumber();
					closerIndex = 0;
				}
			}
		}
		
		/* Reset counters variables */
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
	
	/**
	 * Returns the closer position from an actual position.
	 * @param current Actual position.
	 * @param a First position.
	 * @param b Second position.
	 * @return Closer position.
	 */
	private int getCloser(int current, int a, int b) {
		if(Math.abs(current-a) <= Math.abs(current-b)) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * Get server area id
	 * @return Area id
	 */
	public String getServerAreaId() {
		return areaId;
	}

	/**
	 * Get server minimun trash percentage
	 * @return Minimum trash percentage
	 */
	public int getMinimumTrashQuantity() {
		return minimunTrashPercentage;
	}
}
