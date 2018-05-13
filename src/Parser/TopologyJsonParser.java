package Parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import EnergyGenerator.PhotovoltaicPanel;
import EnergyGenerator.WindTurbine;
import Network.NodeNetwork;
import Network.Prosumer;

public class TopologyJsonParser {

	private NodeNetwork network;
	private String prosumer_info;
	private String relation_info;
	private int prosumer_ID;
	
	public TopologyJsonParser( NodeNetwork network, String prosumer_info, String relation_info ) {
		
		this.network = network;
		this.prosumer_info = prosumer_info;
		this.relation_info = relation_info;
		
	}
	
	/**
	 * Build nodes of the topology
	 * 
	 * @param file path of a file that contain information of all prosumers
	 */
	public void parseProsumers( ) {

		this.prosumer_ID = 0;
		
		
		JSONParser parser = new JSONParser();
		Object prosumers_info;
		
		try {
			prosumers_info = parser.parse( new FileReader( this.prosumer_info ) );
			JSONArray prosumers = (JSONArray)prosumers_info;
			
			Iterator<JSONObject> iterator = prosumers.iterator();
			while (iterator.hasNext()) {
				JSONObject prosumer = iterator.next();
				parseProsumer( prosumer );
            }
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Add one prosumer to topology's nodes
	 * 
	 * @param obj information of one prosumer
	 */
	private void parseProsumer( JSONObject obj ) {
		
		int houseNumber = Integer.parseInt( (String) obj.get("houseNumber"));
		this.network.hashHouseId( houseNumber, this.prosumer_ID );
		//System.out.println( houseNumber );
		
		String energy_consumption = "src/Data/" + (String) obj.get("consumption");
		
		Prosumer prosumer = new Prosumer( this.prosumer_ID, houseNumber, energy_consumption );
		
		parseWindTurbine( prosumer, (JSONArray)obj.get("windTurbines") );
		parsePhotovoltaicPanel( prosumer, (JSONArray)obj.get("photovolaticPanels") );
		
		this.network.addProsumer( prosumer );
		this.prosumer_ID++;
		
	}
	
	/**
	 * Add wind turbines to a prosumer
	 * 
	 * @param prosumer a house that both consume and produce energy
	 * @param windTurbines a array of wind turbine's information
	 */
	private void parseWindTurbine( Prosumer prosumer, JSONArray windTurbines ) {
		
		Iterator<JSONObject> iterator = windTurbines.iterator();
		while (iterator.hasNext()) {
			JSONObject windTurbine = iterator.next();
			double bladeLength = Double.parseDouble( (String) windTurbine.get("bladeLength") );
			double maxPowerOutput = Double.parseDouble( (String) windTurbine.get("maxPowerOutput") );
			prosumer.addWindTurbine( new WindTurbine( bladeLength, maxPowerOutput ) );
			//System.out.println( bladeLength + " " + maxPowerOutput);
        }
		
	}
	
	/**
	 * Add photovoltaic panels to a prosumer
	 * 
	 * @param prosumer a house that both consume and produce energy
	 * @param photovoltaicPanels a array of photovoltaic panel's information
	 */
	private void parsePhotovoltaicPanel( Prosumer prosumer, JSONArray photovoltaicPanels ) {
		
		Iterator<JSONObject> iterator = photovoltaicPanels.iterator();
		while (iterator.hasNext()) {
			JSONObject windTurbine = iterator.next();
			double panelArea = Double.parseDouble( (String) windTurbine.get("panelArea") );
			double maxPowerOutput = Double.parseDouble( (String) windTurbine.get("maxPowerOutput") );
			prosumer.addPhotovoltaicPanel( new PhotovoltaicPanel( panelArea, maxPowerOutput ) );
			//System.out.println( panelArea + " " + maxPowerOutput);
        }
		
	}
	
	/**
	 * Build a connection topology among all houses
	 * 
	 * @param file path of a file that contain resistance, max load information among all prosumers
	 */
	public void parseRelations( ) {
		
		JSONParser parser = new JSONParser();
		Object relation_info;
		
		try {
			relation_info = parser.parse( new FileReader( this.relation_info ) );
			JSONObject relation = (JSONObject)relation_info;

			JSONArray connections = (JSONArray)relation.get("connections");
			Iterator<JSONObject> iterator = connections.iterator();
			while ( iterator.hasNext() ) {
				JSONObject connection = iterator.next();
				parseConnection( connection );
            }
			
			// parse electric utility
			this.network.setElectricUtility( Integer.parseInt( (String) relation.get("electric_Utility") ) );
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Add a connection between two prosumers
	 * 
	 * @param connection information of resistance, max load between two prosumers
	 */
	private void parseConnection( JSONObject connection ) {

		int houseA = this.network.getId( Integer.parseInt( (String) connection.get("houseNumber1")) );
		int houseB = this.network.getId( Integer.parseInt( (String) connection.get("houseNumber2")) );
		double resistance = Double.parseDouble( (String) connection.get("resistance"));
		double maxLoad = Double.parseDouble( (String) connection.get("max_load"));
		
		this.network.addConnection( houseA, houseB, maxLoad, resistance);
		
	}
	
	
	
}
