package com.example.geoprofile;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	public static final String PREFS_NAME = "MyProfilesFile";
	String profile;
	LatLng location;
	Intent intent;
	final int PICK_CONTACT = 1;
	final int PICK_RINGTONE = 2;
	int PICK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		String[] values = { "Edit Profile Name", "Block Number",
				"Block Contacts", "Unblock Numbers", "Set Ringtone",
				"Set Ringer Volume", "Delete Profile" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey("Profile")) {
				profile = getIntent().getExtras().getString("Profile");
				TextView tv = (TextView) findViewById(R.id.textView1);
				tv.setText(profile);
			}
			if (getIntent().getExtras().containsKey("Location")) {
				location = getIntent().getExtras().getParcelable("Location");
				setDefaults();
			}
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					editProfile();
				} else if (position == 1) {
					blockNumber();
				} else if (position == 2) {
					blockContact();
				} else if (position == 3) {
					unblockNumbers();
				} else if (position == 4) {
					setRingtone();
				} else if (position == 5) {
					setRingerVolume();
				} else if (position == 6) {
					deleteProfile();
				}
			}
		});

		Button btnHome = (Button) findViewById(R.id.button1);

		btnHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent = new Intent(getBaseContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

	protected void unblockNumbers() {
		String numbers = "";
		int profileNo;
		profileNo = 0;
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = profilesFile.edit();
		int size = profilesFile.getInt("Profiles_Number", -1);
		for (int i = 1; i <= size; i++) {
			if (profile.equals(profilesFile.getString("Profile" + i + "_Name",
					""))) {
				numbers = profilesFile.getString("Profile" + i
						+ "_BlockedNumbers", "");
				profileNo = i;
				break;
			}
		}
		if (!numbers.isEmpty()) {
			if (numbers.contains(",")) {
				final int x = profileNo;
				final String[] tokens = numbers.split(",");

				final ArrayList<Integer> list = new ArrayList<Integer>();
				new AlertDialog.Builder(this)
						.setTitle("Unblock Numbers")
						.setMultiChoiceItems(
								tokens,
								null,
								new DialogInterface.OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										list.add(which);

									}
								})
						.setPositiveButton("Remove",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										for (int i = 0; i < tokens.length; i++) {
											for (int j = 0; j < list.size(); j++) {
												if (i == list.get(j)
														&& i < (tokens.length - 1)) {
													tokens[i] = tokens[i + 1];
												}
											}
										}
										String nos = "";
										for (int i = 0; i < (tokens.length - list
												.size()); i++) {
											nos = nos + tokens[i] + ",";
										}
										editor.putString("Profile" + x
												+ "_BlockedNumbers", nos);
										editor.commit();
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
			} else {
				final int x1 = profileNo;
				new AlertDialog.Builder(this)
						.setTitle("Unblock Number")
						.setMessage(
								"Do you want to unblock " + numbers
										+ " for this profile?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										editor.remove("Profile" + x1
												+ "_BlockedNumbers");
										editor.commit();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}
								}).show();
			}

		}
	}

	protected void deleteProfile() {
		new AlertDialog.Builder(this)
				.setTitle("Delete Profile")
				.setMessage("Are you sure you?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SharedPreferences profilesFile = getSharedPreferences(
										PREFS_NAME, 0);
								SharedPreferences.Editor editor = profilesFile
										.edit();
								int size = profilesFile.getInt(
										"Profiles_Number", -1);
								for (int i = 1; i <= size; i++) {
									if (profile.equals(profilesFile.getString(
											"Profile" + i + "_Name", ""))) {
										for (int j = i; j < size - 1; j++) {
											editor.putString("Profile" + j
													+ "_Name", profilesFile
													.getString("Profile" + j
															+ 1 + "_Name", ""));
											editor.putString(
													"Profile" + j
															+ "_BlockedNumbers",
													profilesFile
															.getString(
																	"Profile"
																			+ j
																			+ 1
																			+ "_BlockedNumbers",
																	""));
											editor.putString("Profile" + j
													+ "_Ringtone", profilesFile
													.getString("Profile" + j
															+ 1 + "_Ringtone",
															""));
											editor.putString(
													"Profile" + j
															+ "_RingVolume",
													profilesFile
															.getString(
																	"Profile"
																			+ j
																			+ 1
																			+ "_RingVolume",
																	""));
											editor.putString("Profile" + j
													+ "_Location", profilesFile
													.getString("Profile" + j
															+ 1 + "_Location",
															""));
										}
										editor.remove("Profile" + size
												+ "_Name");
										editor.remove("Profile" + size
												+ "_BlockedNumbers");
										editor.remove("Profile" + size
												+ "_Ringtone");
										editor.remove("Profile" + size
												+ "_RingVolume");
										editor.remove("Profile" + size
												+ "_Location");
										break;

									}
								}
								editor.putInt("Profiles_Number", size - 1);
								editor.commit();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}

	protected void setRingerVolume() {
		final SeekBar volume = new SeekBar(this);
		final AudioManager amanager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		volume.setMax(amanager.getStreamMaxVolume(AudioManager.STREAM_RING));
		volume.setProgress(amanager.getStreamVolume(AudioManager.STREAM_RING));
		volume.setKeyProgressIncrement(1);
		new AlertDialog.Builder(this)
				.setTitle("Ring Volume")
				.setView(volume)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								int profileNo = 0;
								SharedPreferences profilesFile = getSharedPreferences(
										PREFS_NAME, 0);
								SharedPreferences.Editor editor = profilesFile
										.edit();
								int size = profilesFile.getInt(
										"Profiles_Number", -1);
								for (int i = 1; i <= size; i++) {
									String profileName = profilesFile
											.getString("Profile" + i + "_Name",
													"");
									if (profile.equals(profileName)) {
										profileNo = i;
									}
								}
								editor.putInt("Profile" + profileNo
										+ "_RingVolume", volume.getProgress());
								editor.commit();

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

	protected void block(String number) {
		int profileNo = 0;
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = profilesFile.edit();
		int size = profilesFile.getInt("Profiles_Number", -1);
		for (int i = 1; i <= size; i++) {
			String profileName = profilesFile.getString(
					"Profile" + i + "_Name", "");
			if (profile.equals(profileName)) {
				profileNo = i;
			}
		}
		String numbers = profilesFile.getString("Profile" + profileNo
				+ "_BlockedNumbers", "");
		if (numbers.isEmpty()) {
			numbers = number;
		} else {
			numbers = numbers + "," + number;
		}
		editor.putString("Profile" + profileNo + "_BlockedNumbers", numbers);
		editor.commit();

	}

	protected void setRingtone() {
		PICK = 2;
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		startActivityForResult(intent, PICK_RINGTONE);
	}

	protected void blockContact() {
		PICK = 1;
		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case (PICK_CONTACT):
			if (PICK == 1) {
				try {
					Uri contactData = data.getData();
					String id, phone;
					int idx, hasPhone;
					Cursor cursor = getContentResolver().query(contactData,
							null, null, null, null);
					if (cursor.moveToFirst()) {
						idx = cursor
								.getColumnIndex(ContactsContract.Contacts._ID);
						id = cursor.getString(idx);

						idx = cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
						hasPhone = cursor.getInt(idx);

						if (hasPhone == 1) {
							Cursor pCur = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = ?",
											new String[] { id }, null);

							while (pCur.moveToNext()) {
								phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								block(phone);
							}
						}
					}
				} catch (Exception e) {
					Log.d("demo", e.toString());
				}

			}

		case (PICK_RINGTONE):
			if (PICK == 2) {
				Uri uri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				int profileNo = 0;
				SharedPreferences profilesFile = getSharedPreferences(
						PREFS_NAME, 0);
				SharedPreferences.Editor editor = profilesFile.edit();
				int size = profilesFile.getInt("Profiles_Number", -1);
				for (int i = 1; i <= size; i++) {
					String profileName = profilesFile.getString("Profile" + i
							+ "_Name", "");
					if (profile.equals(profileName)) {
						profileNo = i;
					}
				}
				editor.putString("Profile" + profileNo + "_Ringtone",
						uri.toString());
				editor.commit();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void blockNumber() {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle("Block Number")
				.setMessage("Please enter a number to block")
				.setView(input)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String number = input.getText().toString();
								if (!number.isEmpty()) {
									block(number);
								} else {
									Toast.makeText(ProfileActivity.this,
											"No number blocked!",
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

	protected void editProfile() {
		final EditText input = new EditText(this);
		input.setText(profile);
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
									SharedPreferences profilesFile = getSharedPreferences(
											PREFS_NAME, 0);
									int size = profilesFile.getInt(
											"Profiles_Number", -1);
									for (int i = 1; i < size; i++) {
										if (profile.equals(profilesFile
												.getString("Profile" + i
														+ "_Name", ""))) {
											SharedPreferences.Editor editor = profilesFile
													.edit();
											profile = input.getText()
													.toString();
											editor.putString("Profile" + i
													+ "_Name", profile);
											editor.commit();
											TextView tv = (TextView) findViewById(R.id.textView1);
											tv.setText(profile);
										}
									}
								} else {
									Toast.makeText(ProfileActivity.this,
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

	protected void setDefaults() {
		SharedPreferences profilesFile = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = profilesFile.edit();
		int size = profilesFile.getInt("Profiles_Number", -1);
		int flag = 0;
		for (int i = 0; i < size; i++) {
			if (profile.equals(profilesFile.getString("Profile" + i + "_Name",
					""))) {
				flag = 1;
				break;
			}
		}
		if (flag == 0) {
			if (size == -1) {
				editor.putInt("Profiles_Number", 1);
				editor.putString("Profile1_Name", profile);
				editor.putString("Profile1_Location", location.toString());
				editor.commit();
			} else {
				size++;
				editor.putInt("Profiles_Number", size);
				editor.putString("Profile" + size + "_Name", profile);
				editor.putString("Profile" + size + "_Location",
						location.toString());
				editor.commit();
			}
		} else {
			Toast.makeText(ProfileActivity.this,
					"Profile Name already exists. Please create a new one",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}
