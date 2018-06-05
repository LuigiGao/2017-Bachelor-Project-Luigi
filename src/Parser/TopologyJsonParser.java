package Parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Network.Network;
import Network.Prosumer;

/**
 * A parser for topology information ( prosumers and their relations )
 * 
 * @author Luigi
 *
 */

public class TopologyJsonParser {

	/** A network of the prosumer */
	private Network network;

	/** Location of prosumers' information */
	private String prosumer_info;

	/** Location of relations' information */
	private String relation_info;

	/** Id of the next new prosumer */
	private int prosumer_ID;

	/**
	 * Build a parser for topology
	 * 
	 * @param network
	 * @param prosumer_info
	 * @param relation_info
	 */
	public TopologyJsonParser(Network network, String prosumer_info, String relation_info) {

		this.network = network;
		this.prosumer_info = prosumer_info;
		this.relation_info = relation_info;

	}

	/**
	 * Build prosumers (nodes) of the topology
	 * 
	 * @param file
	 *            path of a file that contain information of all prosumers
	 */
	public void parseProsumers() {

		this.prosumer_ID = 0;

		JSONParser parser = new JSONParser();
		Object prosumers_info;

		try {
			prosumers_info = parser.parse(new FileReader(this.prosumer_info));
			//JSONArray prosumers = (JSONArray) prosumers_info;
			JSONObject prosumers = (JSONObject) prosumers_info;
			
			JSONArray householders = (JSONArray) prosumers.get("householders");
			Iterator<String> iterator = householders.iterator();
			while (iterator.hasNext()) {
				String householder = iterator.next();
				parseProsumer(householder);
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Add one prosumer to topology's nodes
	 * 
	 * @param householder
	 *            Name of the householder
	 */
	private void parseProsumer(String householder) {

		this.network.hashHouseId(householder, this.prosumer_ID);

		Prosumer prosumer = new Prosumer(this.prosumer_ID, householder);

		this.network.addProsumer(prosumer);
		this.prosumer_ID++;

	}

	/**
	 * Build a connection topology among all houses
	 * 
	 * @param file
	 *            Path of a file that contain resistance, max load information among
	 *            all prosumers
	 */
	public void parseRelations() {

		JSONParser parser = new JSONParser();
		Object relation_info;

		try {
			relation_info = parser.parse(new FileReader(this.relation_info));
			JSONObject relation = (JSONObject) relation_info;

			JSONArray connections = (JSONArray) relation.get("connections");
			Iterator<JSONObject> iterator = connections.iterator();
			while (iterator.hasNext()) {
				JSONObject connection = iterator.next();
				parseConnection(connection);
			}

			// parse electric utility
			this.network.setElectricUtility(Integer.parseInt((String) relation.get("electric_Utility")));

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Add a connection between two prosumers
	 * 
	 * @param connection
	 *            Information of resistance, max load between two prosumers
	 */
	private void parseConnection(JSONObject connection) {

		int houseA = Integer.parseInt((String) connection.get("houseNumber1"));
		int houseB = Integer.parseInt((String) connection.get("houseNumber2"));
		// Normal resistance of transmission line is estimated to be 1 ohm
		// Take a bound +-20% to randomly generate resistance of transmission line
		double resistance = 0.8 + 0.4 * new Random().nextDouble(); // in 1 ohm
		double maxLoad = 10; // in 1 kW

		this.network.addConnection(houseA, houseB, maxLoad, resistance);

	}

}
