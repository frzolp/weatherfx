package us.zolp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import us.zolp.MainApp;
import us.zolp.model.WeatherDay;

public class WeatherOverviewController {
	@FXML
	private TableView<WeatherDay> dayTable;
	@FXML
	private TableColumn<WeatherDay, String> dayColumn;
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
	
	private MainApp mainApp;
	
	public WeatherOverviewController() {
		
	}
	
	@FXML
	private void initialize() {
		dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());
		
		showWeatherDetails(null);
		dayTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showWeatherDetails(newValue));
	}
	
	private void showWeatherDetails(WeatherDay weatherDay) {
		if (weatherDay != null) {
			lowTempLabel.setText(Integer.toString(weatherDay.getLowTemp()));
			highTempLabel.setText(Integer.toString(weatherDay.getHighTemp()));
			chancePrecipitationLabel.setText(Integer.toString(weatherDay.getChancePrecipitation()));
			conditionsLabel.setText(weatherDay.getConditions());
		} else {
			lowTempLabel.setText("");
			highTempLabel.setText("");
			chancePrecipitationLabel.setText("");
			conditionsLabel.setText("");
		}
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		dayTable.setItems(mainApp.getWeatherData());
	}
}
