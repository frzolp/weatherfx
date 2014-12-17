package us.zolp.view;

import java.util.List;

import us.zolp.MainApp;
import us.zolp.model.WeatherDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class TemperatureTrendController {
	private MainApp mainApp;
	
	@FXML
	private LineChart<String, Integer> tempChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	
	private XYChart.Series<String, Integer> highSeries = new XYChart.Series<>();
	private XYChart.Series<String, Integer> lowSeries = new XYChart.Series<>();
	
	private ObservableList<String> dayList = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setTemperatureData(List<WeatherDay> weatherDays) {
		xAxis.setLabel("Day");
		yAxis.setLabel("Temperature");
		highSeries.setName("High Temps");
		lowSeries.setName("Low Temps");
		
		dayList.clear();
		highSeries.getData().clear();
		lowSeries.getData().clear();
		
		for (WeatherDay w : mainApp.getWeatherData()) {
			dayList.add(w.getDay());
		}
		
		xAxis.setCategories(dayList);
		
		if (tempChart.getData().size() == 2) {
			tempChart.getData().remove(1);
			tempChart.getData().remove(0);
		}
		
		for (int x = 0; x < weatherDays.size(); x++) {
			highSeries.getData().add(new Data<>(dayList.get(x), weatherDays.get(x).getHighTemp()));
			lowSeries.getData().add(new Data<>(dayList.get(x), weatherDays.get(x).getLowTemp()));
		}
		
		tempChart.getData().add(highSeries);
		tempChart.getData().add(lowSeries);
	}
}
