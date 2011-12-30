package gsingh.busapp.busclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MyLocationListener implements LocationListener {

	/**
	 * URL object of server
	 */
	URL url;

	/**
	 * Address of server
	 */
	String URLText = null;

	/**
	 * Information TextView
	 */
	TextView display = null;

	/**
	 * Latitude of user
	 */
	double lat;

	/**
	 * Longitude of user
	 */
	double lon;

	/**
	 * Name of the bus this client is tracking
	 */
	String busName = null;

	/**
	 * Bus this client is tracking
	 */
	Bus bus = null;

	MyLocationListener(TextView display) {
		super();
		this.display = display;
	}

	public void setBusName(String name) {
		this.busName = name;
		// TODO: Get all buses from XML file
		// Initialize all buses
		List<String> stopNames = new LinkedList<String>();

		stopNames.add("Stop1");
		stopNames.add("Stop2");
		stopNames.add("Stop3");

		bus = new Bus(busName, stopNames);
	}

	private void updateBusLocation(Bus bus) {

		// TODO: Add route name
		// Location GET URL
		URLText = String
				.format("http://michigangurudwara.com/bus.php?routeName=%s&lat=%f&lon=%f",
						bus.getName(), lat, lon);

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
		String Text = "My current location is: " + "\nLatitude = " + lat
				+ "\nLongitude = " + lon;

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

		updateBusLocation(bus);
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