package eden.com.ruedas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends Activity {
	private GoogleMap googleMap;
	LocationManager locationManager;
	String res;
	LatLng min;
	LatLng max;
	final LatLngBounds.Builder builder = new LatLngBounds.Builder();
	
	@SuppressLint("UseSparseArrays")
	Map<Long, MarkerInfo> marcadores = new HashMap<Long, MarkerInfo>();

    CameraPosition prevPos = null;
	
	LocationListener loclis= new LocationListener(){
	    public void onLocationChanged(Location location){
	    	ubicacionActual(location);
	    	locationManager.removeUpdates(loclis);
	    }
	    public void onProviderDisabled(String provider){
	    	if(ubicacionEncendida()==false)
	        	ubicacionEncendidaDia();
	    }
	    public void onProviderEnabled(String provider) { }
	    public void onStatusChanged(String provider, int status, Bundle extras){ }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		 googleplayDisp();
		 try {
			 Log.e("APP", "INIT!");
			 initilizeMap();
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            Log.e("APP", "A VER!");
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                Log.e("APP", "NO FUNCIONO!");
            } else {
                googleMap.setMyLocationEnabled(true); //habilitar ubicacaion
                googleMap.getUiSettings().setZoomControlsEnabled(true); // Sacar botones zoom
                googleMap.getUiSettings().setZoomGesturesEnabled(true); // Sacar zoom fun.
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.setOnCameraChangeListener(new OnCameraChangeListener(){
					@Override
					public void onCameraChange(CameraPosition arg0) {
						if (prevPos == null)
							prevPos  = arg0;
						
						if (prevPos.zoom<=arg0.zoom)
						{
							LatLngBounds bounds=googleMap.getProjection().getVisibleRegion().latLngBounds;
							min=bounds.northeast;
							max=bounds.southwest;
							String respuesta= enviar();
							if(respuesta.equalsIgnoreCase("nothing found")==false){
								//Toast.makeText(getApplicationContext(),respuesta, Toast.LENGTH_SHORT).show();
								List<MarkerInfo> elementos = MarkerInfo.todosLosMarcadores(respuesta);
								for(MarkerInfo mi : elementos)
								{
									if (!marcadores.containsKey(mi.getId())) {
										Filter val = puede(mi.getIncapacidad());
										MarkerOptions mo = new MarkerOptions().position(new LatLng(mi.getLat(), mi.getLon()) ).title(val.val);
										if (val.malisimo)
											mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
										else
											mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
										googleMap.addMarker(mo);
										marcadores.put(mi.getId(), mi); 
									}
								}
							}
						}
					}
                });
            }
        }
	}
	
	class Filter {
		String val;
		boolean malisimo;
		Filter(String val, boolean imposible) { this.val = val; this.malisimo = imposible; }
	}
	
	@SuppressLint("DefaultLocale")
	private Filter puede(String inpu) {
		String[] str = inpu.split(";");
		boolean imposible = false; 
		String resImposible = "", resPosible = "";
		for (int i = 0; i < str.length; i+=2) {
			if (str[i+1].equals("-")) {
				if (resImposible.isEmpty()) {
					resImposible += str[i].toUpperCase();
				} else {
					resImposible += ", " + str[i].toUpperCase();
				}
				imposible = true;
			} else if (str[i+1].equals("+")){
				if (resPosible.isEmpty()) {
					resPosible += str[i].toUpperCase();
				} else {
					resPosible += ", " + str[i].toUpperCase();
				}
			}
		}
		Filter f;
		if (imposible)
			f = new Filter(resImposible, imposible);
		else
			f = new Filter(resPosible, imposible);
		return f;
	}
	
	public String enviar(){
		Thread t=new Thread() {
		    public void run() {
		        try{
		            HttpClient httpClient = new DefaultHttpClient();
		            
		            String lamin=String.valueOf(min.latitude);
		            String lamax=String.valueOf(max.latitude);
		            String lomin=String.valueOf(min.longitude);
		            String lomax=String.valueOf(max.longitude);
	                HttpResponse response = httpClient.execute(new HttpGet("http://ruedassinfronteras.herokuapp.com/range?minlat="+lamax+"&maxlat="+lamin+"&minlon="+lomax+"&maxlon="+lomin));
		            //HttpResponse response = httpClient.execute(new HttpGet("http://ruedassinfronteras.herokuapp.com/range?minlat=-34.439822980919374&maxlat=-34.43901114615131&minlon=-58.8468430315952&maxlon=-58.843179134080856"));
		            InputStream content = response.getEntity().getContent();
		                            
	                BufferedReader b=new BufferedReader(new InputStreamReader(content));
		                
	                String s= b.readLine();
	                res=s;	                
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		  
		};
		t.start();
		while(t.isAlive()){
		}
		return res;
	}
	
	private boolean ubicacionEncendida() {
	   boolean gpson= true;
   		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
   		if (locationProviders == null || locationProviders.equals(""))
   			gpson=false;
   		return gpson;
   }
   
   	private void ubicacionEncendidaDia(){

        	new AlertDialog.Builder(this)
            .setTitle("Habilitar Ubicaci�n")
            .setMessage("Esta aplicaci�n necesita usar los servicios de ubicaci�n.")
            .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    // cerrar aplicacion
                	System.exit(0);
                }
             })
            .setNegativeButton("Ir a Ajustes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                	//ir a ajustes
                	//startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    
                }
             })
            .setIcon(android.R.drawable.ic_dialog_alert)
             .show();
            
    }
   
    private void googleplayDisp(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
 
        if(status==ConnectionResult.SUCCESS){}
        	//Log.d("Location Updates- Google PLay","Google Play services is available.");
        else
		{}//Log.d("Location Updates-Google Play", "Google Play services is not available.");
    }
    
    String getProviderName() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
     
        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)
     
        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }
    
    public void testRunFineLocation(){
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	long minTime = 4 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
    	locationManager.requestLocationUpdates(getProviderName(),minTime,0,loclis);
    }
    
    private void ubicacionActual(Location location){        
        if (location != null)
        {
        	LatLng currentloc= new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
            	.target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }   
        else
		{}//Log.d("Location Updates-Google Play", "iiii");
    }
	
    @Override
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
	
	protected void onResume() {
	        super.onResume();
	        
	        if(ubicacionEncendida()==false)
	        	ubicacionEncendidaDia();
	        else{
	            testRunFineLocation();
	        }
	          
	    } 
}
