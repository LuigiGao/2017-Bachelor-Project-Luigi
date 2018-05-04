import java.util.Date;
import java.util.Random;

/**
 * This class contains a collection of weather measurements for a particular day
 * to simulate hourly weather
 * 
 * @author Luigi
 *
 */

public class Weather {

	/** Date which the weather is measured */
	private Date date;

	/** Maximum hourly mean windspeed (in 0.1 m/s) */
	private double maximum_wind_speed;

	/** Minimum hourly mean windspeed (in 0.1 m/s) */
	private double minimum_wind_speed;

	/** whether wind exist */
	private boolean has_wind;

	/** Global radiation (in J/cm2) */
	private double global_radiation;

	/** Percentage of maximum potential sunshine duration */
	private double sunshine_duration;

	/** A boolean instance indicates whether the sun shine exist */
	private boolean has_sunshine;

	/** An instance use to generate random number */
	private Random random;

	/**
	 * Build a collection of weather measurements
	 * 
	 * @param date
	 *            Date includes year, month and day which the weather is measured
	 * @param FHX
	 *            Maximum hourly mean windspeed (in 0.1 m/s)
	 * @param FHN
	 *            Minimum hourly mean windspeed (in 0.1 m/s)
	 * @param Q
	 *            Global radiation (in J/cm2)
	 * @param SP
	 *            Percentage of maximum potential sunshine duration
	 */
	public Weather(Date date, double FHX, double FHN, double Q, double SP) {
		this.date = date;
		this.maximum_wind_speed = FHX;
		this.minimum_wind_speed = FHN;
		this.global_radiation = Q;
		this.sunshine_duration = SP;
		this.random = new Random();
	}

	/**
	 * Give a estimate wind speed in interval of minimum and maximum hourly wind
	 * speed (in 0.1m/s)
	 */
	public double windSpeed() {

		// whether the wind exists
		this.has_wind = this.random.nextDouble() > (1 / (2 + this.minimum_wind_speed / 10));

		if (this.has_wind) {
			return this.minimum_wind_speed + (this.maximum_wind_speed - this.minimum_wind_speed) * random.nextDouble();
		}

		return 0;
	}

	// not finish
	/** Give a estimate solar radiation (in 1W/m^2) */
	public double solarRadiation(int hour) {

		return 0;
	}

	/** @return A string contains information about year, month and day */
	public String date() {
		return this.date.getYear() + " " + this.date.getMonth() + 1 + " " + this.date.getDate();
	}

	/** @return A string contains information about weather */
	public String info() {
		return date() + " wind speed: " + this.minimum_wind_speed + " " + this.maximum_wind_speed;
	}

	/**
	 * @return A string contains information about prediction of hourly wind speed
	 *         and solar radiation
	 */
	public String prediction() {
		return date() + " mean wind speed: " + windSpeed();
	}

}
