package com.example.geoprofile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service {
	LocationManager locMgr;
	Location[] locations;
	public static final String PREFS_NAME = "MyProfilesFile";
	static String[] blockedNumbers;

	public void onCreate() {
		Toast.makeText(this, "LocationService: onCreate", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "LocationService: onStart", Toast.LENGTH_SHORT)
				.show();
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		int size = profilesFile.getInt("Profiles_Number", -1);
		if (size != -1) {
			locations = new Location[size];
			for (int i = 1; i <= size; i++) {
				String loc = profilesFile.getString(
						"Profile" + i + "_Location", "");
				String latlng = loc.substring(loc.indexOf("(") + 1,
						loc.length() - 2);
				String[] tokens = latlng.split(",");
				locations[i - 1] = new Location("Destination" + i);
				locations[i - 1].setLatitude(Double.parseDouble(tokens[0]));
				locations[i - 1].setLongitude(Double.parseDouble(tokens[1]));
			}
		}

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
				for (int i = 0; i < locations.length; i++) {
					float distance = location.distanceTo(locations[i]);
					if (distance < 250) {
						activateProfile(i + 1);
						break;
					}
				}
			}
		};
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "LocationService: onDestroy", Toast.LENGTH_SHORT)
				.show();
	}

	public void activateProfile(int profileNo) {
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		int size = profilesFile.getInt("Profiles_Number", -1);
		if (size != -1) {
			String uriString = profilesFile.getString("Profile" + profileNo
					+ "_Ringtone", "");
			if (!uriString.isEmpty()) {
				Uri uri = Uri.parse(uriString);
				RingtoneManager.setActualDefaultRingtoneUri(this,
						RingtoneManager.TYPE_RINGTONE, uri);
			}
			int ringVolume = profilesFile.getInt("Profile" + profileNo
					+ "_RingVolume", -1);
			if (ringVolume != -1) {
				AudioManager amanager = (AudioManager) this
						.getSystemService(Context.AUDIO_SERVICE);
				int volume = amanager.getStreamVolume(AudioManager.STREAM_RING);
				if (volume != ringVolume) {
					amanager.setStreamVolume(AudioManager.STREAM_RING,
							ringVolume, AudioManager.FLAG_SHOW_UI
									+ AudioManager.FLAG_PLAY_SOUND);
				}
			}
			String numbers = profilesFile.getString("Profile" + profileNo
					+ "_BlockedNumbers", "");
			if (!numbers.isEmpty()) {
				if (numbers.contains(",")) {
					blockedNumbers = numbers.split(",");
				} else {
					blockedNumbers = new String[1];
					blockedNumbers[0] = numbers;
				}
			} else {
				blockedNumbers = new String[1];
				blockedNumbers[0] = "";
			}
		}
	}
}
