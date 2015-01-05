package com.android.ocr;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class OcrResult {
	private long timestamp;
	private Paint paint;
	private List<Rect> textlineBoundingBoxes;
	private List<Rect> wordBoundingBoxes;
	private List<Rect> stripBoundingBoxes;
	private List<Rect> regionBoundingBoxes;
	private long recognitionTimeRequired;
	private int meanConfidence;
	private List<Rect> characterBoundingBoxes;
	private String text;
	private int[] wordConfidences;
	private Bitmap bitmap;
	private TessBaseAPI baseApi;
	private String _path;
	private ArrayList<Rect> connectedBoundingBox;
	private int color;

	public OcrResult(TessBaseAPI baseApi) {
		timestamp = System.currentTimeMillis();
		this.paint = new Paint();
		this.baseApi=baseApi;
	}
	public String getText() {
		return text;
	}

	public int[] getWordConfidences() {
		return wordConfidences;
	}

	public int getMeanConfidence() {
		return meanConfidence;
	}

	public long getRecognitionTimeRequired() {
		return recognitionTimeRequired;
	}

	public Point getBitmapDimensions() {
		return new Point(bitmap.getWidth(), bitmap.getHeight()); 
	}

	public List<Rect> getRegionBoundingBoxes() {
		return regionBoundingBoxes;
	}

	public List<Rect> getTextlineBoundingBoxes() {
		return textlineBoundingBoxes;
	}

	public List<Rect> getWordBoundingBoxes() {
		return wordBoundingBoxes;
	}

	public List<Rect> getStripBoundingBoxes() {
		return stripBoundingBoxes;
	}

	public List<Rect> getCharacterBoundingBoxes() {
		return characterBoundingBoxes;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setWordConfidences(int[] wordConfidences) {
		this.wordConfidences = wordConfidences;
	}

	public void setMeanConfidence(int meanConfidence) {
		this.meanConfidence = meanConfidence;
	}

	public void setRecognitionTimeRequired(long recognitionTimeRequired) {
		this.recognitionTimeRequired = recognitionTimeRequired;
	}

	public void setRegionBoundingBoxes(List<Rect> regionBoundingBoxes) {
		this.regionBoundingBoxes = regionBoundingBoxes;
	}

	public void setTextlineBoundingBoxes(List<Rect> textlineBoundingBoxes) {
		this.textlineBoundingBoxes = textlineBoundingBoxes;
	}

	public void setWordBoundingBoxes(List<Rect> wordBoundingBoxes) {
		this.wordBoundingBoxes = wordBoundingBoxes;
	}

	public void setStripBoundingBoxes(List<Rect> stripBoundingBoxes) {
		this.stripBoundingBoxes = stripBoundingBoxes;
	}

	public void setCharacterBoundingBoxes(List<Rect> characterBoundingBoxes) {
		this.characterBoundingBoxes = characterBoundingBoxes;
	}
	public Bitmap cutbitmap(Bitmap bm2,Rect r)
	{
		Bitmap bmOverlay = Bitmap.createBitmap(bm2,r.centerX()-r.width()/2,r.centerY()-r.height()/2, r.width(), r.height());


		return bmOverlay;
	}
	public void setConnectedBoundingBox(ArrayList<Rect> boxRects) {

		this.connectedBoundingBox=boxRects;

	}
	public void setcolor(int color) {
		this.color=color;
	}
	@Override
	public String toString() {
		return text + " " + meanConfidence + " " + recognitionTimeRequired + " " + timestamp;
	}
//draw rectangle around rectangle around each word	
	private Bitmap getAnnotatedBitmap() {
		Canvas canvas = new Canvas(bitmap);

		// Draw bounding boxes around each word
		for (int i = 0; i < wordBoundingBoxes.size(); i++) {
			paint.setAlpha(0xFF);
			paint.setColor(0xFF00CCFF);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			Rect r = wordBoundingBoxes.get(i);
			canvas.drawRect(r, paint);
		} 

		return bitmap;
	}
	Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public Bitmap getfinalBitmap(Bitmap b)
	{
//create one bitmap with background color of original bitmap
		Paint paint = new Paint();
		if(color==-1)
		{
			paint.setColor(b.getPixel(b.getWidth()/2, b.getHeight()/2));
		}
		else
		{
			paint.setColor(color);
		}
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(10);
		Bitmap bitmap = Bitmap.createBitmap(b.getWidth(),b.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawPaint(paint); 

		paint = new Paint(); 
		paint.setStrokeWidth(10);
		
		//read the text strip wise and add to bitmap
		for(int i=0;i< stripBoundingBoxes.size();i++)
		{

			Rect r = stripBoundingBoxes.get(i);
			baseApi.setRectangle(r);

			paint.setColor(Color.WHITE); 
			paint.setTextSize(40); 
			String text= baseApi.getUTF8Text();
			text = text.replaceAll("[^a-zA-Z0-9]+", " ");
			text=text.trim();
			canvas.drawText(text, r.left,r.centerY(), paint);
			Log.v(this.toString(), "text drawn="+text);
		}
		return bitmap;
	}
	//	public Bitmap getconnectedBitmap() {
	//	}

	public Bitmap getTextBitmap() {
		Canvas canvas = new Canvas(bitmap);

		// Draw bounding boxes around each word
		for (int i = 0; i < textlineBoundingBoxes.size(); i++) {
			paint.setAlpha(0xFF);
			paint.setColor(0xFF00CCFF);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			Rect r = textlineBoundingBoxes.get(i);
			canvas.drawRect(r, paint);
		} 
		return bitmap;
	}


}
