package Network;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Weather.WeatherDatabase;

public class Test {

	static Random random = new Random ( );
	
	static String prosumer = "src/Data/Prosumers.json";
	static String relation = "src/Data/Relations.json";
	static String weather = "src/Data/Weather_2015.txt";
	static int days = 3;
	
	public static void main( String... args ) {
		
		testTopologyJson( );
		//testSimulationWeather( );
		
	}
	
	// find the shortest path correctly
	public static void testEnergyFlow( ) {
		//NodeNetwork test = new NodeNetwork( prosumer, relation, weather, days);
		
	}
	
	// simulate weather and energy consumption
	public static void testSimulationWeather( ) {
		NodeNetwork test = new NodeNetwork( prosumer, relation, weather, days);
		Thread t = new Thread( test );
		t.run();
	}
	
	// parse the weather data correctly
	public static void testWeatherTxt( ) {
		WeatherDatabase database = new WeatherDatabase( weather );
		database.info();
		//database.prediction();
	}
	
	// parse the prosumer and their relation correctly
	private static void testTopologyJson( ) {
		NodeNetwork test = new NodeNetwork( prosumer, relation );
		//test.printNodeInfo();
		//test.printMaxLoad();
		//test.printConnection();
		test.printLoadsStatus();
	}
	
}
