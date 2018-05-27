package Weather;

import java.util.ArrayList;
import Parser.WeatherTxtParser;

/**
 * A database stores weathers' information
 * 
 * @author Luigi
 *
 */
public class WeatherDatabase {

	/** A array of weather information */
	private ArrayList<Weather> weathers;

	/** Parser that read and store the weather from txt file */
	private WeatherTxtParser parser;

	/** Count for index of next weather */
	private int count;
	
	/** Size of array of weathers */
	private int size;

	/**
	 * Parse the file and store weathers
	 * 
	 * @param weather_infos
	 *            Path of a file that contain information of weathers
	 */
	public WeatherDatabase(String weather_infos) {

		this.weathers = new ArrayList<Weather>();
		this.count = 0;

		// read and store the information of weathers
		this.parser = new WeatherTxtParser(this, weather_infos);
		this.parser.parse();

		this.size = this.weathers.size();
	}

	/**
	 * Add one weather into this database
	 * 
	 * @param weather
	 *            A collection of weather data of one day
	 */
	public void addWeather(Weather weather) {
		this.weathers.add(weather);
	}

	/**
	 * Remove the first Weather in array
	 * 
	 * @return The first Weather in old array
	 */
	public Weather nextWeather() {
		// repeatedly use the weathers if need more
		if (this.count >= this.size) {
			this.count = 0;
		}
		Weather weather = this.weathers.get(count);
		this.count++;
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
