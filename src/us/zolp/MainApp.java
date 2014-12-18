package us.zolp;

import gov.weather.graphical.Dwml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

import us.zolp.model.WeatherDay;
import us.zolp.util.WeatherUtil;
import us.zolp.view.RootLayoutController;
import us.zolp.view.TemperatureTrendController;
import us.zolp.view.WeatherOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	private RootLayoutController rootLayoutController;
	private WeatherOverviewController weatherOverviewController;
	private TemperatureTrendController temperatureTrendController;
	
	private ObservableList<WeatherDay> weatherData = FXCollections.observableArrayList();
	
	public MainApp() {
		getWeatherDataFromXml("46410", LocalDate.now(), 7, "e", "24 hourly");
		
		for (WeatherDay wd : weatherData) {
			System.out.println(wd.getDay());
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		try {
	        // Load root layout from fxml file.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class
	                .getResource("view/RootLayout.fxml"));
	        rootLayout = (BorderPane) loader.load();

	        // Show the scene containing the root layout.
	        Scene scene = new Scene(rootLayout);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Bad Weather App");

	        // Give the controller access to the main app.
	        rootLayoutController = loader.getController();
	        rootLayoutController.setMainApp(this);

	        primaryStage.show();
	        
	        showTemperatureTrend();
	        //showWeatherOverview();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public ObservableList<WeatherDay> getWeatherData() {
		return weatherData;
	}
	
	public WeatherOverviewController getWeatherOverviewController() {
		return weatherOverviewController;
	}
	
	public TemperatureTrendController getTemperatureTrendController() {
		return temperatureTrendController;
	}
	
	public void showWeatherOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/WeatherOverview.fxml"));
			AnchorPane weatherOverview = (AnchorPane)loader.load();
			
			rootLayout.setCenter(weatherOverview);
			
			weatherOverviewController = loader.getController();
			weatherOverviewController.setMainApp(this);
		} catch (IOException ex) {
			
		}
	}
	
	public void showTemperatureTrend() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/TemperatureTrend.fxml"));
			AnchorPane temperatureTrend = (AnchorPane)loader.load();
			
			rootLayout.setBottom(temperatureTrend);
			
			temperatureTrendController = loader.getController();
			temperatureTrendController.setMainApp(this);
			temperatureTrendController.setTemperatureData(weatherData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void getWeatherDataFromXml(String zip, LocalDate start, int days, String units, String format) {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		HttpClient client = null;
		
		if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty())
			client = HttpClients.custom().setProxy(new HttpHost(proxyHost, Integer.valueOf(proxyPort))).build();
		else
			client = HttpClients.createDefault();
		
		URI uri = null;
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost("graphical.weather.gov");
		builder.setPath("/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php");
		builder.setParameter("zipCodeList", zip);
		if (start != null)
			builder.setParameter("startDate", start.toString());
		builder.setParameter("numDays", Integer.toString(days));
		builder.setParameter("Unit", units);
		builder.setParameter("format", format);
		
		try {
			uri = builder.build();
			
			HttpGet get = new HttpGet(uri);
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				//System.out.println(line);
			}
			line = sb.toString();
			
			JAXBContext ctx = JAXBContext.newInstance(Dwml.class);
			Unmarshaller um = ctx.createUnmarshaller();
			Dwml dwml = (Dwml)um.unmarshal(new StringReader(line));
			weatherData.clear();
			
			for (WeatherDay d : WeatherUtil.dwmlToWeatherDays(dwml, days)) {
				weatherData.add(d);
			}
			System.out.println("getWeatherDataFromXml: weatherData size: " + weatherData.size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 2) {
			System.setProperty("http.proxyHost", args[0]);
			System.setProperty("http.proxyPort", args[1]);
		}
		launch(args);
	}
}
