package util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunnable implements Runnable {
	private ServerSocket server;
	
	public ServerRunnable() throws IOException {
		server = new ServerSocket(25565);
	}
	
	public void run() {
		while(!server.isClosed()) {
			Socket socket;			
			try {				
				socket = server.accept();
				socket.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public ServerSocket getServer()  {
		return server;
	}
}
