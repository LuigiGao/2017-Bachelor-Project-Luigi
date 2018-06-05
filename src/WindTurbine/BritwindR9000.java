package WindTurbine;

import java.util.Random;

/**
 *  Simulation of wind turbine BritwindR9000
 *  
 * Resource:
 * https://www.sustainableenergysystems.co.uk/wp-content/uploads/2015/06/Britwind_R9000_spec.pdf
 * https://en.wind-turbine-models.com/turbines/1696-britwind-r9000#datasheet
 * 
 * @author Luigi
 *
 */
public class BritwindR9000 extends WindTurbine {
	// The Britwind R9000 is fittet with a none. direct drive gearbox
	public BritwindR9000( ) {
		this.bladeLength = 5.5; // in 1 m
		this.bladeSweptArea = Math.PI * Math.sqrt( bladeLength/2 ); // in 1 m^2
		this.maxPowerOutput = 5; // in 1 kW
		double heights[] = {10, 12, 15, 18};
		this.height = heights[ new Random().nextInt(heights.length) ]; // in 1 m
		this.cut_in_speed = 3; // in 1 m/s
		this.cut_out_speed = 60; // in 1 m/s
	}

	@Override
	public String info() {
		return "BritwindR9000\n";
	}
}
