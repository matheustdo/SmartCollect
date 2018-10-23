package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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
import model.Helper;
import model.SCMProtocol;
import util.MulticastPublisher;
import util.MulticastReceiver;
import util.TCPServer;
import util.UDPServer;

/**
 * @author Matheus Teles
 */
public class ServerController extends Observable implements Observer {
	private int udpServerPort,
				tcpServerPort,
				multicastPort,
				trashCansQuantity,
				transferStationsQuantity,
				minimunTrashPercentage;
	private String serverIp,
				   multicastIp,
				   lastMessage,
				   areaId;
	private boolean driverStatus;
	private UDPServer runnableUdpServer;
	private TCPServer runnableTcpServer;
	private MulticastReceiver runnableMulticastReceiver;
	private Helper helper;
	private Map<Integer, Dumpster> dumpsters;
	private Map<String, Driver> drivers;
	private Map<String, Helper> supporting;
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Constructs a new ServerController.
	 */
	public ServerController() {
		this.serverIp = "localhost";
		this.multicastIp = "230.0.0.0";
		this.udpServerPort = 4065;
		this.tcpServerPort = 4066;
		this.multicastPort = 5000;
		this.trashCansQuantity = 0;
		this.transferStationsQuantity = 0;
		this.minimunTrashPercentage = 80;
		this.areaId = "0";
		this.lastMessage = "";
		this.helper = null;
		this.driverStatus = false;		
		this.dumpsters = new HashMap<Integer, Dumpster>();
		this.drivers = new HashMap<String, Driver>();
		this.supporting = new HashMap<String, Helper>();
		new HashMap<String, Helper>();
	}
	
