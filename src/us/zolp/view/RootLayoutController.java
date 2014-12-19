package us.zolp.view;

import java.time.LocalDate;
import java.util.Arrays;

import us.zolp.MainApp;
import us.zolp.model.WeatherDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RootLayoutController {
	private MainApp mainApp;

	// List of items for the daysChoice ChoiceBox
	private ObservableList<Integer> daysList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7);
	// List of items for the unitChoice ChoiceBox
	private ObservableList<String> unitsList = FXCollections.observableArrayList("English", "Metric");
	// List of items for the formatChoice ChoiceBox
	private ObservableList<String> formatList = FXCollections.observableArrayList("12 Hours", "24 Hours");
	
	// Values to be used by selected items in the ChoiceBoxen
	private final String[] units = {"e", "m"};
	private final String[] formats = {"12 hourly", "24 hourly"};
	
	@FXML
	private Button goButton;
	@FXML
	private TextField zipField;
	@FXML
	private ChoiceBox<String> unitChoice;
	@FXML
	private ChoiceBox<String> formatChoice;
	@FXML
	private ChoiceBox<Integer> daysChoice;
	@FXML
	private DatePicker startPicker;
	
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    /**
     * Initializes the controls with default values
     */
    @FXML
    private void initialize() {
    	startPicker.setValue(LocalDate.now());
    	unitChoice.setItems(unitsList);
    	unitChoice.setValue(unitsList.get(0));
    	formatChoice.setItems(formatList);
    	formatChoice.setValue(formatList.get(1));
    	daysChoice.setItems(daysList);
    	daysChoice.setValue(daysList.get(0));
    }
    
    /**
     * Event handler for the Go Button
     */
    @FXML
    private void handleGo() {
    	// Get the ZIP code
    	String zip = zipField.getText();
    	// If user selected "English", use "e"; if "Metric", use "m" 
    	String units = null;
    	if (unitChoice.getValue().equalsIgnoreCase("English"))
    		units = this.units[0];
    	else
    		units = this.units[1];
    	
    	// If user selected "12 hours", use "12 hourly"; if "24 hours", use "24 hourly"
    	String format = null;
    	if (formatChoice.getValue().equalsIgnoreCase("12 Hours"))
    		format = formats[0];
    	else
    		format = formats[1];
    	// Get the date set in the DatePicker
    	LocalDate start = startPicker.getValue();
    	// get the value of the daysChoice ChoiceBox
    	int days = daysChoice.getValue();
    	
    	// Get the XML weather data
    	mainApp.getWeatherDataFromXml(zip, start, days, units, format);
    	// Set the TemperatureTrend's data
    	mainApp.getTemperatureTrendController().setTemperatureData(mainApp.getWeatherData());
    }
}
