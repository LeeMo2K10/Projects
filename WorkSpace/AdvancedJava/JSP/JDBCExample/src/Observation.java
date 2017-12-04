
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 * A bean that extracts weather information from the U. S. National Weather
 * Service web site
 */
public class Observation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The base URL for National Weather Service data
	 */
	private static final String BASEURL = "http://weather.noaa.gov/weather/current";
	/**
	 * Date format used for parsing observation time
	 */
	private static final SimpleDateFormat DATEFMT = new SimpleDateFormat("MMM dd, yyyy - hh:mm aa zzz");
	/**
	 * Airport code
	 */
	private String airportCode;
	/**
	 * Full name of location
	 */
	private String location;
	/**
	 * Time of observation
	 */
	private Date time;
	/**
	 * Temperature in degrees Celsius
	 */
	private Double temperature;

	/**
	 * Sets the airport code, which causes the bean to be reloaded.
	 * 
	 * @param airportCode
	 *            the airportCode.
	 */
	public void setAirportCode(String airportCode) throws IOException {
		this.airportCode = airportCode;
		loadFromURL(getURL());
	}

	/**
	 * Returns the location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the airport code
	 */
	public String getAirportCode() {
		return airportCode;
	}

	/**
	 * Sets the location.
	 * 
	 * @param location
	 *            the location.
	 */
	protected void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the time.
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time
	 *            the time.
	 */
	protected void setTime(Date time) {
		this.time = time;
	}

	/**
	 * Returns the temperature.
	 */
	public double getTemperature() {
		return (temperature == null) ? 0 : temperature.doubleValue();
	}

	/**
	 * Sets the temperature.
	 * 
	 * @param temperature
	 *            the temperature.
	 */
	protected void setTemperature(double temperature) {
		this.temperature = new Double(temperature);
	}

	/**
	 * Returns the URL of the NWS web page that contains current weather
	 * conditions at the airport
	 */
	public URL getURL() throws MalformedURLException {
		StringBuffer sb = new StringBuffer();
		sb.append(BASEURL);
		sb.append("/K");
		sb.append(airportCode.toUpperCase());
		sb.append(".html");
		return new URL(sb.toString());
	}

	/**
	 * Loads the weather data from a URL
	 */
	protected void loadFromURL(URL url) throws IOException {
		load(url.openStream());
	}

	protected void load(InputStream stream) throws IOException {
		location = null;
		time = null;
		temperature = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		for (;;) {
			String line = in.readLine();
			if (line == null)
				break;
			if (location == null)
				parseLocation(line);
			if (time == null)
				parseTime(line);
			if (temperature == null)
				parseTemperature(line);
		}
		in.close();
	}

	protected void parseLocation(String line) {
		final String TOKEN1 = "<TITLE>";
		final String TOKEN2 = "-";
		final String TOKEN3 = "</TITLE>";
		int p = line.indexOf(TOKEN1);
		if (p != -1) {
			p += TOKEN1.length();
			p = line.indexOf(TOKEN2, p);
			if (p != -1) {
				p += TOKEN2.length();
				int q = line.indexOf(TOKEN3);
				if (q != -1) {
					String token = line.substring(p, q).trim();
					StringTokenizer st = new StringTokenizer(token, ",");
					token = st.nextToken();
					token = st.nextToken();
					setLocation(token);
				}
			}
		}
	}

	/**
	 * Searches the current line for the time
	 */
	protected void parseTime(String line) {
		final String TOKEN1 = "<OPTION SELECTED>";
		final String TOKEN2 = "<OPTION>";
	}

	/**
	 * Searches the current line for the temperature
	 */
	protected void parseTemperature(String line) {
		final String TOKEN1 = "(";
		final String TOKEN2 = "C)";
		int q = line.lastIndexOf(TOKEN2);
		if (q != -1) {
			int p = line.lastIndexOf(TOKEN1);
			if (p != -1) {
				p += TOKEN1.length();
				String token = line.substring(p, q).trim();
				try {
					p = line.indexOf(TOKEN1);
					if (p != -1) {
						p += TOKEN1.length();
						q = line.indexOf(TOKEN2, p);
						if (q != -1) {
							token = line.substring(p, q).trim();
							Date date = DATEFMT.parse(token, new ParsePosition(0));
							if (date != null)
								setTime(date);
						}
					}
					setTemperature(Double.parseDouble(token));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}

}