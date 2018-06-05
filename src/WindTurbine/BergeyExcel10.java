package WindTurbine;

/**
 * Simulation of wind turbine BergeyExcel10
 * 
 * Resource:
 * https://en.wind-turbine-models.com/turbines/501-bergey-bwc-excel-10
 * http://bergey.com/documents/2013/10/excel-10-spec-sheet_2013.pdf
 * 
 */

import java.util.Random;

public class BergeyExcel10 extends WindTurbine {
	// The Bergey BWC Excel 10 is fittet without direct drive gearbox.
	public BergeyExcel10() {
		this.bladeLength = 7; // in 1 m
		this.bladeSweptArea = Math.PI * Math.sqrt(bladeLength / 2); // in 1 m^2
		this.maxPowerOutput = 12.6; // in 1 kW
		int maxHeight = 49;
		int minHeight = 24;
		this.height = new Random().nextInt( maxHeight - minHeight + 1) + minHeight; // in 1 m
		this.cut_in_speed = 2.5; // in 1 m/s
		this.cut_out_speed = 60; // in 1 m/s
	}

	@Override
	public String info() {
		return "BergeyExcel10\n";
	}
}
