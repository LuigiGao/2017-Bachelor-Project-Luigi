package WindTurbine;

/**
 * Simulation of wind turbine TheSkystream37
 * 
 * Resource:
 * https://en.wind-turbine-models.com/turbines/1732-xzeres-wind-skystream-3.7
 * https://pdf.wholesalesolar.com/wind%20pdf%20folder/skystreamOwnerManual.pdf
 * https://en.wikipedia.org/wiki/Wind_turbine_design#Gearless_wind_turbine
 * 
 * @author Luigi
 *
 */

public class TheSkystream37 extends WindTurbine {
	// The XZERES Wind Skystream 3.7 is fittet with a gearless gearbox.
	// without gearbox
	public TheSkystream37() {
		this.bladeLength = 3.7; // in 1 m
		this.bladeSweptArea = Math.PI * Math.sqrt(bladeLength / 2); // in 1 m^2
		this.maxPowerOutput = 2.4; // in 1 kW
		// no real data, the suggested height is above 7 m
		this.height = 7; // in 1 m
		this.cut_in_speed = 3.2; // in 1 m/s
		this.cut_out_speed = 25; // in 1 m/s
	}

	@Override
	public String info() {
		return "TheSkystream37\n";
	}
}
