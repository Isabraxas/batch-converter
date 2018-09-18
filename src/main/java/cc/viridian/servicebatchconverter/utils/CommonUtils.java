package cc.viridian.servicebatchconverter.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;


/**
 * A common methods and utils for the data generator for the baku stadium example.
 *
 * @author feronti
 *
 */

public class CommonUtils
{
	public String OS;
    //final public String ANSI_BLACK = "\u001B[30m";
    //final public String ANSI_RED   = "\u001B[31m";
    //final public String ANSI_GREEN = "\u001B[32m";
    //final public String ANSI_BLUE  = "\u001B[34m";
    //final public String ANSI_CLEAR = "\u001B[2J";

    int arrivedMessage = 0;


	//setup values
	public String servicesApi;
	public String aleHost;
	public String alePort;
	String broker;
	String clientId;
	int qos;

	ArrayList<HashMap<String,Object>> logicalReaders;
	ArrayList<HashMap<String,Object>> shifts;
	ArrayList<HashMap<String,Object>> zones;

	public CommonUtils() {
    }

    public String moveTo(int x, int y)
    {
		if (OS.equals( "WINDOWS" ))
		{
			return "";
		}
        String s = "\u001B[" + y + ";" + x +"H";
        return s;
    }


	private String getOS ()
	{
		String str = System.getProperty("os.name").toUpperCase();
		String[] splited = str.split("\\s+");
		return splited[0].trim();
	}

	public String clear()
	{
		String code = "\u001B[2J";

		if( OS.equals( "WINDOWS" ) ) {
			code = "\n\n";
		}
		return code;
	}

	public String black()
	{
		String code = "\u001B[30m";

		if( OS.equals( "WINDOWS" ) ) {
			code = "";
		}
		return code;
	}

	public String red()
	{
		String code = "\u001B[31m";

		if( OS.equals( "WINDOWS" ) ) {
			code = "";
		}
		return code;
	}

	public String green()
	{
		String code = "\u001B[32m";

		if( OS.equals( "WINDOWS" ) ) {
			code = "";
		}
		return code;
	}

	public String blue()
	{
		String code = "\u001B[34m";

		if( OS.equals( "WINDOWS" ) ) {
			code = "";
		}
		return code;
	}


	public void setTitle( String title)
    {
		String version = "1.0";

		try
		{
			version = "1.0." + read( "/build.txt" );
		} catch (Exception e) {
			System.out.println(e.getCause());
		}

		OS = getOS();
		System.out.println(OS);
        System.out.print (clear() + blue() +  moveTo(0,0) );
        System.out.println(" __      ___ _______      ");
        System.out.println(" \\ \\    / (_)___  (_)     ");
        System.out.println("  \\ \\  / / _   / / ___  __");
        System.out.println("   \\ \\/ / | | / / | \\ \\/ /");
        System.out.println("    \\  /  | |/ /__| |>  < ");
        System.out.println("     \\/   |_/_____|_/_/\\_\\");
        System.out.println("  " + title + " " + version );
        System.out.println(black() );


        System.out.print ( moveTo(1,9) );
    }

    public Properties readConfigFile()
    {
        Properties  prop  = new Properties();
        InputStream input = null;

        try {
            String filename = System.getProperty("user.dir") + "/vizix.config";
            input = new FileInputStream(filename);

            prop.load(input);

			Random r = new Random();
			//generic values
			servicesApi = prop.getProperty( "services.api" );
			aleHost     = prop.getProperty( "ale.host" );
			alePort     = prop.getProperty( "ale.port" );
			broker = prop.getProperty( "mqtt.broker" );
			clientId = prop.getProperty( "mqtt.clientId" ) + "_" + r.nextInt(1000);
			qos = Integer.parseInt( prop.getProperty( "mqtt.qos" ) );

            return prop;

        } catch (IOException ex) {
            System.out.println(red());
            System.out.println(ex.getMessage());
            System.out.println("exception: "+ex);
            System.out.println(black());
            System.exit(0);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return prop;
        }
    }

	public String read( String fname ) throws IOException
	{
		InputStream is = null;
		InputStreamReader isr = new InputStreamReader( is );
		BufferedReader br = new BufferedReader( isr );
		StringBuffer sb = new StringBuffer();
		String line;
		while( (line = br.readLine()) != null )
		{
			sb.append( line + "\n" );
		}
		br.close();
		return sb.toString();
	}

	//menu functions
	public void showItemMenu(String i, String option)
	{
		System.out.print(red());
		System.out.print(i +". ");
		System.out.print(black());
		System.out.print(option);
		System.out.println();
	}

	private Boolean isValidOption (String str, ArrayList<String> items) {
		boolean contains = false;
		for (String item : items) {
			if (str.equalsIgnoreCase(item)) {
				contains = true;
				break; // No need to look further.
			}
		}
		return contains;
	}


	public String getRandomLRCode()
	{
		Random r = new Random();

		if (logicalReaders.size() == 0)
		{
			System.out.println("Error, the LogicalReaders list is empty!");
		}

		int index = r.nextInt( logicalReaders.size());

		HashMap<String,Object> entry = (HashMap<String,Object>) logicalReaders.get(index);
		return  entry.get( "code" ).toString();

	}

	public String rtrim (Object s, int n)
	{
		if (s == null) {
			return alignRight( " ", n, ' ' );
		}
		return alignRight( s.toString(),n, ' ' );
	}

	public String ltrim (Object s, int n)
	{
		return alignLeft( s.toString(),n, ' ' );
	}

	public String alignRight (String s, int n)
	{
		return alignRight( s,n, ' ' );
	}

	public String alignLeft (String s, int n)
	{
		return alignLeft( s, n, ' ' );
	}

	private String alignRight (String s, int n, char c)
	{
		String space = "";
		for (int i = 0; i<n; i++) {
			space += c;
		}
		s = space + s;
		int len = s.length();
		return s.substring( len -n, len );
	}

	private String alignLeft (String s, int n, char c)
	{
		String space = s;
		for (int i = 0; i<n; i++) {
			space += c;
		}
		int len = space.length();
		return space.substring( 0, n );
	}

	public void simpleStats ()
	{
		HashMap<String, Object> map = new HashMap<>();

		System.out.println ("+----------------------+----------+----------+----------+---------------------+");
		System.out.println ("|ThingTypeCode         |  count   | parents  | children |         max         |");
		System.out.println ("|----------------------+----------+----------+----------+---------------------|");
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			HashMap<String, Object> row = map;
			System.out.print( "|" + alignLeft( row.get( "_id" ).toString(), 22, ' ') );
			System.out.print( "|" + alignRight( row.get("count").toString(), 9, ' ' ) + " ");
			System.out.print( "|" + alignRight( row.get("parent").toString(), 9, ' ' ) + " ");
			System.out.print( "|" + alignRight( row.get("children").toString(), 9, ' ' ) + " ");
			System.out.print( "|" + alignRight( row.get("max").toString(), 21, ' ' ) + "");
			System.out.println("|");
		}
		System.out.println ("+----------------------+----------+---------------------+---------------------+");
	}

}
