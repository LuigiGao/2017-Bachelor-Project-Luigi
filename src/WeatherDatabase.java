import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class WeatherDatabase {

	/** A array of weather information */
	private ArrayList<Weather> weathers;

	/** A text scanner contains information of weathers */
	private Scanner weather_resource;

	/** A string contains information of weather of a particular date */
	private String weather_info;

	/**
	 * Parse the file and store weathers
	 * 
	 * @param weather_infos
	 *            Path of a file that contain information of weathers
	 */
	public WeatherDatabase(String weather_infos) {

		this.weathers = new ArrayList<Weather>();

		try {

			this.weather_resource = new Scanner(new File(weather_infos));
			parseWeathers();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Ignore the comments and parse the weather information line by line
	 */
	private void parseWeathers() {

		this.weather_info = this.weather_resource.nextLine();

		// parse comments
		while (this.weather_info.charAt(0) == '#') {
			this.weather_info = this.weather_resource.nextLine();
		}

		// System.out.println( this.weather_info );

		// parse first weather information
		parseWeather();

		// parse rest weather information
		while (this.weather_resource.hasNext()) {
			this.weather_info = this.weather_resource.nextLine();
			parseWeather();
		}

	}

	/**
	 * Build one Weather and store it into array
	 */
	// more information may added to improve the weather, for example the mean wind
	// speed etc..
	private void parseWeather() {

		double FHX = 0, FHN = 0, SP = 0, Q = 0;
		Date date;

		// parse the stn prefix
		parseDouble();

		// parse year/month/day
		date = parseDate();
		// parse ','
		parseDouble();

		// parse other information
		FHX = parseDouble();
		FHN = parseDouble();
		// SP = parseDouble();
		// Q = parseDouble();

		this.weathers.add(new Weather(date, FHX, FHN, Q, SP));
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

		// System.out.println( year + " " + month + " " + day );

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
						// System.out.println( n );
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
