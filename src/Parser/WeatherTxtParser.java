package Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;

import Weather.Weather;
import Weather.WeatherDatabase;

/**
 * A parser that write weather to the weather database
 * 
 * @author Luigi
 *
 */

public class WeatherTxtParser {

	/** A text scanner contains information of weathers */
	private Scanner weather_resource;

	/** A string contains information of weather of a particular date */
	private String weather_info;

	/** Database that stores weather */
	private WeatherDatabase database;

	/**
	 * Build the parser
	 * 
	 * @param database
	 * @param weather_infos
	 */
	public WeatherTxtParser(WeatherDatabase database, String weather_infos) {

		this.database = database;

		try {
			this.weather_resource = new Scanner(new File(weather_infos));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Parse the weather information line by line
	 */
	public void parse() {

		parseComments();
		
		// parse first weather information
		parseWeather();

		// parse rest weather information
		while (this.weather_resource.hasNext()) {
			this.weather_info = this.weather_resource.nextLine();
			parseWeather();
		}

	}

	/**
	 * Ignore the comments
	 */
	private void parseComments() {

		this.weather_info = this.weather_resource.nextLine();

		// strategy to parse explanation of sources and variables
		while (this.weather_info.isEmpty() || this.weather_info.charAt(0) != '#') {
			// System.out.println( this.weather_info );
			this.weather_info = this.weather_resource.nextLine();
		}

		// strategy to parse comments with '#' and the empty dividing line
		while (this.weather_info.isEmpty() || this.weather_info.charAt(0) == '#') {
			this.weather_info = this.weather_resource.nextLine();
		}

		// following text is weather data

	}

	/**
	 * Build one Weather and store it into array
	 */
	private void parseWeather() {

		// more information could be added to improve the weather, for example the mean wind
		// speed etc..
		double FHX = 0, FHN = 0, TN = 0, TX = 0, SQ = 0, Q = 0;
		Date date;

		// parse the stn prefix
		parseDouble();

		// parse year/month/day
		date = parseDate();

		parseDoubles(3);
		FHX = parseDouble();

		parseDoubles(1);
		FHN = parseDouble();


		parseDoubles(4);
		TN = parseDouble();

		parseDoubles(1);
		TX = parseDouble();
		
		parseDoubles(3);
		SQ = parseDouble();

		parseDoubles(1);
		Q = parseDouble();

		this.database.addWeather(new Weather(date, FHX, FHN, TN, TX, Q, SQ));
	}

	/**
	 * Convert first 8 characters of a string into year, month and day, and build a
	 * Date
	 * 
	 * @return Date with year, month and day
	 */
	private Date parseDate() {
		int year = parseInt(4);
		int month = parseInt(2) - 1;
		int day = parseInt(2);

		// parse ','
		parseDouble();

		return new Date(year, month, day);
	}

	/**
	 * Parse first double of the string of weather information
	 * 
	 * @return The first double of the string of weather information
	 */
	// parse stn, fhx, fhn, etc...
	private double parseDouble() {
		double n = 0;
		double f = 1;
		int dot_count = 0;

		for (int i = 0; i < this.weather_info.length(); i++) {

			if (this.weather_info.charAt(i) != ',') {

				if ('0' <= this.weather_info.charAt(i) && this.weather_info.charAt(i) <= '9') {

					if (dot_count == 0) {
						n = n * 10 + this.weather_info.charAt(i) - '0';
					} else {
						f = 0.1 * f;
						n = n + f * (this.weather_info.charAt(i) - '0');
					}

				} else if (this.weather_info.charAt(i) == '.') {
					dot_count++;
					if (dot_count > 1)
						System.out.println("error input file for weather");
				}

			} else {
				// cut the string until ',' + 1
				this.weather_info = this.weather_info.substring(i + 1);
				// System.out.println( this.weather_info );
				// System.out.println( n );
				return n;
			}
		}

		return n;
	}

	/** parse(ignore) the next n doubles */
	private void parseDoubles(int n) {

		for (int i = 0; i < n; i++) {
			parseDouble();
		}

	}

	/**
	 * Parse first int with length n
	 * 
	 * @param n
	 *            The length of int
	 * @return A int with length n
	 */
	private int parseInt(int n) {
		int out = 0;

		for (int i = 0; i < n; i++) {
			out = out * 10 + weather_info.charAt(i) - '0';
		}

		this.weather_info = this.weather_info.substring(n);
		// System.out.println( this.weather_info );

		return out;
	}

}
