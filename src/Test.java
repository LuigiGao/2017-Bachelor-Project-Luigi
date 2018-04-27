import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {

	public static void main( String... args ) {
		parseRelations( );
	}
	
	private static void parseProsumers( ) {
		JSONParser parser = new JSONParser();
	    
		try {
			Object obj = parser.parse( new FileReader( "src/prosumers_info.json" ) );
			JSONArray array = (JSONArray)obj;

			JSONObject prosumer = (JSONObject)array.get(0);
			int houseNumber = Integer.parseInt( (String) prosumer.get("houseNumber"));
			System.out.println( houseNumber );
			
			JSONArray windTurbines = (JSONArray)prosumer.get("windTurbines");
			
			Iterator<JSONObject> iterator = windTurbines.iterator();
			while (iterator.hasNext()) {
				JSONObject windTurbine = iterator.next();
				double bladeLength = Double.parseDouble( (String) windTurbine.get("bladeLength") );
				double maxPowerOutput = Double.parseDouble( (String) windTurbine.get("maxPowerOutput") );
				System.out.println( bladeLength );
				System.out.println( maxPowerOutput );
            }
			/*
			JSONObject windTurbine = (JSONObject)windTurbines.get(0);
			double bladeLength = Double.parseDouble( (String) windTurbine.get("bladeLength") );
			double maxPowerOutput = (Long) windTurbine.get("maxPowerOutput");
			System.out.println( bladeLength );
			System.out.println( maxPowerOutput );
			*/
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void parseRelations( ) {
		JSONParser parser = new JSONParser();
	    
		try {
			Object obj = parser.parse( new FileReader( "src/relations_info.json" ) );
			JSONObject relation = (JSONObject)obj;

			JSONArray connections = (JSONArray)relation.get("connections");
			JSONObject connection = (JSONObject) connections.get(0);
			int houseNumber1 = Integer.parseInt( (String) connection.get("houseNumber1"));
			int houseNumber2 = Integer.parseInt( (String) connection.get("houseNumber2"));
			double resistance = Double.parseDouble( (String) connection.get("resistance"));
			System.out.println( houseNumber1 + " " + houseNumber2 + " " + resistance);
			
			int electric_Utility = Integer.parseInt( (String) relation.get("electric_Utility") );
			System.out.println( electric_Utility );
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
