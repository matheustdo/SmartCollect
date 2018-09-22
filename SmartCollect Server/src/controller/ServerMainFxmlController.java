package controller;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import model.Dumpster;
import model.DumpsterType;

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
    private TableView<Dumpster> dumpstersTableView;
    
    @FXML
    private TableColumn<Dumpster, Integer> idColumn;

    @FXML
    private TableColumn<Dumpster, String> typeColumn;

    @FXML
    private TableColumn<Dumpster, String> levelColumn;

    @FXML
    private TableColumn<Dumpster, String> quantityColumn;
    
    @FXML
    private TableColumn<Dumpster, String> capacityColumn;

    @FXML
    private TextArea logTextArea;
    
    private final DecimalFormat decimalFormat = new DecimalFormat("0");
    private ServerController serverController = new ServerController();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			serverController.readServerConfigFile();
			serverController.turnServerOn();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pageUpdater();		
		updateInfo();		
	}    
	
	public void updateInfo() {
		
	}
	
	private void pageUpdater() {		
		new Thread(new Runnable() { 
            private ObservableList<Dumpster> ol;

			public void run() {    
            	ipNumberLabel.setText(serverController.getServerIp());
        		portNumberLabel.setText(Integer.toString(serverController.getServerPort()));
            	idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdNumber()).asObject());
            	typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypeName()));
            	levelColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashPercentage())));
            	quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashQuantity())));
            	capacityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getMaxCapacity())));
            	while(true) {    
            		serverController.updateDumpstersCounters();
            		Platform.runLater(new Runnable() {
            		    @Override
            		    public void run() {
                    		trashCansQuantityLabel.setText(Integer.toString(serverController.getTrashCansQuantity()));
                    		transferStationsQuantityLabel.setText(Integer.toString(serverController.getTransferStationsQuantity()));
                    		motoristsQuantityLabel.setText(Integer.toString(serverController.getMotoristsQuantity()));
            		    }
            		});            		     		
            		try {
            			ol = FXCollections.observableArrayList(serverController.getDumpstersList());
						dumpstersTableView.setItems(ol);						
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}            		
            	}            	
            }
        }).start();
	}
}
