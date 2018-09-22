package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("fxml/ServerMain.fxml"));
		
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
