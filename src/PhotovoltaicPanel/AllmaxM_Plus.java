package PhotovoltaicPanel;
/**
 * Simulation of PV panel AllmaxM_Plus
 * 
 * Resource:
 * http://mgr.trinasolar.com/sites/default/files/PS-M-0421%20Datasheet_Allmax%20M%20Plus_US_Apr2018_C.pdf
 * https://news.energysage.com/what-are-the-most-efficient-solar-panels-on-the-market/
 * choose the one with max power 275Wp
 * @author Luigi
 *
 */
public class AllmaxM_Plus extends PhotovoltaicPanel {

	public AllmaxM_Plus( ) {
		this.cellL = 156.75 / 1000; // in 1 m
		this.cellW = 156.75 / 1000; // in 1 m
		this.nCells = 6 * 10;
		this.panelArea = cellL * cellW * nCells; // in 1 m^2
		this.efficiency = (21.1-16.8) * random.nextDouble() + 16.8;
		this.maxPowerOutput = 275.0 / 1000; // in 1 kW
		this.standardTemperature = 25; // in 1 degree Celsius
		this.coefficientTemperature = -0.39; // in 1/K
	}

	@Override
	public String info() {
		return "AllmaxM_Plus\n";
	}
	
}
