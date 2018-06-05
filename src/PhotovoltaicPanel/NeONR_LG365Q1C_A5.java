package PhotovoltaicPanel;

/**
 * Simulation of PV panel NeONR_LG365Q1C_A5
 * 
 * Resource:
 * DS_NeONR_60cells.pdf
 * https://news.energysage.com/what-are-the-most-efficient-solar-panels-on-the-market/
 * 
 * @author Luigi
 *
 */
public class NeONR_LG365Q1C_A5 extends PhotovoltaicPanel {

	public NeONR_LG365Q1C_A5( ) {
		this.cellL = 161.7 / 1000; // in 1 m
		this.cellW = 161.7 / 1000; // in 1 m
		this.nCells = 6 * 10;
		this.panelArea = cellL * cellW * nCells; // in 1 m^2
		this.efficiency = (21.1-16.8) * random.nextDouble() + 16.8;
		this.maxPowerOutput = 365.0 / 1000; // in 1 kW
		this.standardTemperature = 25; // in 1 degree Celsius
		this.coefficientTemperature = -0.3; // in 1/K
	}

	@Override
	public String info() {
		return "NeONR_LG365Q1C_A5\n";
	}
	
}
