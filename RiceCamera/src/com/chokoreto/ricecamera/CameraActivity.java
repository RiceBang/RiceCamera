package com.chokoreto.ricecamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements View.OnClickListener {

	Button shutter;
	ImageView pic;
	Intent i;
	int CAPTURE_CODE = 1337;
	Bitmap bmp;

	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.main);

		InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
		bmp = BitmapFactory.decodeStream(is);
		shutter = (Button) findViewById(R.id.shutter);
		pic = (ImageView) findViewById(R.id.image);

		shutter.setOnClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.shutter:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			File tmpImg = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpImg));
			startActivityForResult(i, CAPTURE_CODE);
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK & requestCode == CAPTURE_CODE) {
			File tempImg = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
		    Bitmap bitmap = bitmapFromFile(tempImg.getAbsolutePath());
			int bmpWidth = bitmap.getWidth();
			Log.w("com.chokoreto.ricecamera", Integer.toString(bmpWidth));
			int bmpHeight = bitmap.getHeight();
			Log.w("com.chokoreto.ricecamera", Integer.toString(bmpHeight));
			int trgtWidth = (int) (bmpWidth * 0.4f);
			int trgtHeight = (int) (bmpHeight * 0.4f);
			Bitmap trgtBmp = Bitmap.createScaledBitmap(bitmap, trgtWidth,
					trgtHeight, true);
			Calendar c = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyyMMdd_HHmmss");
			String dateTime = dateFormat.format(c.getTime());
			String filename = dateTime + ".png";
			try {
				String path = Environment.getExternalStorageDirectory()
						.toString();
				OutputStream fOut = null;
				File folder = new File(path, "/Pictures/RiceCamera");
				if (!folder.exists()) {
					folder.mkdir();
				}
				File file = new File(path + "/Pictures/RiceCamera/", filename);
				fOut = new FileOutputStream(file);
				trgtBmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
				fOut.flush();
				fOut.close();
				Intent iMediaScanner = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
			    sendBroadcast(iMediaScanner);
				Toast.makeText(getApplicationContext(), "Image saved",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public static Bitmap bitmapFromFile(String path)
	{ 
		
	    final BitmapFactory.Options bfOptions = new BitmapFactory.Options();
	    bfOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, bfOptions);

	    bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
	 
	    bfOptions.inSampleSize = 1;
	 
	   
	    bfOptions.inJustDecodeBounds = false;
	 
	    return BitmapFactory.decodeFile(path, bfOptions);
	}

}