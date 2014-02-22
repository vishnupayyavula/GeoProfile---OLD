package com.example.geoprofile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver{

	String[] blockedNumbers = LocationService.blockedNumbers;
	String incomingNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (blockedNumbers != null) {
			Bundle b = intent.getExtras();
			incomingNumber = b
					.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				Class<?> c;
				c = Class.forName(tm.getClass().getName());
				Method m = c.getDeclaredMethod("getITelephony");
				m.setAccessible(true);
				com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m
						.invoke(tm);

				for (int i = 0; i < blockedNumbers.length; i++) {
					if (blockedNumbers[i].equals(incomingNumber)) {
						telephonyService.endCall();
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
