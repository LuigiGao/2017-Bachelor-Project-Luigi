package PhotovoltaicPanel;

/**
 * Simulation of PV panel CSUN_S156_5BB
 * 
 * Resource:
 * http://www.csun-solar.com/products-poly-modules.html
 * https://news.energysage.com/what-are-the-most-efficient-solar-panels-on-the-market/
 * http://www.csun-solar.com/fileadmin/dateiablage/media/datasheets/m-cells/CSUN-S156-5BB_Dia210_.pdf
 * 
 * @author Luigi
 *
 */
public class CSUN_S156_5BB extends PhotovoltaicPanel {
	
	public CSUN_S156_5BB( ) {
		this.cellL = 156.75 / 1000; // in 1 m
		this.cellW = 156.75 / 1000; // in 1 m
		// assume the number of cells of a photovoltaic panel
		this.nCells = 6 * 10;
		this.panelArea = cellL * cellW * nCells; // in 1 m^2
		this.efficiency = (16.53-14.98) * random.nextDouble() + 14.98;
		this.maxPowerOutput = 4.92*60 / 1000; // in 1 kW
		this.standardTemperature = 25; // in 1 degree Celsius
		this.coefficientTemperature = -0.4; // in 1/K
	}

	@Override
	public String info() {
		return "CSUN_S156_5BB\n";
	}
	
}
