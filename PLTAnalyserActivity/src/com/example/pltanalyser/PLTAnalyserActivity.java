package com.example.pltanalyser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PLTAnalyserActivity extends Activity {

	WifiManager wifi;
	String wifis[];
	WifiScanReceiver wifiReciever;
	private ZoomableImageView image;
	private RelativeLayout containerLayout;
	static String imgDecodableString;

	private static final int RESULT_LOAD_MAP = 1;

	private static int relativeX;
	private static int relativeY;
	public static final String TAG = PLTAnalyserActivity.class.getSimpleName();

	private TelephonyManager tm;
	private Handler mHandler = new Handler();
	private int loopCounter = 0;
	private int fInterval = 0;
	private boolean taskInProgress = false;
	private Bitmap mutableBitmap;
	private String imageName = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");

		setContentView(R.layout.activity_pltanalyser);
		containerLayout = (RelativeLayout) findViewById(R.id.LinearLayout01);
		image = (ZoomableImageView) findViewById(R.id.imageView1);
		image.setOnLongPressImageViewListener(new ZoomableImageView.OnLongPressImageViewListener() {

			@Override
			public void onLongPressImageView(PointF touchPoint) {

				relativeX = (int) touchPoint.x;
				relativeY = (int) touchPoint.y;

				if (taskInProgress) {
					Log.i(TAG, "Scanning task in progress, wait for completion");
					Toast.makeText(PLTAnalyserActivity.this, "Scanning in progress, please wait till completion",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					/*
					 * Toast.makeText(getApplicationContext(), relativeX + "," +
					 * relativeY, Toast.LENGTH_SHORT) .show();
					 */
				}

				SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				String iteration = settingsPrefs.getString(SettingsActivity.KEY_ITERATIONS,
						SettingsActivity.DEFAULT_ITERATIONS);
				String interval = settingsPrefs.getString(SettingsActivity.KEY_INTEVAL,
						SettingsActivity.DEFAULT_INTERVAL);

				loopCounter = Integer.parseInt(iteration);
				fInterval = Integer.parseInt(interval);
				Log.d(TAG, "Fingerprint task scheduled, Iterations = " + loopCounter + ", Interval = " + fInterval);
				if (!taskInProgress)
					taskInProgress = true;
				circlePoint(relativeX, relativeY, mutableBitmap);
				fingerprintLocation();
			}
		});
	}

	protected void onStart() {
		super.onStart();
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiReciever = new WifiScanReceiver();
		registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		unregisterReceiver(wifiReciever);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fingerprint, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else if (id == R.id.action_load_map) {
			loadImagefromGallery();
		}
		return super.onOptionsItemSelected(item);
	}

	public void fingerprintLocation() {
		if (loopCounter > 0 && taskInProgress) {
			loopCounter--;
			Log.d(TAG, "fingerprintLocation(), loop coutner = " + loopCounter);

			// start cellular scan
			if (tm != null && mListener != null) {
				tm.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			}

			// start wi-fi scan
			if (wifi != null) {
				Log.d(TAG, "Wifi manager start scan");
				wifi.startScan();
			} else {
				Log.d(TAG, "Wifi manager null");
			}

		} else if (loopCounter == 0 && taskInProgress) {
			taskInProgress = false;
			Log.d(TAG, "Fingerprint complete");
			Toast.makeText(this, "Scanning complete", Toast.LENGTH_LONG).show();
		}
	}

	public void loadImagefromGallery() {

		// Create intent to Open Image applications like Gallery, Google Photos
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_MAP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			// When the MAP is selected
			if (requestCode == RESULT_LOAD_MAP && resultCode == RESULT_OK && null != data) {
				containerLayout.setBackgroundResource(0);
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				File f = new File("" + imgDecodableString);
				imageName = f.getName();

				cursor.close();

				image.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

				BitmapFactory.Options myOptions = new BitmapFactory.Options();
				myOptions.inDither = true;
				myOptions.inScaled = false;
				myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
				myOptions.inMutable = true;
				myOptions.inPurgeable = true;

				Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString, myOptions);

				Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
				mutableBitmap = workingBitmap.copy(myOptions.inPreferredConfig, true);
			} else {
				Toast.makeText(this, "Please select a map.", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
		}
	}

	void saveImage(Bitmap img) {
		String RootDir = Environment.getExternalStorageDirectory() + File.separator + "txt_imgs";
		File myDir = new File(RootDir);
		myDir.mkdirs();
		
		File file = new File(myDir, imageName);
		try {

			if (file == null)
				file.createNewFile();

			FileOutputStream out = new FileOutputStream(file);

			img.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void circlePoint(float relativeX, float relativeY, Bitmap mutableBitmap) {
		// TODO Auto-generated method stub

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL_AND_STROKE);

		Canvas canvas = new Canvas(mutableBitmap);
		canvas.drawCircle(relativeX, relativeY, 10, paint);

		image.setImageBitmap(mutableBitmap);
		saveImage(mutableBitmap);
	}

	// store Wi-Fi signal info
	public void getWifiData(Intent intent) {

		Log.i(TAG, "scanning wifi access points: " + intent.getAction());

		if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
			
			int rssi = wifi.getConnectionInfo().getRssi();
			int level = WifiManager.calculateSignalLevel(rssi, 5);
			System.out.println("Level is " + level + " out of 5");
			int dBm = (level * 100 / 10) - 100;

			String dataSet = "X= " + relativeX + ", Y= " + relativeY + " Wi-Fi Signal Strength: " + dBm + "dBm" + '\n';

			Toast.makeText(this, dataSet, Toast.LENGTH_LONG).show();
			DataStorage dataStorage = new DataStorage(getApplicationContext());
			dataStorage.writeWifiDataToFile(dataSet);

		}

	}

	// broadcast receiver to receive list of wifi signals values
	class WifiScanReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			Log.d(TAG, "wifi signal data rec");
			getWifiData(intent);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "scan iteration");
					fingerprintLocation();
				}
			}, fInterval * 1000);

		}
	}

	// store cellular signal info
	public void getcellularData(SignalStrength strength) {

		Log.i(TAG, "scanning cellular Signal data: " + strength);

		try {

			List<CellInfo> signalinfos = tm.getAllCellInfo();
			int SignalStrength = strength.getGsmSignalStrength();
			SignalStrength = (2 * SignalStrength) - 113; // -> dBm

			String cellularData = "";

			cellularData = "X=" + relativeX + ", Y=" + relativeY + " Cellular Signal Strength: " + SignalStrength
					+ "dBm" + '\n';

			Toast.makeText(this, cellularData, Toast.LENGTH_LONG).show();

			DataStorage dataStorage = new DataStorage(getApplicationContext());
			dataStorage.writeCellularDataToFile(cellularData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// phone state listener to listen cellular signal change
	final PhoneStateListener mListener = new PhoneStateListener() {
		@Override
		public void onSignalStrengthsChanged(SignalStrength sStrength) {
			getcellularData(sStrength);
			tm.listen(mListener, PhoneStateListener.LISTEN_NONE);

		}
	};
}
