package Weather;

import java.util.Date;
import java.util.Random;

/**
 * This class simulate weather for one day. It use collected real data to
 * predict a new estimated data
 * 
 * Resource: 
 * https://www.thoughtco.com/high-and-low-temperature-timing-3444247
 * https://www.timeanddate.com/sun/netherlands/groningen
 * https://www.knmi.nl/nederland-nu/klimatologie/daggegevens
 * 
 * @author Luigi
 *
 */

public class Weather {

	/** Date at which day the weather is measured */
	private Date date;

	/** Maximum hourly mean wind speed (in 1 sm/s) */
	private double maximum_wind_speed;

	/** Minimum hourly mean wind speed (in 1 m/s) */
	private double minimum_wind_speed;

	/** Indicate whether wind exist */
	private boolean has_wind;

	/** Global radiation (in 1 J/m^2 for 1 day) */
	private double global_radiation;

	/** Sunshine duration (in 1 h) */
	private double sunshine_duration;

	/** Minimum temperature (in 1 degrees Celsius) */
	private double min_temperature;

	/** Maximum temperature (in 1 degrees Celsius) */
	private double max_temperature;

	/** A estimated time of sunrise */
	private int sunrise;

	/** A estimated time of sunset */
	private int sunset;

	private Random random;

	/**
	 * Estimated solar radiation calculated from global_radiation and
	 * sunshine_duration (in 1kWh/m^2h)
	 */
	private double solar_radiation;

	/**
	 * Build a Weather
	 * 
	 * @param date
	 * @param FHX
	 *            Maximum hourly mean windspeed (in 0.1 m/s)
	 * @param FHN
	 *            Minimum hourly mean windspeed (in 0.1 m/s)
	 * @param TN
	 *            Minimum temperature (in 0.1 degrees Celsius)
	 * @param TX
	 *            Maximum temperature (in 0.1 degrees Celsius)
	 * @param Q
	 *            Global radiation (in J/cm2)
	 * @param SQ
	 *            Sunshine duration (in 0.1 hour) calculated from global radiation
	 *            (-1 for <0.05 hour)
	 */
	public Weather(Date date, double FHX, double FHN, double TN, double TX, double Q, double SQ) {
		this.date = date;
		// in 1 m
		this.minimum_wind_speed = FHN * 0.1;
		this.maximum_wind_speed = FHX * 0.1;
		// in 1 degrees Celsius
		this.min_temperature = TN * 0.1;
		this.max_temperature = TX * 0.1;
		// in 1 J/m^2 for whole day
		this.global_radiation = Q * 10000;
		// in 1h
		if (SQ == -1) {
			this.sunshine_duration = 0.05;
		} else {
			this.sunshine_duration = SQ * 0.1;
		}

		this.random = new Random();
		// in 1 kWh/m^2 in for 1 h
		if (sunshine_duration != 0) {
			this.solar_radiation = global_radiation / (sunshine_duration * 3600 * 1000);
		} else {
			this.solar_radiation = 0;
		}

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
	 * Give a estimate wind speed using minimum and maximum hourly wind speed
	 * 
	 * @return A estimate wind speed in 1 m/s
	 */
	public double estimateWindSpeed() {

		this.has_wind = this.random.nextDouble() > (1 / (2 + this.minimum_wind_speed / 10));

		if (this.has_wind) {
			// Strategy1: a random number between min and max wind speed
			// return this.minimum_wind_speed + (this.maximum_wind_speed -
			// this.minimum_wind_speed) * random.nextDouble();

			double mean = getMean(this.minimum_wind_speed, this.maximum_wind_speed);

			// Strategy2: use normal distribution to simulate wind speed
			// double stdDev = getStdDev( this.minimum_wind_speed, this.maximum_wind_speed
			// );
			// return Math.max( random.nextGaussian() * stdDev + mean, 0 );

			// Strategy3: use min and max wind speed as bound 95% area of normal
			// distribution
			double stdDev = (this.maximum_wind_speed - this.minimum_wind_speed) / (2 * 1.96);
			return Math.max(random.nextGaussian() * stdDev + mean, 0);
		}
		// if no wind return 0
		return 0;
	}

	/**
	 * Estimating solar radiation at a time
	 * 
	 * @param time_in_day
	 *            Time in a day between 0 and 23
	 * @return Solar radiation in 1kWh/hm^2
	 */
	public double estimateSolarRadiation(int time_in_day) {
		return getSolarRadiation(time_in_day) * Math.max(random.nextGaussian(), 0);
	}

	/**
	 * Estimating the temperature at a time
	 * 
	 * @param time_in_day
	 *            Time in a day between 0 and 23
	 * @return Temperature in 1 degrees Celsius
	 */
	public double estimateTemperature(int time_in_day) {
		double diff = (max_temperature - min_temperature) / 12;

		int intervals = (time_in_day + 24 - 16) % 12;

		double temperature = 0;
		// assume the hottest time is 16:00 and coldest time is 4:00
		// divide the max/min temperture in interval and estimating the temperature at
		// given time
		if (time_in_day >= 16 || time_in_day < 4) {
			temperature = max_temperature - diff * intervals;
		} else {
			temperature = min_temperature + diff * intervals;
		}

		double error = (max_temperature - min_temperature) / 10; // 10%
		// temperature with bound +-5%
		temperature += -error / 2 + error * random.nextDouble();

		return temperature;
	}

	/**
	 * @param time_in_day
	 *            Time in a day between 0 and 23
	 * @return Solar radiation in 1 kWh/hm^2 )
	 */
	public double getSolarRadiation(int time_in_day) {

		if (time_in_day >= this.sunrise && time_in_day <= this.sunset) {
			return this.solar_radiation;
		}

		return 0;
	}

	/** @return Minimum wind speed in 1 m/s */
	public double getMinWindSpeed() {
		return this.minimum_wind_speed;
	}

	/** @return Maximum wind speed in 1m/s */
	public double getMaxWindSpeed() {
		return this.maximum_wind_speed;
	}

	/** @return Minimum temperature in 1 degrees Celsius */
	public double getMinTemperature() {
		return this.min_temperature;
	}

	/** @return Maximum temperature in 1 degrees Celsius */
	public double getMaxTemoerature() {
		return this.max_temperature;
	}

	/** @return A string of year, month and day at which day the data collected */
	public String date() {
		return this.date.getYear() + " " + (this.date.getMonth() + 1) + " " + this.date.getDate();
	}

	/** @return A string collected real data */
	public String realData() {
		return (date() 
				+ "\n min wind speed: " + this.minimum_wind_speed + " m/s" 
				+ "\n max wind speed:  " + this.maximum_wind_speed + " m/s" 
				+ "\n min temperature:  " + this.min_temperature + " degrees Celsius"
				+ "\n max temperature:  " + this.max_temperature + " degrees Celsius" 
				+ "\n global radiation:" + this.global_radiation + " J/m^2" 
				+ "\n sunshine duration " + this.sunshine_duration + " h" 
				+ "\n");
	}

	/**
	 * @return A string contains information about prediction of hourly wind speed
	 *         and solar radiation
	 */
	public String prediction(int time_in_day) {
		return (date() + " at " + time_in_day + "o'clock" 
				+ "\n wind speed: " + this.estimateWindSpeed() + " m/s"
				+ "\n temperature:  " + this.estimateTemperature(time_in_day) + " degrees Celsius"
				+ "\n solar radiation:" + this.estimateSolarRadiation(time_in_day) + " kW/m^2" 
				+ "\n");
	}
}
