package Weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import Parser.WeatherTxtParser;

public class WeatherDatabase {

	/** A array of weather information */
	private ArrayList<Weather> weathers;

	private WeatherTxtParser parser;
	
	/**
	 * Parse the file and store weathers
	 * 
	 * @param weather_infos
	 *            Path of a file that contain information of weathers
	 */
	public WeatherDatabase(String weather_infos) {

		this.weathers = new ArrayList<Weather>();
		this.parser = new WeatherTxtParser( this, weather_infos );
		
		this.parser.parse( );

	}
	
	public void addWeather( Weather weather ) {
		this.weathers.add( weather );
	}

	/**
	 * Remove the first Weather in array
	 * 
	 * @return The first Weather in old array
	 */
	public Weather nextWeather() {
		Weather weather = this.weathers.get(0);
		this.weathers.remove(0);
		return weather;
	}

	/**
	 * Print information (min/max wind speed, global radiation, etc..) of all
	 * weathers in this database
	 */
	public void info() {
		for (Weather w : this.weathers) {
			System.out.println(w.info());
		}
	}

	/**
	 * Print prediction (hourly wind speed, and solar radiation) of all Weathers in
	 * this database
	 */
	public void prediction() {
		for (Weather w : this.weathers) {
			w.prediction();
		}
	}

}
