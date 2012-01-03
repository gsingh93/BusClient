package gsingh.busapp.busclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MyLocationListener implements LocationListener {

	/**
	 * Server name
	 */
	private final String SERVERNAME = "http://michigangurudwara.com";

	/**
	 * XML file name
	 */
	private final String FILENAME = "/coord.xml";

	/**
	 * Information TextView
	 */
	TextView display = null;

	/**
	 * Bus this client is tracking
	 */
	Bus bus = null;

	/**
	 * Latitude of user
	 */
	double lat;

	/**
	 * Longitude of user
	 */
	double lon;

	MyLocationListener(TextView display) {
		super();
		this.display = display;
	}

	public void initBus(String name) {
		// Get all stops for bus

		// Get all routes from XML file
		NodeList nl = getRouteList();

		String routeName = null;
		NodeList sl = null;
		List<String> stopNames = new LinkedList<String>();

		// For each route, get all stops
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element route = (Element) nl.item(i);

				routeName = route.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();

				if (routeName.equals(name)) {
					// For each stop, get the stop name and add it to the list
					sl = route.getElementsByTagName("stop");

					if (sl != null && sl.getLength() > 0) {
						for (int j = 0; j < sl.getLength(); j++) {
							Element stop = (Element) sl.item(j);

							stopNames.add(stop.getElementsByTagName("name")
									.item(0).getFirstChild().getNodeValue());
						}

						// Once all stops for this bus have been added, create
						// the bus
						bus = new Bus(name, stopNames);

						// Since we're done, break
						break;
					}
				}
			}
		}
	}

	private NodeList getRouteList() {
		// Setup HTTP connection to XML file
		URL url;
		NodeList nl = null;
		try {
			url = new URL(SERVERNAME + FILENAME);
			InputStream URLStream = url.openStream();

			// Retrieve DOM from XML file
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(URLStream);

			Element docEl = dom.getDocumentElement();

			nl = docEl.getElementsByTagName("route");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nl;
	}

	private void updateBusLocation() {

		// TODO: Make server update route for given routename
		// Location GET URL

		Log.d("tag", bus.getName());
		Log.d("tag", String.valueOf(lat));
		Log.d("tag", String.valueOf(lon));

		String URLText = String.format(SERVERNAME
				+ "/bus.php?routeName=%s&lat=%f&lon=%f", bus.getName(), lat,
				lon);

		List<String> stopNames = bus.getStopNames();
		List<Double> times = bus.getStopTimes();
		List<Double> distances = bus.getStopDistances();

		int i = 0;
		StringBuilder sb = new StringBuilder(URLText);
		for (String name : stopNames) {
			sb.append("&name[]=").append(name);
			sb.append("&time[]=").append(times.get(i));
			sb.append("&distance[]=").append(distances.get(i));
			i++;
		}

		URLText = sb.toString();

		URLText = URLText.replace(" ", "%20");

		Log.d("tag", URLText);

		// Initialize HTTP objects
		DefaultHttpClient hc = new DefaultHttpClient();
		HttpPost postMethod = new HttpPost(URLText);

		// Send data
		try {
			hc.execute(postMethod);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateScreenText() {

		// Create text to be displayed on screen
		String Text = String.format(
				"\nMy current location is:\nLatitude = %f\nLongitude = %f",
				lat, lon);

		// Display location
		display.append(Text);
		display.append("\n\nCheck http://michigangurudwara.com/bus.php to see your location");
	}

	@Override
	public void onLocationChanged(Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();

		// Update text on the screen
		updateScreenText();

		// Send bus location to server
		updateBusLocation();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}