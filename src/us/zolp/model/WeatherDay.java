package us.zolp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WeatherDay {
	private final StringProperty day;
	private final StringProperty conditions;
	private final StringProperty location;
	private final StringProperty conditionIcon;
	private final IntegerProperty hiTemp;
	private final IntegerProperty loTemp;
	private final IntegerProperty chancePrecipitation;
	
	public WeatherDay() {
		this(null, 0, 0, 0, null, null, null);
	}
	
	public WeatherDay(String day, int hiTemp, int loTemp, int percentChance, String conditions, String conditionIcon, String location) {
		this.day = new SimpleStringProperty(day);
		this.conditions = new SimpleStringProperty(conditions);
		this.location = new SimpleStringProperty(location);
		this.conditionIcon = new SimpleStringProperty(conditionIcon);
		this.hiTemp = new SimpleIntegerProperty(hiTemp);
		this.loTemp = new SimpleIntegerProperty(loTemp);
		this.chancePrecipitation = new SimpleIntegerProperty(percentChance);
	}
	
	public String getDay() {
		return day.get();
	}
	
	public void setDay(String day) {
		this.day.set(day);
	}
	
	public StringProperty dayProperty() {
		return day;
	}
	
	public String getConditions() {
		return conditions.get();
	}
	
	public void setConditions(String conditions) {
		this.conditions.set(conditions);
	}
	
	public StringProperty conditionsProperty() {
		return conditions;
	}
	
	public String getLocation() {
		return location.get();
	}
	
	public void setLocation(String location) {
		this.location.set(location);
	}
	
	public StringProperty locationProperty() {
		return location;
	}
	
	public String getConditionIcon() {
		return conditionIcon.get();
	}
	
	public void setConditionIcon(String conditionIcon) {
		this.conditionIcon.set(conditionIcon);
	}
	
	public StringProperty conditionIconProperty() {
		return conditionIcon;
	}
	
	public Integer getHighTemp() {
		return hiTemp.getValue();
	}
	
	public void setHighTemp(Integer hiTemp) {
		this.hiTemp.setValue(hiTemp);
	}
	
	public IntegerProperty highTempProperty() {
		return hiTemp;
	}
	
	public Integer getLowTemp() {
		return loTemp.getValue();
	}
	
	public void setLowTemp(Integer loTemp) {
		this.loTemp.setValue(loTemp);
	}
	
	public IntegerProperty lowTempProperty() {
		return loTemp;
	}
	
	public Integer getChancePrecipitation() {
		return chancePrecipitation.getValue();
	}
	
	public void setChancePrecipitation(Integer chancePrecipitation) {
		this.chancePrecipitation.setValue(chancePrecipitation);
	}
	
	public IntegerProperty chancePrecipitationProperty() {
		return chancePrecipitation;
	}
	
	@Override
	public String toString() {
		return day.get();
	}
}
