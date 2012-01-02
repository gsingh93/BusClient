package gsingh.busapp.busclient;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusClientActivity extends Activity {

	private LocationManager locManager = null;
	private LocationListener locListener = null;

	/**
	 * Text view where user sees all information such as latitude, longitude,
	 * and instructions
	 */
	private TextView display = null;

	/**
	 * True of the bus is being tracked, false otherwise
	 */
	private Boolean tracking = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		display = (TextView) findViewById(R.id.display);

		/* Use the LocationManager class to obtain GPS locations */
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener(display);
    }

	public void onClickLocate(View v) {
		if (tracking == false) {
			((MyLocationListener) locListener).setBusName(display.getText()
					.toString());
			// TODO: How frequent are the updates
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, locListener);
			display.append("\nTracking service has started");
			
			((Button) v).setText("Start");

			tracking = true;
		} else {
			locManager.removeUpdates(locListener);
			display.append("\nTracking service has stopped");
			((Button) v).setText("Stop");
		}

	}
}