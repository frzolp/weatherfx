package us.zolp.util;

import gov.weather.graphical.DataType;
import gov.weather.graphical.DecimalValType;
import gov.weather.graphical.Dwml;
import gov.weather.graphical.ParametersType;
import gov.weather.graphical.ParametersType.ConditionsIcon;
import gov.weather.graphical.ParametersType.Weather.WeatherConditions;
import gov.weather.graphical.ParametersType.Precipitation;
import gov.weather.graphical.ParametersType.Temperature;
import gov.weather.graphical.ParametersType.Weather;
import gov.weather.graphical.PercentageValType;
import gov.weather.graphical.TempValType;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import us.zolp.model.WeatherDay;

public class WeatherUtil {
	private WeatherUtil() { }
	
	public static List<WeatherDay> dwmlToWeatherDays(Dwml dwml, int days) {
		List<WeatherDay> weatherDays = new ArrayList<WeatherDay>();
		DataType dataType = null;
		ParametersType parameters = null;
		List<TempValType> loTemps = null;
		List<TempValType> hiTemps = null;
		List<String> conditionIcons = null;
		List<Object> conditions = null;
		List<PercentageValType> precips = null;
		int x = 0;
		
		/*
		 * REMOVE THE FOLLOWING CODE AFTER DEBUGGING
		 */
		try {
			JAXBContext ctx = JAXBContext.newInstance(Dwml.class);
			Marshaller mar = ctx.createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			mar.marshal(dwml, new OutputStreamWriter(System.out));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * REMOVE THE PRECEDING CODE AFTER DEBUGGING
		 */
		
		System.out.println("Inside dwmlToWeatherDays");
		if (dwml == null)
			System.out.println("dwmlToWeatherDays: dwml is null!");
		
		dataType = dwml.getData().get(0);
		
		x = 0;
		while ((parameters = dataType.getParameters().get(x)) == null)
			x++;
		
		for (Temperature t : parameters.getTemperature()) {
			if (t != null) {
				if (t.getType().equalsIgnoreCase("maximum")) {
					hiTemps = t.getValue();
					System.out.println("dwmlToWeatherDays: got hiTemps, size: " + t.getValue().size());
				} else if (t.getType().equalsIgnoreCase("minimum")) {
					loTemps = t.getValue();
					System.out.println("dwmlToWeatherDays: got loTemps, size: " + t.getValue().size());
				}
			}
		}
		
		if (hiTemps != null)
			System.out.println("Inside dwmlToWeatherDays, after temperature, " + hiTemps.size());
		
		for (x = 0; x < parameters.getProbabilityOfPrecipitation().size(); x++) {
			if (parameters.getProbabilityOfPrecipitation().get(x) != null) {
				precips = parameters.getProbabilityOfPrecipitation().get(x).getValue();
				break;
			}
		}
		
		if (precips != null)
			System.out.println("Inside dwmlToWeatherDays, after precipitation, " + precips.size());
		
		for (x = 0; x < parameters.getConditionsIcon().size(); x++) {
			if (parameters.getConditionsIcon().get(x) != null)
				conditionIcons = parameters.getConditionsIcon().get(x).getIconLink();
		}
		
		if (conditionIcons != null)
			System.out.println("Inside dwmlToWeatherDays, after condition icons, " + conditionIcons.size());
		
		for (x = 0; x < parameters.getWeather().size(); x++) {
			if (parameters.getWeather().get(x) != null)
				conditions = parameters.getWeather().get(x).getNameAndWeatherConditions();
		}
		
		if (conditions != null)
			System.out.println("Inside dwmlToWeatherDays, after conditions, " + conditions.size());
		
		if (loTemps != null)
			if (loTemps.get(0) == null)
				loTemps.remove(0);
		if (hiTemps != null)
			if (hiTemps.get(0) == null)
				hiTemps.remove(0);
			if (conditionIcons != null)
				if (conditionIcons.get(0) == null)
					conditionIcons.remove(0);
		if (conditions != null) {
			if (conditions.get(0) == null)
				conditions.remove(0);
			if (conditions.get(0).getClass().equals(String.class))
				conditions.remove(0);
		}
		if (precips != null)
			if (precips.get(0) == null)
				precips.remove(0);
		
		System.out.println("Inside dwmlToWeatherDays, after purging");
		
		for (x = 0; x < days; x++) {
			System.out.println("Inside dwmlToWeatherDays, iteration " + x);
			WeatherDay weatherDay = new WeatherDay();
			weatherDay.setDay("Day " + x);
			if (precips.get(x) != null)
				if (precips.get(x).getValue() != null)
					weatherDay.setChancePrecipitation(precips.get(x).getValue().intValue());
				else
					weatherDay.setChancePrecipitation(-2);
			else
				weatherDay.setChancePrecipitation(-1);
			
			if (loTemps.get(x) != null)
				if (loTemps.get(x).getValue() != null)
					weatherDay.setLowTemp(loTemps.get(x).getValue().intValue());
				else
					weatherDay.setLowTemp(-2);
			else
				weatherDay.setLowTemp(-1);
			
			if (hiTemps.get(x) != null)
				if (hiTemps.get(x).getValue() != null)
					weatherDay.setHighTemp(hiTemps.get(x).getValue().intValue());
				else
					weatherDay.setHighTemp(-2);
			else
				weatherDay.setHighTemp(-1);
			
			if (conditionIcons.get(x) != null)
				weatherDay.setConditionIcon(conditionIcons.get(x));
			
			if (conditions.get(x) != null) {
				if (conditions.get(x).getClass().equals(String.class)) {
					weatherDay.setConditions(((String)conditions.get(x)));
				}
				else if (conditions.get(x).getClass().equals(WeatherConditions.class)) {
					weatherDay.setConditions(((WeatherConditions)conditions.get(x)).getWeatherSummary());
				}
			}
			weatherDays.add(weatherDay);
			System.out.println(weatherDay.getDay() + " " + weatherDay.getHighTemp());
		}
		return weatherDays;
	}
}
