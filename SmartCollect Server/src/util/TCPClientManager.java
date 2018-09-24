package util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPClientManager extends Thread {
	private Socket clientSocket;
	
	public TCPClientManager(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.start();
	}

	@Override
	public void run() {		
		while(true) {
			try {
				System.out.println(clientSocket.getInetAddress() + Integer.toString(clientSocket.getPort()));
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.flush();
				oos.writeObject("a");				
				oos.close();							
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
