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
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

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
		setTemperatureData(mainApp.getWeatherData());
	}
	
	public Data<String, Integer> plot(String x, int y, int type) {
		final Data<String, Integer> data = new Data<>(x, y);
		data.setNode(new HoveredThresholdNode(y, type));
		return data;
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
			highSeries.getData().add(plot(dayList.get(x), weatherDays.get(x).getHighTemp(), 0));
			lowSeries.getData().add(plot(dayList.get(x), weatherDays.get(x).getLowTemp(), 1));
		}
		
		tempChart.getData().add(highSeries);
		tempChart.getData().add(lowSeries);
	}
	
	class HoveredThresholdNode extends StackPane {
		HoveredThresholdNode(int value, int type) {
			setPrefSize(15, 15);
			
			final Label label = createDataThresholdLabel(value, type);
			
			setOnMouseEntered(evt -> {
				getChildren().setAll(label);
				setCursor(Cursor.NONE);
				toFront();
			});
			
			setOnMouseExited(evt -> {
				getChildren().clear();
				setCursor(Cursor.CROSSHAIR);
			});
		}
		
		private Label createDataThresholdLabel(int value, int type) {
			final Label label = new Label(value + "");
			label.getStyleClass().addAll("default-color" + type, "chart-line-symbol", "chart-series-line");
			label.setStyle("-fx-font-size: 12;");
			
			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
			return label;
		}
	}
	

}
