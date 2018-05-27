package Weather;

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

	/** Maximum hourly mean wind speed (in 0.1 m/s) */
	private double maximum_wind_speed;

	/** Minimum hourly mean wind speed (in 0.1 m/s) */
	private double minimum_wind_speed;

	/** whether wind exist */
	private boolean has_wind;

	/** Global radiation (J/cm^2 in 1 day) */
	private double global_radiation;

	/**
	 * Sunshine duration (in 0.1 hour) calculated from global radiation (-1 for
	 * <0.05 hour)
	 */
	private double sunshine_duration;

	/** A boolean instance indicates whether the sun shine exist */
	private boolean has_sunshine;

	/** The time of sunrise */
	private int sunrise;

	/** The time of sunset */
	private int sunset;

	/** Air density (in kg/m^3) */
	private final double air_density = 1.225;

	/** An instance use to generate random number */
	private Random random;
	
	/** Mean solar radiation calculated from global_radiation and sunshine_duration (J/cm^2 in 1h) */
	private double solar_radiation;

	/**
	 * Build a collection of weather measurements
	 * 
	 * @param date
	 *            Date includes year, month and day which the weather is measured
	 * @param FHX
	 *            Maximum hourly mean wind speed (in 0.1 m/s)
	 * @param FHN
	 *            Minimum hourly mean wind speed (in 0.1 m/s)
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
		this.solar_radiation = this.global_radiation / (10 * this.sunshine_duration);
		setSunSchedule(date.getMonth());
	}

	/**
	 * Set the sunrise and sunset due to month
	 * 
	 * @param month
	 */
	private void setSunSchedule(int month) {
		switch (month) {
		case 0: // January
			this.sunrise = 9;
			this.sunset = 17;
			break;
		case 1: // February
			this.sunrise = 8;
			this.sunset = 18;
			break;
		case 2: // March
			this.sunrise = 7;
			this.sunset = 19;
			break;
		case 3: // April
			this.sunrise = 7;
			this.sunset = 21;
			break;
		case 4: // May
			this.sunrise = 6;
			this.sunset = 21;
			break;
		case 5: // June
			this.sunrise = 5;
			this.sunset = 22;
			break;
		case 6: // July
			this.sunrise = 5;
			this.sunset = 22;
			break;
		case 7: // August
			this.sunrise = 6;
			this.sunset = 21;
			break;
		case 8: // September
			this.sunrise = 7;
			this.sunset = 20;
			break;
		case 9: // October
			this.sunrise = 8;
			this.sunset = 19;
			break;
		case 10: // November
			this.sunrise = 8;
			this.sunset = 17;
			break;
		case 11: // December
			this.sunrise = 9;
			this.sunset = 16;
			break;
		}
	}

	/**
	 * @param a
	 * @param b
	 * @return (a + b) / 2
	 */
	double getMean(double a, double b) {
		return (a + b) / 2;
	}

	/**
	 * @param a
	 * @param b
	 * @return Variance of a and b
	 */
	double getVariance(double a, double b) {
		double mean = getMean(a, b);
		double temp = (a - mean) * (a - mean) + (b - mean) * (b - mean);
		return temp / (2 - 1);
	}

	/**
	 * @param a
	 * @param b
	 * @return Standard deviation of a and b
	 */
	double getStdDev(double a, double b) {
		return Math.sqrt(getVariance(a, b));
	}

	/**
	 * Give a estimate wind speed in interval of minimum and maximum hourly wind
	 * speed (in 0.1m/s)
	 * 
	 * @return A estimate wind speed
	 */
	public double windSpeed() {

		// whether the wind exists
		this.has_wind = this.random.nextDouble() > (1 / (2 + this.minimum_wind_speed / 10));

		if (this.has_wind) {
			// a random number between min and max wind speed
			// return this.minimum_wind_speed + (this.maximum_wind_speed -
			// this.minimum_wind_speed) * random.nextDouble();

			double mean = getMean(this.minimum_wind_speed, this.maximum_wind_speed);

			// use normal distribution to simulate wind speed
			// double stdDev = getStdDev( this.minimum_wind_speed, this.maximum_wind_speed
			// );
			// return Math.max( random.nextGaussian() * stdDev + mean, 0 );

			// use min and max wind speed as bound 95% area of normal distribution
			double stdDev = (this.maximum_wind_speed - this.minimum_wind_speed) / (2 * 1.96);
			return Math.max(random.nextGaussian() * stdDev + mean, 0);
		}

		return 0;
	}

	/**
	 * @param hour
	 * @return Estimated solar radiation (in 1 J/cm^2 )
	 */
	public double solarRadiation(int hour) {

		if (hour >= this.sunrise && hour <= this.sunset) {
			// not sure how to estimate the solar radiation
			return this.solar_radiation * Math.max(random.nextGaussian(), 0);
		}

		return 0;
	}

	/**
	 * @return Air density
	 */
	public double airDensity() {
		return this.air_density;
	}

	/**
	 * @return A string contains information about year, month and day
	 */
	public String date() {
		return this.date.getYear() + " " + (this.date.getMonth() + 1) + " " + this.date.getDate();
	}

	/** @return A string contains information about weather */
	public String info() {
		return date() + " wind speed: " + this.minimum_wind_speed + " " + this.maximum_wind_speed
				+ "; global radiation:" + this.global_radiation + " with duration " + this.sunshine_duration + "%";
	}

	/**
	 * @return A string contains information about prediction of hourly wind speed
	 *         and solar radiation
	 */
	public String prediction() {
		return date() + " mean wind speed: " + windSpeed();
	}
}
