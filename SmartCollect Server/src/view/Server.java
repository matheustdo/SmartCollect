package view;

import controller.ServerFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPServer;

public class Server extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));		
		Parent root = loader.load();		
		ServerFXMLController serverFXMLController = loader.getController();
		
		Scene scene = new Scene(root);		
		
		primaryStage.setTitle("SmartColect Server");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(675);
		primaryStage.setMinHeight(520);
		primaryStage.show();
		
		Runnable UDPRunnableServer = new UDPServer(25565, "localhost");
		Thread threadUDPServer =  new Thread(UDPRunnableServer);
		threadUDPServer.start();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
			});
		
		serverFXMLController.updateInfo(((UDPServer)UDPRunnableServer).getServerSocket().getLocalAddress().getHostAddress(), 
				((UDPServer)UDPRunnableServer).getServerSocket().getLocalPort(), 0, 0, 0);
	}
}
