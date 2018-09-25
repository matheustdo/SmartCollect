package view;

import controller.ServerMainFxmlController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ServerMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ServerMain.fxml"));		
		Parent root = loader.load();		
		ServerMainFxmlController serverFxmlController = loader.getController();
		
		Scene scene = new Scene(root, 790, 480);		
		
		primaryStage.setTitle("SmartColect Server");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(710);
		primaryStage.setMinHeight(520);
		primaryStage.getIcons().add(new Image("/assets/icon.png"));
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			serverFxmlController.exit();
			System.exit(0);
			});
	}
}
