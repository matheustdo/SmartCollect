package controller;
	
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * @author Matheus Teles
 */
public class DriverMainFxmlController implements Initializable, Observer {
	@FXML
    private TextField serverIpTextField;

    @FXML
    private TextField serverPortTextField;

    @FXML
    private TextField driverPosTextField;

    @FXML
    private Button turnOnButton;

    @FXML
    private CheckBox brokenChoicer;

    @FXML
    private Label statusTextField;

    @FXML
    private Label nextDumpsterLabel;

    @FXML
    private Label routeRestLabel;
    
    private DriverController driverController = new DriverController();
    private int position;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	statusTextField.setTextFill(Color.DARKRED);	
    	driverController.addObserver(this);
    }
    
    @FXML
    void turnOnButtonAction(ActionEvent event) throws InterruptedException, IOException {
    	position = Integer.parseInt(driverPosTextField.getText());
    	if(statusTextField.getText().equals("OFFLINE")) {
	    	driverController.turnClientOn(Integer.parseInt(serverPortTextField.getText()), serverIpTextField.getText());
	    	statusTextField.setText("ONLINE");
	    	statusTextField.setTextFill(Color.DARKGREEN);
	    	turnOnButton.setText("Update position");    	
	    	serverIpTextField.setDisable(true);
	    	serverPortTextField.setDisable(true);
    	} else {
    		driverController.setTcpOutObject(driverController.getId() + " " + 
					 driverPosTextField.getText() + " " + !brokenChoicer.isSelected());
    	}
    	
    	    	
    }
    
    /**
     * Generates a formatted route.
     */
	private void generateRoute() {
		if(driverController.getRoute() != null) {
			StringTokenizer st = new StringTokenizer(driverController.getRoute());
			routeRestLabel.setText("");
			
	    	if(st.hasMoreTokens()) {
				nextDumpsterLabel.setText(st.nextToken());
	    	}
	    	
	    	while(st.hasMoreTokens()) {
				routeRestLabel.setText(routeRestLabel.getText() + "-> " + st.nextToken() + " ");		
	    	}
		}
    }
	
	/**
	 * Breaks the truck.
	 */
	@FXML
    void brokenOnAction(ActionEvent event) {
		driverController.setTcpOutObject(driverController.getId() + " " + position + " " + !brokenChoicer.isSelected());    	
    }

	/**
	 * Updates view.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DriverController) {
			Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	driverController.setTcpOutObject(driverController.getId() + " " + position + " " + !brokenChoicer.isSelected());
    		    	generateRoute();
    		    }
    		});
		}		
	}
}
