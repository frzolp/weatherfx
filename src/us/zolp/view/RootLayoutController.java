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
	
	enum Units {
		ENGLISH("e"),
		METRIC("m");
		
		private final String value;
		
		private Units(final String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	enum Format {
		TWELVE("12 hourly"),
		TWENTYFOUR("24 hourly");
		
		private final String value;
		
		private Format(final String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	private ObservableList<Integer> daysList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7);
	private ObservableList<String> unitsList = FXCollections.observableArrayList("English", "Metric");
	private ObservableList<String> formatList = FXCollections.observableArrayList("12 Hours", "24 Hours");
	
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
	@FXML
	private Label highLabel;
	@FXML
	private Label lowLabel;
	@FXML
	private Label precipLabel;
	@FXML
	private Label conditionsLabel;
	@FXML
	private Label urlLabel;
	@FXML
	private ListView<WeatherDay> weatherList;
	@FXML
	private ImageView conditionImage;
	
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        weatherList.setItems(mainApp.getWeatherData());
    }
    
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    @FXML
    private void initialize() {
    	startPicker.setValue(LocalDate.now());
    	unitChoice.setItems(unitsList);
    	unitChoice.setValue(unitsList.get(0));
    	formatChoice.setItems(formatList);
    	formatChoice.setValue(formatList.get(1));
    	daysChoice.setItems(daysList);
    	daysChoice.setValue(daysList.get(0));
    	
    	weatherList.setCellFactory(lc -> new ListCell<WeatherDay>() {
    		@Override
    		protected void updateItem(WeatherDay t, boolean b) {
    			super.updateItem(t, b);
    			if (t != null) {
    				setText(t.getDay());
    			} else {
    				setText("");
    			}
    		}
    	});
    	
    	weatherList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
    			showWeatherDetails(newValue));
    	weatherList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void showWeatherDetails(WeatherDay weatherDay) {
		if (weatherDay != null) {
			lowLabel.setText(Integer.toString(weatherDay.getLowTemp()));
			highLabel.setText(Integer.toString(weatherDay.getHighTemp()));
			precipLabel.setText(Integer.toString(weatherDay.getChancePrecipitation()));
			conditionsLabel.setText(weatherDay.getConditions());
			urlLabel.setText(weatherDay.getConditionIcon());
			conditionImage.setImage(new Image(weatherDay.getConditionIcon()));
			//mainApp.showTemperatureTrend();
		} else {
			lowLabel.setText("");
			highLabel.setText("");
			precipLabel.setText("");
			conditionsLabel.setText("");
		}
	}
    
    @FXML
    private void handleGo() {
    	String zip = zipField.getText();
    	String units = null;
    	if (unitChoice.getValue().equalsIgnoreCase("English"))
    		units = this.units[0];
    	else
    		units = this.units[1];
    	String format = null;
    	if (formatChoice.getValue().equalsIgnoreCase("12 Hours"))
    		format = formats[0];
    	else
    		format = formats[1];
    	LocalDate start = startPicker.getValue();
    	int days = daysChoice.getValue();
    	
    	mainApp.getWeatherDataFromXml(zip, start, days, units, format);
    	
    }
}
