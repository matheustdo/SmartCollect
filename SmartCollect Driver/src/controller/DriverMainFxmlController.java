package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class DriverMainFxmlController implements Initializable {
	 	@FXML
	    private TextField driverIdTextField;

	    @FXML
	    private TextField driverPosTextField;

	    @FXML
	    private Button turnOnButton;

	    @FXML
	    private Label statusTextField;

	    @FXML
	    private Label nextDumpsterLabel;

	    @FXML
	    private Label routeRestLabel;
	    
	    private DriverController driverController = new DriverController();
	    
	    @Override
	    public void initialize(URL arg0, ResourceBundle arg1) {
	    	statusTextField.setTextFill(Color.DARKRED);	
	    }
	    
	    @FXML
	    void turnOnButtonAction(ActionEvent event) throws InterruptedException, IOException {
	    	if(statusTextField.getText().equals("OFFLINE")) {
		    	driverController.turnClientOn(4065, "localhost");
		    	statusTextField.setText("ONLINE");
		    	statusTextField.setTextFill(Color.DARKGREEN);
		    	turnOnButton.setText("Update position");
		    	driverIdTextField.setDisable(true);
	    	}
	    	generateRoute();
	    }
	    
	    private void generateRoute() {
	    	StringTokenizer st = new StringTokenizer(driverController.getRoute());
	    	nextDumpsterLabel.setText("");
	    	routeRestLabel.setText("");
	    	if(st.hasMoreTokens()) {
	    		nextDumpsterLabel.setText(st.nextToken());
	    	}
	    	while(st.hasMoreTokens()) {
	    		routeRestLabel.setText(routeRestLabel.getText() + "-> " + st.nextToken() + " ");
	    	}
	    }
}
