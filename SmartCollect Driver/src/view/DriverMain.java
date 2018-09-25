package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DriverMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("fxml/DriverMain.fxml"));
		
		Scene scene = new Scene(root, 302, 323);
		
		primaryStage.setTitle("SmartColect Driver");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("/assets/icon.png"));
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
			});
	}	
}
