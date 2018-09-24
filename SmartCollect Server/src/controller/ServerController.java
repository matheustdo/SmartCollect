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

import model.Dumpster;
import model.DumpsterType;
import util.TCPServer;
import util.UDPServer;

public class ServerController implements Observer {
	private int serverPort,
				trashCansQuantity,
				transferStationsQuantity,
				driversQuantity,
				minimunTrashPercentage;
	private String serverIp;
	private UDPServer runnableUdpServer;
	private TCPServer runnableTcpServer;
	private Map<Integer, Dumpster> dumpsters;
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ServerController() {
		trashCansQuantity = 0;
		transferStationsQuantity = 0;
		driversQuantity = 0;
		minimunTrashPercentage = 80;
		dumpsters = new HashMap<Integer, Dumpster>();
	}
	
	public void turnUdpServerOn() throws UnknownHostException, SocketException {
		runnableUdpServer = new UDPServer(serverPort, serverIp);
		Thread threadUDPServer =  new Thread(runnableUdpServer);
		threadUDPServer.start();
		runnableUdpServer.addObserver(this);
	}
	
	public void turnTcpServerOn() throws IOException {
		runnableTcpServer = new TCPServer(getRoute(3), serverPort, serverIp);
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
        bw.write("#SmartCollect server properties\r\n" + "server-ip: " + serverIp +
        		 "\r\nserver-port: " + Integer.toString(serverPort) + "\r\nminimun-trash-percentage: " + 
        		 minimunTrashPercentage);
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
				dumpsters.put(((Dumpster)((UDPServer) subject).getObj()).getIdNumber(), ((Dumpster)((UDPServer) subject).getObj()));
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
	
	public int getDriversQuantity() {
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
	
	public String getRoute(int pos) {
		List<Dumpster> dumpstersList = getDumpstersList();
		String route = new String();
		int closer = 0, closerIndex = 0, before = pos;
		
		if(!dumpstersList.isEmpty()) {
			closer =  dumpstersList.get(0).getIdNumber();
		}
		
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
			
			/*if(closerIndex >= 0) {
				dumpstersList.remove(closerIndex);
			}*/
			
			before = closer;
			if(dumpstersList.get(closerIndex).getType().equals(DumpsterType.CAN)) {
				route += closer + " ";
			}	
			dumpstersList.remove(closerIndex);
			if(!dumpstersList.isEmpty()) {
				closer = dumpstersList.get(0).getIdNumber();
				closerIndex = 0;
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
