package eden.com.ruedas;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CamaraActivity extends Activity {
	 static final int CAMERA_REQUEST = 1888; 
	 final static String FOTO= "FOTO"; 
	 Bitmap photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camara);
		Intent i=getIntent();
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, CAMERA_REQUEST);
	}
	private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
            return true;
         else 
            return false;
    }
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
	            photo = (Bitmap) data.getExtras().get("data"); 
	            devolver(photo); 
	        }  
	    } 
	public void devolver(Bitmap photo){
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
			byte[] image = stream.toByteArray();
		Intent returnIntent = new Intent();
		returnIntent.putExtra(FOTO, image);
		setResult(RESULT_OK,returnIntent);
		finish();	
	}
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
	    super.onBackPressed();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camara, menu);
		return true;
	}*/
}
