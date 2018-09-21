package view;

import controller.ServerMainFxmlController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPServer;

public class ServerMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ServerMain.fxml"));		
		Parent root = loader.load();		
		ServerMainFxmlController serverMainFxmlController = loader.getController();
		
		Scene scene = new Scene(root);		
		
		primaryStage.setTitle("SmartColect Server");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(675);
		primaryStage.setMinHeight(520);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
			});
	}
}
