package com.example.geoprofile;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

public class MapActivity extends Activity {
	public static final String PREFS_NAME = "MyProfilesFile";
	GoogleMap mMap;
	Intent intent;
	LocationManager locMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
//				Toast.makeText(MapActivity.this,
//						"Lat:" + location.getLatitude() + " Long:"+location.getLongitude(),
//						Toast.LENGTH_SHORT).show();
				Log.d("demo", "Lat:" + location.getLatitude() + " Long:"+location.getLongitude());
			}
		};
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		int size = profilesFile.getInt("Profiles_Number", -1);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		if (size >= 1) {
			for (int i = 1; i <= size; i++) {
				String profile = profilesFile.getString(
						"Profile" + i + "_Name", null);
				String loc = profilesFile.getString(
						"Profile" + i + "_Location", null);
				String latlng = loc.substring(loc.indexOf("(") + 1,
						loc.length() - 2);
				String[] tokens = latlng.split(",");
				LatLng location = new LatLng(Double.parseDouble(tokens[0]),
						Double.parseDouble(tokens[1]));
				mMap.addMarker(new MarkerOptions().position(location).title(
						profile));
			}
		}
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				35.30728201, -80.72284712), 13));
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				createProfile(point);

			}
		});
	}

	protected void createProfile(final LatLng loc) {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle("Profile")
				.setMessage("Please enter a profile name")
				.setView(input)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String profileName = input.getText().toString();
								if (!profileName.isEmpty()) {
									mMap.addMarker(new MarkerOptions()
											.position(loc).title(profileName));
									intent = new Intent(getBaseContext(),
											ProfileActivity.class);
									intent.putExtra("Profile", profileName);
									intent.putExtra("Location", loc);
									startActivity(intent);
								} else {
									Toast.makeText(MapActivity.this,
											"Profile not saved!",
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}
