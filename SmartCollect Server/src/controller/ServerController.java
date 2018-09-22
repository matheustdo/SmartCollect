package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import model.Dumpster;
import util.UDPServer;

public class ServerController implements Observer {
	private int serverPort;
	private String serverIp;
	private UDPServer runnableUdpServer;
	private Map<Integer, Dumpster> dumpsters;
	
	public ServerController() {
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
	
	public int getTrashCansQuantity() {
		return 0;
	}
	
	public int getTransferStationsQuantity() {
		return 0;
	}
	
	public int getMotoristsQuantity() {
		return 0;
	}
	
	public Map<Integer, Dumpster> getDumpsters() {
		return dumpsters;
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

	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof UDPServer) {
			dumpsters.put(((Dumpster)((UDPServer) subject).getObj()).getIdNumber(), ((Dumpster)((UDPServer) subject).getObj()));
		}		
	}
}