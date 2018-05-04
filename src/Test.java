import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {

	public static void main( String... args ) {
		testDate( );
		//testWeather( );
		//testSimulationWeather( );
		
	}
	
	public static void testSimulationWeather( ) {
		
		Topology test = new Topology( "src/prosumers_info.json", "src/relations_info.json", "src/KNMI_20171231.txt", 3);
		Thread t = new Thread( test );
		t.run();
		
	}
	
	public static void testWeather( ) {
		WeatherDatabase database = new WeatherDatabase( "src/KNMI_20171231.txt" );
		database.prediction();
	}
	
	public static void testDate( ) {
		Date date = new Date( 1998, 9 - 1 ,18 );
		System.out.println( date.getYear() );
		System.out.println( date.getMonth() );
		System.out.println( date.getDate() );
		System.out.println( date );
	}
	
	public static void testTxt( ) {
		
		try {
			Scanner file = new Scanner( new File( "src/KNMI_20171231.txt" ));
			
			String line = file.nextLine();
			while( line.charAt(0) == '#') {
				line = file.nextLine();
			}
			//line = line.substring( line.indexOf(',') + 1, line.length());
			System.out.println( line );
			//System.out.println( line.indexOf(',') );
			
			while( file.hasNextLine() ) {
				line = file.nextLine();
				System.out.println( line );
			}
			
			/*
			double snt = 0;
			for( int i = 0; i < line.length(); i++ ) {
				if( line.charAt( i ) != ',' ) {
					if( '0' <= line.charAt(i) && line.charAt(i) <='9' ) {
						snt = snt * 10 + line.charAt(i) - '0';
					}
				}else {
					line = line.substring( i );
					System.out.println( snt );
					System.out.println( line );
				}
			}
			
			while( line.charAt(0) != EOF ) {

				while( line.charAt(0) != ',' && line.charAt(0) != 3 ) {
					if( '0' <= line.charAt(0) && line.charAt(0) <='9' ) {
						snt = snt * 10 + line.charAt(0) - '0';
					}
					line = line.substring( 1 );
				}
				System.out.println( snt );
				
			}
			*/
			//System.out.println( line );
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void testTopology( ) {
		
		Topology test = new Topology( "src/prosumers_info.json", "src/relations_info.json" );
		test.info();
		
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
