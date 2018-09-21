package controller;

import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import model.Dumpster;
import model.DumpsterType;
import model.NoSpaceException;
import util.UDPClient;

public class DumpsterMainFxmlController implements Initializable {
	@FXML
    private Label levelLabel;

    @FXML
    private TextField serverIPTextField;

    @FXML
    private TextField serverPortTextField;

    @FXML
    private ChoiceBox<String> typeChoicer;

    @FXML
    private TextField streetAddressTextField;

    @FXML
    private TextField capacityTextField;

    @FXML
    private TextField incrementTextField;

    @FXML
    private Button increaseTrashButton;

    @FXML
    private Button turnOnButton;

    @FXML
    private Button emptyItButton;
    
    @FXML
    private Label serverStatusTextField;
    
    private Dumpster dumpster;
    private String serverIP;
    private int serverPort;
    private boolean serverStatus;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.#");
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		serverStatus = false;
		incrementTextField.setDisable(true);
		increaseTrashButton.setDisable(true);
		emptyItButton.setDisable(true);
		serverStatusTextField.setTextFill(Color.RED);
		
		typeChoicer.setItems(FXCollections.observableArrayList("Trash can", "Transfer station"));
		typeChoicer.getSelectionModel().selectFirst();
		
		levelLabel.setText(0 + "%");
	}
	
	@FXML
    void turnOnAction(ActionEvent event) {
		if(serverStatus == false) {
			serverIP = serverIPTextField.getText();
			try {			
				serverPort = Integer.parseInt(serverPortTextField.getText());
				if(typeChoicer.getSelectionModel().selectedItemProperty().getValue().equals("Trash can")) {
					try {
					dumpster = new Dumpster(Double.parseDouble(capacityTextField.getText()), 
							streetAddressTextField.getText(), DumpsterType.CAN);
					turnServerOn();
					} catch (Exception e) {
						capacityTextField.setText("Insert a correct value");
					}
				} else {
					try {
					dumpster = new Dumpster(Double.parseDouble(capacityTextField.getText()), 
							streetAddressTextField.getText(), DumpsterType.STATION);
					turnServerOn();
					} catch (Exception e) { 
						capacityTextField.setText("Insert a correct value");
					}
				}
			} catch (Exception e) {
				serverPortTextField.setText("Insert a correct value");
			}
		} else {
			try {
				dumpster.setMaxCapacity(Double.parseDouble(capacityTextField.getText()));
			} catch (Exception e) {
				capacityTextField.setText("Insert a correct value");
			}
		}
    }
	
	private void turnServerOn() {
		Runnable runnableClient;
		try {
			runnableClient = new UDPClient(serverPort, serverIP, dumpster);
			Thread threadClient =  new Thread(runnableClient);
			threadClient.start();
			
			levelLabel.setText(decimalFormat.format(dumpster.getTrashLevel()) + "%");
			serverIPTextField.setDisable(true);
			serverPortTextField.setDisable(true);
			typeChoicer.setDisable(true);
			turnOnButton.setText("Update dumpster");
			serverStatus = true;
			serverStatusTextField.setText("SENDING DATA");
			serverStatusTextField.setTextFill(Color.GREEN);
			incrementTextField.setDisable(false);
			increaseTrashButton.setDisable(false);
			emptyItButton.setDisable(false);
		} catch (UnknownHostException e) {
			serverIPTextField.setText("Insert a correct IP");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
    void emptyOnAction(ActionEvent event) {
		dumpster.setEmpty();
    }

    @FXML
    void increaseOnAction(ActionEvent event) {
    	try {
    		dumpster.increaseTrash(Double.parseDouble(incrementTextField.getText()));
    	} catch (Exception e) {
    		incrementTextField.setText("Insert a correct value");
    	} catch (NoSpaceException e) {
    		incrementTextField.setText("No space to it");
		}
    }
}