	/**
	 * Turns on udp server.
	 * @throws IOException 
	 */
	public void turnUdpServerOn() throws IOException {
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
	 * Turns on multicasting.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public void turnMulticastReceiverOn() throws IOException {
		runnableMulticastReceiver = new MulticastReceiver(multicastPort, multicastIp);
		Thread threadMulticastReceiver =  new Thread(runnableMulticastReceiver);
		threadMulticastReceiver.start();
		runnableMulticastReceiver.addObserver(this);
	}

	/**
	 * Send a multicast message.
	 * @param message Message.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	private void sendMulticastMessage(String message) throws IOException {
		MulticastPublisher m = new MulticastPublisher();
		m.multicast(message, multicastIp, multicastPort);
	}	
	
	/**
	 * Returns the server ip.
	 * @return Server ip.
	 */
	public String getServerIp() {		
		return runnableUdpServer.getServerAdress();
	}
	
	/**
	 * Returns the multicast group ip.
	 * @return Multicast group ip.
	 */
	public String getMulticastIp() {
		return multicastIp;
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
	 * Returns the multicasting port.
	 * @return Multicast port.
	 */
	public int getMulticastPort() {
		return multicastPort;
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
        bw.write("#SmartCollect server properties\r\n" + "server-ip: " + serverIp + "\r\nmulticast-ip: " +
        		 multicastIp + "\r\nudp-server-port: " + Integer.toString(udpServerPort) + "\r\ntcp-server-port: " +
        		 Integer.toString(tcpServerPort) + "\r\nmulticast-port: " + Integer.toString(multicastPort) + 
        		 "\r\nminimun-trash-percentage: " + minimunTrashPercentage + "\r\nserver-area-id: " + areaId);
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
            String multicastIpLine = br.readLine().replaceAll(" ", "");
            multicastIp = multicastIpLine.replace("multicast-ip:", "");
            String udpPortLine = br.readLine().replaceAll(" ", "");            
            udpServerPort = Integer.parseInt(udpPortLine.replace("udp-server-port:", ""));
            String tcpPortLine = br.readLine().replaceAll(" ", "");
            tcpServerPort = Integer.parseInt(tcpPortLine.replace("tcp-server-port:", ""));
            String multicastPortLine = br.readLine().replaceAll(" ", "");
            multicastPort = Integer.parseInt(multicastPortLine.replace("multicast-port:", ""));
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
					// Updates a dumpster
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
				} else if(action == SCMProtocol.INFO) {
					// Receive server info to select the helper if the server wants a driver
					System.out.println("Entrando no protocolo udp.info");
					if(!driverStatus) {
						String helperArea = st.nextToken();
						int helperTrashCansQuantity = Integer.parseInt(st.nextToken());
						String helperIp = st.nextToken();
						int helperPort = Integer.parseInt(st.nextToken());
						Helper h = new Helper(helperArea, helperTrashCansQuantity, helperIp, helperPort);
						System.out.println("Helper: " + helperArea + " " +helperTrashCansQuantity+ " " +helperIp+ " " +helperPort);
						selectBestHelper(h);
						System.out.println("Best helper selecionado: " + helper.getArea());
						try {
							sendTcpMessage(SCMProtocol.INFO + " " + areaId + " " + serverIp + " " + 
										   tcpServerPort, helper.getIp(), helper.getPort());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}					
				}
			}
		} else if (subject instanceof TCPServer) {
			if(((TCPServer) subject).getInObj() instanceof String) {		
				StringTokenizer st = new StringTokenizer(((TCPServer) subject).getInObj().toString());
				int action = Integer.parseInt(st.nextToken());
				
				if(action == SCMProtocol.PROCESS) {
					String id = st.nextToken();
					int pos = Integer.parseInt(st.nextToken());
					String status = st.nextToken();
					String route = getRoute(pos);
					
					for(Helper h:getSupportingList()) {
						try {
							System.out.println("Area do helper: " + h.getArea());
							StringTokenizer ss = new StringTokenizer(getSupportingRoute(h.getIp(), h.getPort(), areaId, pos, status));
							action = Integer.parseInt(ss.nextToken());
							
							if(action == SCMProtocol.UPDATE) {
								while(ss.hasMoreTokens()) {
									route += ss.nextToken() + h.getArea() + " ";
								}
							}
						} catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
					
					runnableTcpServer.setOutObj(SCMProtocol.UPDATE + " " + route);				
					
					System.out.println(id.equals(areaId));
					if(id.equals(areaId)) {
						if (!drivers.containsKey(id)) {
							this.lastMessage = "A driver was created at region id " + id;
							setChanged();
							notifyObservers();
						}
						System.out.println("Criando a lixeira: " + id + " " + pos + " " + status);
						drivers.put(id, new Driver(id, pos, route, status));
						
						System.out.println("-Situacao do helper: " + (helper != null));
						System.out.println("+Situação do status: " + status.equals("true"));
						if(status.equals("false") && helper == null) {
							try {
								// Sends a help request
								System.out.println("Enviando multicast.help");
								sendMulticastMessage(SCMProtocol.HELP + " " + areaId + " " + serverIp + 
													 " " + udpServerPort);
								if(!supporting.isEmpty()) {
									for(Helper h:getSupportingList()) {
										sendTcpMessage(SCMProtocol.UPDATE + "", h.getIp(), h.getPort());
									}		
									supporting.clear();
								}
								driverStatus = false;
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (status.equals("true")) {
							driverStatus = true;
							if (helper != null) {
								try {
									sendTcpMessage(SCMProtocol.DELETE + " " + areaId, helper.getIp(), helper.getPort());
								} catch (IOException e) {
									e.printStackTrace();
								}
								helper = null;				
							 } 
						 }
					}
				} else if (action == SCMProtocol.INFO) {
					String area = st.nextToken();
					String ip = st.nextToken();
					int port = Integer.parseInt(st.nextToken());
					
					supporting.put(area, new Helper(area, 0, ip, port));
				} else if(action == SCMProtocol.DELETE) {
					String area = st.nextToken();
					supporting.remove(area);
				} else if(action == SCMProtocol.UPDATE) {
					// Sends a help request
					try {
						sendMulticastMessage(SCMProtocol.HELP + " " + areaId + " " + serverIp + 
											 " " + udpServerPort);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (subject instanceof MulticastReceiver) {
			StringTokenizer st = new StringTokenizer(((MulticastReceiver) subject).getReceivedStr());
			int action = Integer.parseInt(st.nextToken());
			if(action == SCMProtocol.HELP) {
				String helpAreaId = st.nextToken();
				// Sends help to another area if the driver status is true
				if(!helpAreaId.equals(areaId) && driverStatus) {
					String helpServerIp = st.nextToken();
					int helpUdpServerPort = Integer.parseInt(st.nextToken());
					
					try {
						sendUdpMessage(SCMProtocol.INFO + " " + areaId + " " + trashCansQuantity + 
									   " " + serverIp + " " + tcpServerPort, helpServerIp, helpUdpServerPort);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Gets a supporting route.
	 * @param serverIP
	 * @param serverPort
	 * @param id
	 * @param position
	 * @param status
	 * @return Route.
	 * @throws UnknownHostException Unknown host.
	 * @throws IOException IOException Signals that an I/O exception of some sort has occurred.
	 * @throws ClassNotFoundException Class not found.
	 */
	private String getSupportingRoute(String serverIP, int serverPort, String id, int position, String status) throws UnknownHostException, IOException, ClassNotFoundException {
		/* Connects to server socket */
		Socket socket = new Socket(InetAddress.getByName(serverIP), serverPort);
		/* Send output object to server */
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(SCMProtocol.PROCESS + " " + id + " " + position + " " + status);
		/* Receive input object from server */
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		String inObj = (String) ois.readObject();
		/* Update observers */
		setChanged();
		notifyObservers();
		/* Close socket and sleep thread */
		socket.close();	
		return inObj;
	}
	
	/**
	 * Select the best helper.
	 * @param h Helper to compare.
	 */
	private void selectBestHelper(Helper h) {
		if(helper == null || helper.getTrashCansQuantity() > h.getTrashCansQuantity()) {
			this.helper = h;			
		}		
	}
	
	/**
	 * Sends a tcp message.
	 * @param message Message to send.
	 * @param ip Destination ip.
	 * @param port Destination port.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	private void sendTcpMessage(String message, String ip, int port) throws IOException {
		Socket socket = new Socket(InetAddress.getByName(ip), port);
		/* Send output object to server */
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(message);
		/* Close socket */
		socket.close();
	}
	
	/**
	 * Sends a udp message.
	 * @param message Message to send.
	 * @param ip Destination ip.
	 * @param port Destination port.
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	private void sendUdpMessage(String message, String ip, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        DatagramPacket datagramPacket;
        /* Send output object */
    	oos.writeObject(message);
        oos.close();
        byte[] objData = baos.toByteArray();
    	datagramPacket = new DatagramPacket(objData, objData.length, InetAddress.getByName(ip), port);
        socket.send(datagramPacket);
        /* Close socket */
        socket.close();
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
		Set<String> keys = drivers.keySet();
		List<Driver> driversList = new ArrayList<Driver>();
		for(String key : keys) {
			if(key != null) {
				driversList.add(drivers.get(key));
			}
		}
		return driversList;
	}
	
	/**
	 * Returns a list that contains all current supporting servers.
	 * @return A servers list.
	 */
	public List<Helper> getSupportingList() {
		Set<String> keys = supporting.keySet();
		List<Helper> supportingList = new ArrayList<Helper>();
		for(String key : keys) {
			if(key != null) {
				supportingList.add(supporting.get(key));
			}
		}
		return supportingList;
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
		int closer = 0, closerIndex = 0, before = pos; //lastPrioritized = pos;
		
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
					//lastPrioritized = closer;
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
		
		/* Reset counters variables 
		if(!restDumpstersList.isEmpty()) {
			before = lastPrioritized;
			closer =  restDumpstersList.get(0).getIdNumber();
			closerIndex = 0;
		} */

		/* Plans the route to the unpriotirized dumps 
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
		}*/
		
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
