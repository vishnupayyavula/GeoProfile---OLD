package com.example.geoprofile;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "MyProfilesFile";
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean statusOfGPS = manager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(!statusOfGPS){
			Toast.makeText(this, "GPS is off", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		int size = profilesFile.getInt("Profiles_Number", -1);
		if (size >= 1) {
			startService(new Intent(getBaseContext(), LocationService.class));
		}
		String[] values = { "Profiles", "Map", "Stop Service" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					intent = new Intent(getBaseContext(),
							ProfilesActivity.class);
					startActivity(intent);
				} else if (position == 1) {
					intent = new Intent(getBaseContext(), MapActivity.class);
					startActivity(intent);
				} else if (position == 2) {
					StopService();
				}

			}
		});

		Button btnExit = (Button) findViewById(R.id.button1);
		btnExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				System.exit(0);
			}
		});
	}

	protected void StopService() {
		stopService(new Intent(getBaseContext(), LocationService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
