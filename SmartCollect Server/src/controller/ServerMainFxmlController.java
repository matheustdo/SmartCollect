package controller;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import util.Log;

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
    private Label driversQuantityLabel;

    @FXML
    private Accordion connectionsAccordion;
    
    @FXML
    private TableView<Dumpster> dumpstersTableView;
    
    @FXML
    private TableColumn<Dumpster, Integer> dumpstersIdColumn;
    
    @FXML
    private TableColumn<Dumpster, Integer> dumpstersRegionColumn;

    @FXML
    private TableColumn<Dumpster, String> dumpstersTypeColumn;

    @FXML
    private TableColumn<Dumpster, String> dumpstersLevelColumn;

    @FXML
    private TableColumn<Dumpster, String> dumpstersQuantityColumn;
    
    @FXML
    private TableColumn<Dumpster, String> dumpstersCapacityColumn;
    
    @FXML
    private TableView<?> driversTableView;

    @FXML
    private TableColumn<?, ?> driversIdColumn;
    
    @FXML
    private TableColumn<?, ?> driversRegionColumn;

    @FXML
    private TableColumn<?, ?> driversPositionColumn;

    @FXML
    private TableColumn<?, ?> driversRouteColumn;

    @FXML
    private TextArea logTextArea;
    
    private final DecimalFormat decimalFormat = new DecimalFormat("0");
    private ServerController serverController = new ServerController();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {			
			serverController.loadServerData();
			logTextArea.appendText(Log.server("Loading properties" + "\n"));
			serverController.readServerConfigFile();
			logTextArea.appendText(Log.server("Properties loaded" + "\n"));
			logTextArea.appendText(Log.server("Starting UDP server" + "\n"));
			serverController.turnUdpServerOn();			
			logTextArea.appendText(Log.server("UDP server has initialized at " + 
					   serverController.getServerIp() + 
					   serverController.getServerPort()) + "\n");
			logTextArea.appendText(Log.server("Starting TCP server" + "\n"));
			serverController.turnTcpServerOn();	
			logTextArea.appendText(Log.server("TCP server has initialized at " + 
					   serverController.getServerIp() + 
					   serverController.getServerPort()) + "\n");			
		} catch (IOException | ClassNotFoundException e) {
			logTextArea.appendText(Log.serverError(printStackTrace(e) + "\n"));
			e.printStackTrace();
		}
		pageUpdater();		
	}
	
	public void exit() {
		logTextArea.appendText(Log.server("Closing server" + "\n"));
		try {
			serverController.saveServerData();
			serverController.saveServerLog(logTextArea.getText().replaceAll("\n", System.getProperty("line.separator")));
		} catch (IOException e) {
			logTextArea.appendText(Log.serverError(e.getStackTrace() + "\n"));
			e.printStackTrace();
		}
		logTextArea.appendText(Log.server("Server closed" + "\n"));
	}
	
	private void pageUpdater() {		
		new Thread(new Runnable() { 
            private ObservableList<Dumpster> ol;

			public void run() {    
            	ipNumberLabel.setText(serverController.getServerIp());
        		portNumberLabel.setText(Integer.toString(serverController.getServerPort()));
            	dumpstersIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdNumber()).asObject());
            	dumpstersRegionColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRegionIdNumber()).asObject());
            	dumpstersTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypeName()));
            	dumpstersLevelColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashPercentage()) + "%"));
            	dumpstersQuantityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashQuantity()) + "l"));
            	dumpstersCapacityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getMaxCapacity())+ "l"));
            	while(true) {    
            		serverController.updateDumpstersCounters();
            		Platform.runLater(new Runnable() {
            		    @Override
            		    public void run() {
                    		trashCansQuantityLabel.setText(Integer.toString(serverController.getTrashCansQuantity()));
                    		transferStationsQuantityLabel.setText(Integer.toString(serverController.getTransferStationsQuantity()));
                    		driversQuantityLabel.setText(Integer.toString(serverController.getDriversQuantity()));
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
	
	private String printStackTrace(Exception e) {
		String stackTrace = e.toString() + "\n";
		StackTraceElement[] elements = e.getStackTrace();
		
		for(int i = 0; i < elements.length; i++) {
			StackTraceElement s = elements[i];
			stackTrace += "at " + s.getClassName() + "." + s.getMethodName();
			if(s.getFileName() == null) {
				stackTrace += "(Unknown Source)\n";
			} else {
				stackTrace += "(" + s.getFileName() + ":" + s.getLineNumber() + ")\n";
			}
		}
		
		return stackTrace;		
	}
}
