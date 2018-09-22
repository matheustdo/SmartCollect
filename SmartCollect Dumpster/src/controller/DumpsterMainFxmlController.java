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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class DumpsterMainFxmlController implements Initializable {
	@FXML
    private Label levelLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label statusTextField;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private ChoiceBox<String> typeChoicer;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField capacityTextField;

    @FXML
    private Button turnOnButton;

    @FXML
    private Slider quantitySlider;
    
    private final DecimalFormat decimalFormat = new DecimalFormat("0");
    private DumpsterController dumpsterController = new DumpsterController();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		quantitySlider.setDisable(true);
		statusTextField.setTextFill(Color.DARKRED);		
		typeChoicer.setItems(FXCollections.observableArrayList("Trash can", "Transfer station"));
		typeChoicer.getSelectionModel().selectFirst();
	}
	
	@FXML
	void onMouseDragged(MouseEvent event) {
		levelLabel.setText(decimalFormat.format(quantitySlider.getValue()) + "%");
		quantityLabel.setText(decimalFormat.format(dumpsterController.updateTrashQuantity(quantitySlider.getValue())) + "/" + capacityTextField.getText() + " l");
		dumpsterController.changeServerObj();
	}
	
	@FXML
    void turnOnAction(ActionEvent event) throws UnknownHostException, SocketException, InterruptedException {
		createDumpster();
		startClient();
		toggleElements();
		quantityLabel.setText("0" + "/" + capacityTextField.getText() + " l");
    }
	
	private void createDumpster() {
		int id = Integer.parseInt(idTextField.getText());
		double capacity = Double.parseDouble(capacityTextField.getText());
		String type = typeChoicer.getSelectionModel().selectedItemProperty().getValue();
		dumpsterController.createDumpster(id, capacity, type);
	}
	
	private void startClient() throws UnknownHostException, SocketException, InterruptedException {
		int port = Integer.parseInt(portTextField.getText());
		String address = ipTextField.getText();
		dumpsterController.turnClientOn(port, address);
		statusTextField.setText("ONLINE");
		statusTextField.setTextFill(Color.DARKGREEN);
	}
	
	private void toggleElements() {
		ipTextField.setDisable(true);
		portTextField.setDisable(true);
		typeChoicer.setDisable(true);
		idTextField.setDisable(true);
		capacityTextField.setDisable(true);
		turnOnButton.setDisable(true);		
		quantitySlider.setDisable(false);
	}
}