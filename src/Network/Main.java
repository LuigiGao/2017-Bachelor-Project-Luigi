package Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import PhotovoltaicPanel.*;
import Weather.WeatherDatabase;
import WindTurbine.*;

/**
 * Main class run the program with given input data.
 * 
 * Analysis:
 * 			analysis the performance of the smart grid system
 * Simulation:
 * 			simulate the working of smart grid
 * 
 * @author Luigi
 *
 */

public class Main {

	static String prosumer = "src/Data/Prosumers.json";
	
	static String relationBus = "src/Data/Relation_Bus.json";
	static String relationStar = "src/Data/Relation_Star.json";
	static String relationSmallWorld = "src/Data/Relation_SmallWorld.json";
	static String relationComplete = "src/Data/Relation_Complete.json";
	
	static String weather = "src/Data/Weather_2015.txt";
	
	static int days = 30;

	public static void main(String... args) {
		
		analysis( );
		simulation( );
		
	}

	public static void simulation() {
		Network test = new Network_Simulation(prosumer, relationBus, weather, days);
		Thread t = new Thread(test);
		t.run();
	}
	
	public static void analysis() {
		long startTime = System.nanoTime();
		Network test = new Network_Analysis(prosumer, relationBus, weather, days);
		Thread t = new Thread(test);
		t.run();
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Total Runtime: " + totalTime / 1000000000 + "s");
	}
	
}
