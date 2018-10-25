package view;

import controller.DriverMainFxmlController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Matheus Teles
 */
public class DriverMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/DriverMain.fxml"));
		Parent root = loader.load();
		DriverMainFxmlController driverFxmlController = loader.getController();
		
		Scene scene = new Scene(root, 302, 377);
		
		primaryStage.setTitle("SmartColect Driver");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("/assets/icon.png"));
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			driverFxmlController.exit();
			System.exit(0);
			});
	}	
}
