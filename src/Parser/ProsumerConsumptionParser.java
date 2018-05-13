package Parser;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Network.Prosumer;

public class ProsumerConsumptionParser {

	private String file_location;

	public ProsumerConsumptionParser( String file_location) {
		this.file_location = file_location;
	}

	public double[] parseConsumption() {
		double[] consumption = new double[24];
		int n = 0;
		
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse( new FileReader( this.file_location ));
			for( ; n < 24; n++ ) {
				consumption[n] = Double.parseDouble( (String) obj.get( "" + n ) );
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return consumption;
	}

}
