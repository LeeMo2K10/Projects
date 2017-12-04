import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
* A bean that extracts weather information from
* the U. S. National Weather Service web site
*/
public class Observation implements Serializable
{
/**
* The base URL for National Weather Service data
*/
private static final String BASEURL =
"http://weather.noaa.gov/weather/current";
/**
* Date format used for parsing observation time
*/
private static final SimpleDateFormat DATEFMT =
new SimpleDateFormat("MMM dd, yyyy - hh:mm aa zzz");
/**
* Airport code
*/
private String airportCode;
/**
* Full name of location
*/
private String location;Chapter 15:
JSP and JavaBeans
437
/**
* Time of observation
*/
private Date time;
/**
* Temperature in degrees Celsius
*/
private Double temperature;
// ===========================================
//
Bean accessor methods
// ===========================================
/**
* Sets the airport code, which
* causes the bean to be reloaded.
* @param airportCode the airportCode.
*/
public void setAirportCode(String airportCode)
throws IOException
{
this.airportCode = airportCode;
loadFromURL(getURL());
}
/**
* Returns the location.
*/
public String getLocation()
{
return location;
}
/**
* Returns the airport code
*/
public String getAirportCode()
{
return airportCode;
}