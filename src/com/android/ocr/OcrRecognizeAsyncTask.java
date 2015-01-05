package com.android.ocr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
//run in background the ocr engine
final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private SimpleAndroidOCRActivity activity;
	private TessBaseAPI baseApi;
	private long timeRequired;
	private OcrResult ocrResult;
	OcrRecognizeAsyncTask(SimpleAndroidOCRActivity activity, TessBaseAPI baseApi) {
		this.activity = activity;
		this.baseApi = baseApi;

	}
//set image to the OCR Engine and start processing	
	@Override
	protected Boolean doInBackground(Void... params) {
		long start = System.currentTimeMillis();
		Bitmap bitmap=activity.getBitmap();
		String textResult = "";

		try {     
			baseApi.setDebug(true);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
			Boolean initialzed=	baseApi.init(SimpleAndroidOCRActivity.DATA_PATH, "eng",TessBaseAPI.OEM_TESSERACT_ONLY);

			Log.v(this.toString(),"tessrect initialized="+initialzed);
			baseApi.setImage(bitmap);

			String text = baseApi.getUTF8Text();
			if(null!=text)
			{
				textResult=text;
			}
			textResult = textResult.replaceAll("[^a-zA-Z0-9]+", " ");

			textResult = textResult.trim();

			timeRequired = System.currentTimeMillis() - start;
			Log.v(this.toString(), textResult);
			if (textResult == null) {
				return false;
			}
			ocrResult = new OcrResult(baseApi);
			ocrResult.setcolor(activity.getColor());
			ocrResult.setWordConfidences(baseApi.wordConfidences());
			ocrResult.setMeanConfidence( baseApi.meanConfidence());
			ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
			ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
			ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
			ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
			ocrResult.setConnectedBoundingBox(baseApi.getConnectedComponents().getBoxRects());


		} catch (RuntimeException e) {
			Log.v(this.toString(), textResult);
			Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
			e.printStackTrace();
			try {
				baseApi.clear();
			} catch (NullPointerException e1) {

			}
			return false;
		}
		timeRequired = System.currentTimeMillis() - start;
		ocrResult.setBitmap(bitmap);
		ocrResult.setText(textResult);
		ocrResult.setRecognitionTimeRequired(timeRequired);

		return true;
	}
	//gets the result and set to imageview
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		activity._image.setImageBitmap(ocrResult.getfinalBitmap(ocrResult.getBitmap()));
		Log.v(this.toString(), "image set");
		if (baseApi != null) {
			baseApi.clear();
			Log.v(this.toString(), "api cleared");
		}
		activity.getProgressDialog().dismiss();
		Log.v(this.toString(), "dialog dismissed");
	}


}
