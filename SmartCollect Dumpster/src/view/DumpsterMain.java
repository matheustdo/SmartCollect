package view;

import controller.DumpsterMainFxmlController;
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/DumpsterMain.fxml"));		
		Parent root = loader.load();		
		DumpsterMainFxmlController dumpsterMainFxmController = loader.getController();
		
		Scene scene = new Scene(root, 297, 350);
		
		primaryStage.setTitle("SmartColect Dumpster");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
			});
	}	
}
