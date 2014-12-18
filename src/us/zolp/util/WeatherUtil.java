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
import gov.weather.graphical.StartValidTimeType;
import gov.weather.graphical.TempValType;
import gov.weather.graphical.TimeLayoutElementType;

import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import us.zolp.model.WeatherDay;

public class WeatherUtil {
	private WeatherUtil() { }
	
	public static List<WeatherDay> dwmlToWeatherDays(Dwml dwml, int days) {
		List<WeatherDay> weatherDays = new ArrayList<WeatherDay>();
		List<LocalDateTime> startTimes = new ArrayList<LocalDateTime>();
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
//		try {
//			JAXBContext ctx = JAXBContext.newInstance(Dwml.class);
//			Marshaller mar = ctx.createMarshaller();
//			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			mar.marshal(dwml, new OutputStreamWriter(System.out));
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		/*
		 * REMOVE THE PRECEDING CODE AFTER DEBUGGING
		 */
		
		System.out.println("Inside dwmlToWeatherDays");
		if (dwml == null)
			System.out.println("dwmlToWeatherDays: dwml is null!");
		
		dataType = dwml.getData().get(0);
		for (TimeLayoutElementType tl : dataType.getTimeLayout()) {
			if (tl.getLayoutKey().equalsIgnoreCase("k-p24h-n" + days + "-1")) {
				for (Object obj : tl.getStartValidTimeAndEndValidTime()) {
					if (obj.getClass().equals(StartValidTimeType.class)) {
						startTimes.add(((StartValidTimeType)obj).getValue().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
					}
				}
				break;
			}
		}
		
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
			loTemps.removeAll(Collections.singleton(null));
		if (hiTemps != null)
			hiTemps.removeAll(Collections.singleton(null));
		if (conditionIcons != null)
			conditionIcons.removeAll(Collections.singleton(null));
		
		if (conditions != null) {
			conditions.removeAll(Collections.singleton(null));
			if (conditions.get(0).getClass().equals(String.class))
				conditions.remove(0);
		}
		
		if (precips != null)
			precips.removeAll(Collections.singleton(null));
		
		System.out.println("Inside dwmlToWeatherDays, after purging");
		
		for (PercentageValType p : precips) {
			if (p.getValue() != null)
				System.out.println(p.getValue());
		}
		
		for (x = 0; x < days; x++) {
			System.out.println("Inside dwmlToWeatherDays, iteration " + x);
			WeatherDay weatherDay = new WeatherDay();
//			weatherDay.setDay("Day " + (x + 1));
			weatherDay.setDay(startTimes.get(x).format(DateTimeFormatter.ofPattern("MM/dd")));
			
			if (x < precips.size()) {
				if (x > 0) {
					if ((x * 2) < precips.size()) {
						if (precips.get(x * 2) != null)
							if (precips.get(x * 2).getValue() != null)
								weatherDay.setChancePrecipitation(precips.get(x * 2).getValue().intValue());
							else
								weatherDay.setChancePrecipitation(-2);
						else
							weatherDay.setChancePrecipitation(-1);
					}
				} else {
					if (precips.get(x) != null)
						if (precips.get(x).getValue() != null)
							weatherDay.setChancePrecipitation(precips.get(x).getValue().intValue());
						else
							weatherDay.setChancePrecipitation(-2);
					else
						weatherDay.setChancePrecipitation(-1);
				}
			}
			
			if (x < loTemps.size()) {
				if (loTemps.get(x) != null)
					if (loTemps.get(x).getValue() != null)
						weatherDay.setLowTemp(loTemps.get(x).getValue().intValue());
					else
						weatherDay.setLowTemp(-2);
				else
					weatherDay.setLowTemp(-1);
			}
			
			if (x < hiTemps.size()) {
				if (hiTemps.get(x) != null)
					if (hiTemps.get(x).getValue() != null)
						weatherDay.setHighTemp(hiTemps.get(x).getValue().intValue());
					else
						weatherDay.setHighTemp(-2);
				else
					weatherDay.setHighTemp(-1);
			}
			
			if (x < conditionIcons.size()) {
				if (conditionIcons.get(x) != null)
					weatherDay.setConditionIcon(conditionIcons.get(x));
			}
			
			if (x < conditions.size()) {
				if (conditions.get(x) != null) {
					if (conditions.get(x).getClass().equals(String.class)) {
						weatherDay.setConditions(((String)conditions.get(x)));
					}
					else if (conditions.get(x).getClass().equals(WeatherConditions.class)) {
						weatherDay.setConditions(((WeatherConditions)conditions.get(x)).getWeatherSummary());
					}
				}
			}
			weatherDays.add(weatherDay);
			System.out.println(weatherDay.getDay() + " " + weatherDay.getHighTemp());
		}
		return weatherDays;
	}
}
