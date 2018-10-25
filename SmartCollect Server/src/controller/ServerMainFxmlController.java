package controller;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
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
import model.Driver;
import model.Dumpster;
import util.Log;

/**
 * @author Matheus Teles
 */
public class ServerMainFxmlController implements Initializable, Observer {
	@FXML
    private Label ipNumberLabel;
	
	@FXML
    private Label areaIdTextLabel;
	
	@FXML
    private Label minimumTextLabel;

    @FXML
    private Label udpPortNumberLabel;
    
    @FXML
    private Label tcpPortNumberLabel;

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
    private TableColumn<Dumpster, String> dumpstersTypeColumn;

    @FXML
    private TableColumn<Dumpster, String> dumpstersLevelColumn;

    @FXML
    private TableColumn<Dumpster, String> dumpstersQuantityColumn;
    
    @FXML
    private TableColumn<Dumpster, String> dumpstersCapacityColumn;
    
    @FXML
    private TableView<Driver> driversTableView;

    @FXML
    private TableColumn<Driver, String> driversIdColumn;

    @FXML
    private TableColumn<Driver, Integer> driversPositionColumn;

    @FXML
    private TableColumn<Driver, String> driversRouteColumn;
    
    @FXML
    private TableColumn<Driver, String> driversStatusColumn;

    @FXML
    private TextArea logTextArea;
    
    @FXML
    private Label helpeningNumberLabel;

    @FXML
    private Label multicastIpLabel;

    @FXML
    private Label multicastPortLabel;
    
    private final DecimalFormat decimalFormat = new DecimalFormat("0");
    private ServerController serverController = new ServerController();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {			
			serverController.addObserver(this);
			serverController.loadServerData();
			logTextArea.appendText(Log.server("Loading properties" + "\n"));
			serverController.readServerConfigFile();
			logTextArea.appendText(Log.server("Properties loaded" + "\n"));
			logTextArea.appendText(Log.server("Starting UDP server" + "\n"));
			serverController.turnUdpServerOn();			
			logTextArea.appendText(Log.server("UDP server has initialized at " + 
					   serverController.getServerIp() + ":" +
					   serverController.getUdpServerPort()) + "\n");
			logTextArea.appendText(Log.server("Starting TCP server" + "\n"));
			serverController.turnTcpServerOn();	
			logTextArea.appendText(Log.server("TCP server has initialized at " + 
					   serverController.getServerIp() + ":" +
					   serverController.getTcpServerPort()) + "\n");
			logTextArea.appendText(Log.server("Starting multicast receiver" + "\n"));
			serverController.turnMulticastReceiverOn();
			logTextArea.appendText(Log.server("Multicasting initialized at " + 
					   serverController.getMulticastIp() + ":" +
					   serverController.getMulticastPort()) + "\n");
			
		} catch (IOException | ClassNotFoundException e) {
			logTextArea.appendText(Log.serverError(printStackTrace(e) + "\n"));
			e.printStackTrace();
		}
		pageUpdater();		
	}
	
	/**
	 * Write on log an exit message.
	 */
	public void exit() {
		logTextArea.appendText(Log.server("Closing server" + "\n"));		
		try {
			serverController.stopHelping();
			serverController.saveServerData();
			serverController.saveServerLog(logTextArea.getText().replaceAll("\n", System.getProperty("line.separator")));
		} catch (IOException e) {
			logTextArea.appendText(Log.serverError(e.getStackTrace() + "\n"));
			e.printStackTrace();
		}
		logTextArea.appendText(Log.server("Server closed" + "\n"));
	}
	
	/**
	 * Update server page.
	 */
	private void pageUpdater() {		
		new Thread(new Runnable() { 
            private ObservableList<Dumpster> dumpstersOl;
            private ObservableList<Driver> driversOl;

			public void run() {
					areaIdTextLabel.setText(serverController.getServerAreaId());
					minimumTextLabel.setText(Integer.toString(serverController.getMinimumTrashQuantity()));
	            	ipNumberLabel.setText(serverController.getServerIp() + " - " + serverController.getServerHostName());
	        		udpPortNumberLabel.setText(Integer.toString(serverController.getUdpServerPort()));
	        		tcpPortNumberLabel.setText(Integer.toString(serverController.getTcpServerPort()));	 
	        		dumpstersIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdNumber()).asObject());
	            	dumpstersTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypeName()));
	            	dumpstersLevelColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashPercentage()) + "%"));
	            	dumpstersQuantityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getTrashQuantity()) + "l"));
	            	dumpstersCapacityColumn.setCellValueFactory(data -> new SimpleStringProperty(decimalFormat.format(data.getValue().getMaxCapacity())+ "l"));
	            	driversIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
	            	driversPositionColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPos()).asObject());
	            	driversRouteColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoute()));
	            	driversStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
            	while(true) {    
            		serverController.updateDumpstersCounters();
            		Platform.runLater(new Runnable() {
            		    @Override
            		    public void run() {            		    	
                    		trashCansQuantityLabel.setText(Integer.toString(serverController.getTrashCansQuantity()));
                    		transferStationsQuantityLabel.setText(Integer.toString(serverController.getTransferStationsQuantity()));
                    		driversQuantityLabel.setText(Integer.toString(serverController.getDriversQuantity()));
                    		multicastIpLabel.setText(serverController.getMulticastIp());
                    		multicastPortLabel.setText(Integer.toString(serverController.getMulticastPort()));
                    		helpeningNumberLabel.setText(Integer.toString(serverController.getHelpingCansQuantity()));
            		    }
            		});            		     		
            		try {
            			dumpstersOl = FXCollections.observableArrayList(serverController.getDumpstersList());
						dumpstersTableView.setItems(dumpstersOl);		
						driversOl = FXCollections.observableArrayList(serverController.getDriversList());
						driversTableView.setItems(driversOl);
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}            		
            	}            	
            }
        }).start();
	}
	
	/**
	 * Write an exception stack trace.
	 * @param e Exception that will be opened.
	 * @return StackTrace message.
	 */
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
	
	@Override
	public void update(Observable subject, Object arg1) {
		if(subject instanceof ServerController) {
			Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
            		logTextArea.appendText(Log.server(serverController.getLastMessage() + "\n"));;
    		    }
    		});     
		}
	}
}
