package Weather;

import java.util.ArrayList;
import Parser.WeatherTxtParser;

/**
 * A database manage and stores weathers' information
 * 
 * @author Luigi
 *
 */
public class WeatherDatabase {

	/** A array of weather information */
	private ArrayList<Weather> weathers;

	/** Count for index of next weather */
	private int count;

	/** Size of array of weathers */
	private int size;

	/**
	 * Build the database based on input file
	 * 
	 * @param weather_infos
	 *            Path of a file that stores information of weathers
	 */
	public WeatherDatabase(String weather_infos) {

		this.weathers = new ArrayList<Weather>();
		this.count = 0;

		// read and store the information of weathers
		WeatherTxtParser parser = new WeatherTxtParser(this, weather_infos);
		parser.parse();

		this.size = this.weathers.size();
	}

	/**
	 * Add one weather into this database
	 * 
	 * @param weather
	 *            Weather data
	 */
	public void addWeather(Weather weather) {
		this.weathers.add(weather);
	}

	/**
	 * Extract next Weather
	 * 
	 * @return Weather
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
	 * Display real data (min/max wind speed, global radiation, etc..) of all
	 * weathers
	 */
	public void realData() {
		for (Weather w : this.weathers) {
			System.out.println(w.realData());
		}
	}

	/**
	 * Display a prediction (hourly wind speed, and solar radiation, etc..) of all Weathers in
	 * this database
	 */
	public void prediction() {
		for (Weather w : this.weathers) {
			for (int i = 0; i < 24; i++)
				System.out.println(w.prediction(i));
		}
	}

}
