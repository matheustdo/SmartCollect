package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DumpsterMain extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("fxml/DumpsterMain.fxml"));
		
		Scene scene = new Scene(root, 260, 380);
		
		primaryStage.setTitle("SmartColect Dumpster");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}	
}
