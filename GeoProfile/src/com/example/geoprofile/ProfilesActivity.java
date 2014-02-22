package com.example.geoprofile;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ProfilesActivity extends Activity {
	Intent intent;
	public static final String PREFS_NAME = "MyProfilesFile";
	ArrayList<String> profiles;
	ArrayList<String> locations;
	ArrayList<String> ringtones;
	ArrayList<String> blockedNumbers;
	ArrayList<Integer> ringVolumes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);

		profiles = new ArrayList<String>();
		locations = new ArrayList<String>();
		ringtones = new ArrayList<String>();
		blockedNumbers = new ArrayList<String>();
		ringVolumes = new ArrayList<Integer>();

		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		// SharedPreferences.Editor editor = profilesFile.edit();
		// editor.clear();
		// editor.commit();
		int size = profilesFile.getInt("Profiles_Number", -1);
		if (size >= 1) {
			for (int i = 1; i <= size; i++) {
				profiles.add(profilesFile.getString("Profile" + i + "_Name",
						""));
				locations.add(profilesFile.getString("Profile" + i
						+ "_Location", ""));
				ringtones.add(profilesFile.getString("Profile" + i
						+ "_Ringtone", ""));
				blockedNumbers.add(profilesFile.getString("Profile" + i
						+ "_BlockedNoumbers", ""));
				ringVolumes.add(profilesFile.getInt("Profile" + i
						+ "_RingVolume", -1));
			}
			loadListView();
		} else {
			Toast.makeText(this, "No profiles available!", Toast.LENGTH_SHORT)
					.show();
		}

		Button btnHome = (Button) findViewById(R.id.button1);
		Button btnAddProfile = (Button) findViewById(R.id.button2);
		Button btnRemoveProfile = (Button) findViewById(R.id.button3);

		btnHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent = new Intent(getBaseContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		btnAddProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(getBaseContext(), MapActivity.class);
				startActivity(intent);
			}
		});

		btnRemoveProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removeProfile();
			}
		});
	}

	protected void loadListView() {
		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				profiles);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(getBaseContext(), ProfileActivity.class);
				intent.putExtra("Profile", profiles.get(position));
				startActivity(intent);
			}
		});
	}

	protected void removeProfile() {
		CharSequence[] items = new CharSequence[profiles.size()];
		final ArrayList<Integer> removedProfiles = new ArrayList<Integer>();
		for (int i = 0; i < profiles.size(); i++) {
			items[i] = profiles.get(i);
		}
		new AlertDialog.Builder(this)
				.setTitle("Remove Profiles")
				.setMultiChoiceItems(items, null,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								removedProfiles.add(which);
							}
						})
				.setPositiveButton("Remove",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								SharedPreferences profilesFile = getSharedPreferences(
										PREFS_NAME, 0);
								SharedPreferences.Editor editor = profilesFile
										.edit();
								for (int j : removedProfiles) {
									profiles.remove(j);
									locations.remove(j);
									ringtones.remove(j);
									blockedNumbers.remove(j);
									ringVolumes.remove(j);
								}
								editor.clear();
								editor.commit();
								if (profiles.size() != 0) {
									editor.putInt("Profiles_Number",
											profiles.size());
									for (int i = 1; i <= profiles.size(); i++) {
										editor.putString("Profile" + i
												+ "_Name", profiles.get(i - 1));
										editor.putString("Profile" + i
												+ "_Location",
												locations.get(i - 1));
										editor.putString("Profile" + i
												+ "_Ringtone",
												ringtones.get(i - 1));
										editor.putString("Profile" + i
												+ "_BlockedNumbers",
												blockedNumbers.get(i - 1));
										editor.putInt("Profile" + i
												+ "_RingVolume",
												ringVolumes.get(i - 1));
									}

									editor.commit();
								} else {
									Toast.makeText(ProfilesActivity.this, "No profiles available!", Toast.LENGTH_SHORT)
									.show();
								}
								loadListView();
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
		getMenuInflater().inflate(R.menu.profiles, menu);
		return true;
	}

}
