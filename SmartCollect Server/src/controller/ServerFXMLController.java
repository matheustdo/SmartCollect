package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import view.Server;

public class ServerFXMLController implements Initializable {
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}    
	
	public void updateInfo(String ipNumber, int portNumber, int dumpstersQuantity, int stationsQuantity, int motoristsQuantity) {
		ipNumberLabel.setText(ipNumber);
		portNumberLabel.setText(Integer.toString(portNumber));
		trashCansQuantityLabel.setText(Integer.toString(dumpstersQuantity));
		transferStationsQuantityLabel.setText(Integer.toString(stationsQuantity));
		motoristsQuantityLabel.setText(Integer.toString(motoristsQuantity));
	}
}
