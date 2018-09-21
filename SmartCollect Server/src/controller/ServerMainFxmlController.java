package controller;

import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ServerMainFxmlController implements Initializable {
	@FXML
    private Label ipNumberLabel;

    @FXML
    private Label portNumberLabel;

    @FXML
    private Label trashCansQuantityLabel;

    @FXML
    private Label transferStationsQuantityLabel;

    @FXML
    private Label motoristsQuantityLabel;

    @FXML
    private Accordion connectionsAccordion;

    @FXML
    private TextArea logTextArea;
    
    private ServerController serverController = new ServerController();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			serverController.turnServerOn();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
		updateInfo();		
	}    
	
	public void updateInfo() {
		ipNumberLabel.setText(serverController.getServerIp());
		portNumberLabel.setText(Integer.toString(serverController.getServerPort()));
		trashCansQuantityLabel.setText(Integer.toString(serverController.getTrashCansQuantity()));
		transferStationsQuantityLabel.setText(Integer.toString(serverController.getTransferStationsQuantity()));
		motoristsQuantityLabel.setText(Integer.toString(serverController.getMotoristsQuantity()));
	}
}
