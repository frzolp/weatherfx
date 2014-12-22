package us.zolp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import us.zolp.MainApp;
import us.zolp.model.WeatherDay;

public class WeatherOverviewController {
	@FXML
	private Label lowTempLabel;
	@FXML
	private Label highTempLabel;
	@FXML
	private Label chancePrecipitationLabel;
	@FXML
	private ImageView conditionImage;
	@FXML
	private Label conditionsLabel;
	@FXML
	private ListView<WeatherDay> weatherList;
	
	private MainApp mainApp;
	
	public WeatherOverviewController() {
		
	}
	
	@FXML
	private void initialize() {
		/*
		 * Customize the CellFactory of the ListView, because by default,
		 * each cell will contain the value of .toString(). Instead, show
		 * the date for each cell.
		 */
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
    	
		/*
		 * Add a listener for the selectedItemProperty event. When a cell is clicked, display the
		 * details of the selected WeatherDay.
		 */
    	weatherList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
    			showWeatherDetails(newValue));
    	
    	// We don't want any funny business: only one day can be selected at a time.
    	weatherList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	/**
	 * Displays the details of a given WeatherDay
	 * @param weatherDay The day to show the details for
	 */
	public void showWeatherDetails(WeatherDay weatherDay) {
		// Better safe than sorry: check if the parameter is null
		if (weatherDay != null) {
			lowTempLabel.setText(weatherDay.getLowTemp().toString() + "°");
			highTempLabel.setText(weatherDay.getHighTemp().toString() + "°");
			chancePrecipitationLabel.setText(weatherDay.getChancePrecipitation().toString() + "%");
			conditionsLabel.setText(weatherDay.getConditions());
			conditionImage.setImage(new Image(weatherDay.getConditionIcon()));
		} else {
			lowTempLabel.setText("");
			highTempLabel.setText("");
			chancePrecipitationLabel.setText("");
			conditionsLabel.setText("");
		}
	}
	
	/**
	 * Sets the instance of the calling MainApp class. This is called by
	 * MainApp to set a reference to itself in an instance of this class.
	 * @param mainApp the instance of the calling MainApp class
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		weatherList.setItems(mainApp.getWeatherData());
	}
}
