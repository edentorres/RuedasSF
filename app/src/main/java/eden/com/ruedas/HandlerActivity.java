package eden.com.ruedas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HandlerActivity extends Activity {
	byte[] image;
	public int numero=1;
	public String lat="", lon="",serial="", dis="",res="",id;
	static final int CAMARA = 11;
	static final int INFO=22;
	static final int SERVIDOR =33;
	static final String FOTO= "FOTO";
	final static String UPLOAD = "UPLOAD";
	Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handler);
		i = getIntent();
		decidir();
	}
	
	public void decidir(){
		if (numero==1) { //Ir a camara
			numero++;
			String s = i.getExtras().getString(MainActivity.SERIAL_NUM) + " " + i.getExtras().getString(MainActivity.GEO_LOC_LAT) + " " + i.getExtras().getString(MainActivity.GEO_LOC_LON);
			serial=i.getExtras().getString(MainActivity.SERIAL_NUM);
			lat=i.getExtras().getString(MainActivity.GEO_LOC_LAT);
			lon=i.getExtras().getString(MainActivity.GEO_LOC_LON);
			((TextView)findViewById(R.id.serialNum)).setText(s);
			
			Intent intent = new Intent(getApplicationContext(), CamaraActivity.class);
			startActivityForResult(intent, CAMARA);
		} else if (numero==2) {//Ir a info
			Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
			intent.putExtra(FOTO, image);
			startActivityForResult(intent, INFO);
		} else if (numero==3) { //Enviar al serv y volver
			/*Servidor serv= new Servidor(lat,lon,serial,dis, image);
			
			id = serv.postData();
			String foto = serv.sendPhoto(id);*/
			((TextView)findViewById(R.id.serialNum)).setText(" --- " + id);
			
			/*int up;
			if (foto.equalsIgnoreCase("uploaded")) 
				up=0; //si se subio o no
			else 
				up=1;*/
			
			//Intent returnIntent = new Intent();
			new Uploader().execute(new Geotag(lat, lon, serial, dis, image));
			//returnIntent.putExtra(UPLOAD, );
			//setResult(RESULT_OK, returnIntent);
			finish();
		}
		else if(numero==4){
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMARA) {
	         {
	        	numero=2;
	        	image= data.getExtras().getByteArray(CamaraActivity.FOTO);
	        	decidir();
	        }
	    }
	    if (requestCode == INFO) {
	        if (resultCode == RESULT_OK) {
	        	numero=3;
	        	dis=data.getExtras().getString(InfoActivity.TIPO);
	        	decidir();
	        }
			if (resultCode == RESULT_CANCELED) {
				numero=4;
				//dis=data.getExtras().getString(InfoActivity.TIPO);
				decidir();
			}
	    }
	}
}
