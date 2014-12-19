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
		// Test initialization data
		getWeatherDataFromXml("46410", LocalDate.now(), 7, "e", "24 hourly");
		
		for (WeatherDay wd : weatherData) {
			System.out.println(wd.getDay());
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		// Load and display the root layout
		showRootLayout();
		// Load and display the weather overview
		showWeatherOverview();
		// Load and display the temperature trend
		showTemperatureTrend();
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
	
	public void showRootLayout() {
		try {
	        // Load root layout from fxml file.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
	        rootLayout = (BorderPane) loader.load();

	        // Show the scene containing the root layout.
	        Scene scene = new Scene(rootLayout);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Bad Weather App");

	        // Give the controller access to the main app.
	        rootLayoutController = loader.getController();
	        rootLayoutController.setMainApp(this);

	        primaryStage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void showWeatherOverview() {
		try {
			// Get WeatherOverview from FXML file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/WeatherOverview.fxml"));
			AnchorPane weatherOverview = (AnchorPane)loader.load();
			
			// Put the WeatherOverview in the center position of the root layout
			rootLayout.setCenter(weatherOverview);
			
			// Get the view's controller and give it access to the main app
			weatherOverviewController = loader.getController();
			weatherOverviewController.setMainApp(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void showTemperatureTrend() {
		try {
			// Get TemperatureTrend from FXML file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/TemperatureTrend.fxml"));
			AnchorPane temperatureTrend = (AnchorPane)loader.load();
			
			// Put the TemperatureTrend in the bottom position of the root layout
			rootLayout.setBottom(temperatureTrend);
			
			// Get the view's controller and give it access to the main app
			temperatureTrendController = loader.getController();
			temperatureTrendController.setMainApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Assemble an HTTP GET URI, unmarshal it to a DWML object,
	 * 			and put the values into an ObservableList of WeatherDay objects 
	 * @param zip ZIP code of the location to search
	 * @param start First date to retrieve data for
	 * @param days Number of days to retrieve
	 * @param units Measurement units to use (English or Metric)
	 * @param format Hourly format to use (12-hourly or 24-hourly)
	 */
	public void getWeatherDataFromXml(String zip, LocalDate start, int days, String units, String format) {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		HttpClient client = null;
		URI uri = null;
		URIBuilder builder = new URIBuilder();
		
		// If a proxy has been specified, create an HttpClient that uses those settings,
		// otherwise, use a default HttpClient configuration
		if (proxyHost != null && proxyPort != null) {
			if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
				client = HttpClients.custom().setProxy(new HttpHost(proxyHost, Integer.valueOf(proxyPort))).build();
			}
		} else {
			client = HttpClients.createDefault();
		}
		
		/*
		 * Build the URI for the GET request
		 */
		
		// Using HTTP (http://)
		builder.setScheme("http");
		// Set the host (http://graphical.weather.gov)
		builder.setHost("graphical.weather.gov");
		// Set the service path (http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php)
		builder.setPath("/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php");
		// Set the zipCodeList parameter (ndfdBrowserClientByDay.php?zipCodeList=[zip])
		builder.setParameter("zipCodeList", zip);
		
		// Set the startDate parameter, optional
		// (ndfdBrowserClientByDay.php?zipCodeList=[zip]&startDate=[start])
		if (start != null)
			builder.setParameter("startDate", start.toString());
		
		// Set the numDays parameter
		// (ndfdBrowserClientByDay.php?zipCodeList=[zip]&startDate=[start]&numDays=[num])
		builder.setParameter("numDays", Integer.toString(days));
		// Set the unit parameter
		// (ndfdBrowserClientByDay.php?zipCodeList=[zip]&startDate=[start]&numDays=[num]&Unit=[unit])
		builder.setParameter("Unit", units);
		// Set the format parameter
		// (ndfdBrowserClientByDay.php?zipCodeList=[zip]&startDate=[start]&numDays=[num]&Unit=[unit]&format=[format])
		builder.setParameter("format", format);
		
		/*
		 * Assemble the completed URI and try to perform the POST
		 */
		try {
			// Create the URI from the URIBuilder
			uri = builder.build();
			
			// Create a GET request using the URI
			HttpGet get = new HttpGet(uri);
			// Execute the query and get the response
			HttpResponse response = client.execute(get);
			// Create a BufferedReader to read the content of the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			StringBuilder sb = new StringBuilder();
			
			// Read the response and assemble it into a String
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			line = sb.toString();
			
			// Unmarshal the received XML into a Dwml object
			JAXBContext ctx = JAXBContext.newInstance(Dwml.class);
			Unmarshaller um = ctx.createUnmarshaller();
			Dwml dwml = (Dwml)um.unmarshal(new StringReader(line));
			weatherData.clear();
			
			// Convert the Dwml objects into WeatherDay objects, put them in
			// the Observable list
			for (WeatherDay d : WeatherUtil.dwmlToWeatherDays(dwml, days)) {
				weatherData.add(d);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// Takes two arguments, a host/IP address, and port
		if (args.length == 2) {
			System.setProperty("http.proxyHost", args[0]);
			System.setProperty("http.proxyPort", args[1]);
		}
		
		// Start the JavaFX code
		launch(args);
	}
}
