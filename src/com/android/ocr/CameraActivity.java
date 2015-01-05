package com.android.ocr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
//activity for auto capture of image
public class CameraActivity extends Activity {

	public static final String TAG = "cameraactivity.java";
	private Camera mCamera;
	private CameraPreview mPreview;
	private Button captureButton;
	private boolean takingpicture;
	//store data after taking picture
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			//	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			//	        if (pictureFile == null){
			//	            Log.d(TAG, "Error creating media file, check storage permissions: " +
			//	                e.getMessage());
			//	            return;
			//	        }

			try {
				String _path =SimpleAndroidOCRActivity.DATA_PATH + "/ocr.jpg";
				FileOutputStream fos = new FileOutputStream(_path);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};
	protected TextView textTimeLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameralayout);
		captureButton = (Button) findViewById(R.id.button_capture);
		textTimeLeft = new TextView(this);
		captureButton.setOnClickListener(new ButtonClickHandler() );
		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);

		((FrameLayout) findViewById(R.id.camera_preview)).addView(textTimeLeft, params);
	
		startTimer();
	}



	public  Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
			Log.v(TAG, "exception on getting camera");
		}
		return c; // returns null if camera is unavailable
	}

	public class ButtonClickHandler implements View.OnClickListener {
		@SuppressWarnings("deprecation")
		public void onClick(View view) {

			if(!takingpicture)
			{
				Log.v(TAG, "taking picture");
				takingpicture=true;
				mCamera.takePicture(null, mPicture, mPicture);
				
				afterclick();
			}
			else
			{
				Log.v(TAG, "some one else istaking picture");
			}
		
//			super.onBackPressed();
			
		}		
	}
	void afterclick()
	{
		setResult(Activity.RESULT_OK, getIntent());
		super.onBackPressed();
	}
	//starts the timer
	public void startTimer(){

		// 5000ms=5s at intervals of 1000ms=1s so that means it lasts 5 seconds
		new CountDownTimer(8000,1000){

			@Override
			public void onFinish() {
				// count finished
								textTimeLeft.setText("Picture Taken");
				if(!takingpicture)
				{
					takingpicture=true;
					mCamera.takePicture(null, null, mPicture, mPicture);
					afterclick();
				
					
				}
				
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// every time 1 second passes
									textTimeLeft.setText("Seconds Left: "+millisUntilFinished/1000);
			}

		}.start();
		
		
	}
}
