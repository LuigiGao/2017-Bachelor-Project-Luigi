import java.util.ArrayList;

/**
 * A house that both consume and produce energy
 * @author Luigi
 *
 */

public class Prosumer {

	// add a storage
	
	/** automatically generate by program to represent a prosumer */
	// which is also the index of column and row
	private int id;
	
	/** house number of the prosumer */
	private int houseNumber;
	
	/** array of wind turbines */
	private ArrayList<WindTurbine> windTurbines;
	
	/** array of photovoltaic panels */
	private ArrayList<PhotovoltaicPanel> photovoltaicPanels;
	
	/**
	 * Build a house that both consume and produce energy
	 * @param id automatically generate by program to represent a prosumer
	 * @param houseNumber identity of prosumer
	 */
	public Prosumer( int id, int houseNumber ) {
		this.id = id;
		this.houseNumber = houseNumber;
		this.windTurbines = new ArrayList<WindTurbine> ();
		this.photovoltaicPanels = new ArrayList<PhotovoltaicPanel> ();
	}
	
	/**
	 * Add a wind turbine to the prosumer
	 * @param wt wind turbine
	 */
	public void addWindTurbine( WindTurbine wt ) {
		this.windTurbines.add( wt );
	}
	
	/**
	 * Add a photovoltaic panel to the prosumer
	 * @param pp photovoltaic panel
	 */
	public void addPhotovoltaicPanel( PhotovoltaicPanel pp ) {
		this.photovoltaicPanels.add( pp );
	}
	
	/**
	 * Calculate the power
	 * 
	 * @param airDensity
	 * @param windSpeed
	 * @param solarRadiation
	 * @return the total power combining of all wind turbines and photovoltaic panels
	 */
	public double powerOutput( double airDensity, double windSpeed, double solarRadiation ) {
		double power = 0;
		
		for( WindTurbine wt : this.windTurbines ) {
			power = power + wt.powerOutput(airDensity, windSpeed);
		}
		for( PhotovoltaicPanel pp : this.photovoltaicPanels ) {
			power = power + pp.powerOutput(solarRadiation);
		}
		
		return power;
	}
	
	/** @return id that automatically generate by program to represent a prosumer */
	public int getID( ) {
		return this.id;
	}
	
	/** @return the house number of the prosumer */
	public int getHouseNumber( ) {
		return this.houseNumber;
	}
	
	/** @return a array list of wind turbines */
	public ArrayList<WindTurbine> getWindTurbines( ){
		return this.windTurbines;
	}
	
	/** @return a array list of photovoltaic panels */
	public ArrayList<PhotovoltaicPanel> getPhotovoltaicPanels( ){
		return this.photovoltaicPanels;
	}
	
	/** @return a string of house number, information of wind turbines, and information of photovoltaic panels */
	public String info( ) {
		String info = "House number: " + this.houseNumber + "\n";
		for( WindTurbine wt : this.windTurbines ) {
			info = info + "Wind Turbines:\n";
			info = info + wt.info();
		}
		for( PhotovoltaicPanel pp : this.photovoltaicPanels ) {
			info = info + "Photovoltaic Panels:\n";
			info = info + pp.info();
		}
		
		return info;
	}
}
