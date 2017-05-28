package eden.com.ruedas;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    final static String SERIAL_NUM = "SERIAL_NUM";
    final static String GEO_LOC_LAT = "GEO_LOC_LAT";
    final static String GEO_LOC_LON = "GEO_LOC_LON";
    static final int HANDLER = 1111;
    LatLng currentloc;
    Toolbar toolbar;

    LocationListener loclis = new LocationListener() {
        public void onLocationChanged(Location location) {
            ubicacionActual(location);
        }

        public void onProviderDisabled(String provider) {
            if (ubicacionEncendida() == false)
                ubicacionEncendidaDia();
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private GoogleMap googleMap;
    int num = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.getActivity().findViewById(R.id.listIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapaActivity.class);
                startActivity(intent);
            }
        });
        Uploader.act = this;

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btnReportar);
        button.setSize(FloatingActionButton.SIZE_NORMAL);
        button.setColorNormalResId(R.color.accent);
        button.setIcon(R.drawable.ic_add_white);
        //button.setStrokeVisible(false);
        button = (FloatingActionButton) findViewById(R.id.btnLocation);
        button.setSize(FloatingActionButton.SIZE_NORMAL);
        button.setColorNormal(Color.WHITE);
        button.setIcon(R.drawable.ic_gps_fixed_black_24dp);
        button.setStrokeVisible(false);

        googleplayDisp();

        findViewById(R.id.btnReportar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = "123";
                //tMgr.getSimSerialNumber(); // no anda en tablets, sacar para que ande
                if (mPhoneNumber.length() <= 0) {
                    //mPhoneNumber = "1234"; //borrar
                    Toast t = Toast.makeText(getApplicationContext(), "Debe tener un numero de telefono", Toast.LENGTH_LONG);
                    t.show(); //Volver a poner
                } else
                {
                    if (currentloc != null) {
                        String La = String.valueOf(currentloc.latitude);
                        String Lon = String.valueOf(currentloc.longitude);
                        pasarInfo((String) mPhoneNumber, La, Lon);
                    } else {
                        Location loc = googleMap.getMyLocation();
                        if (loc == null) {
                            Toast t = Toast.makeText(getApplicationContext(), "Error con la ubicación", Toast.LENGTH_LONG);
                            t.show();
                            pasarInfo(mPhoneNumber, "18765", "2"); //borrar
                        } else {
                            String La = String.valueOf(loc.getLatitude());
                            String Lon = String.valueOf(loc.getLongitude());
                            pasarInfo(mPhoneNumber, La, Lon);
                        }
                    }
                }
            }
        });
        findViewById(R.id.btnVerMapa).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapaActivity.class);
                startActivity(intent);
            }
        });

        try {
            Log.e("APP", "Inicializando mapa!");
            initilizarMapa();// Loading map
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.btnLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = googleMap.getMyLocation();
                ubicacionActual(location);
                /*if (location != null) {

                    LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition position = googleMap.getCameraPosition();

                    CameraPosition.Builder builder = new CameraPosition.Builder();
                    builder.zoom(15);
                    builder.target(target);

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

                }*/
            }
        });
    }

    private void pasarInfo(String mPhoneNumber, String La, String Lon) {
        Intent intent = new Intent(getApplicationContext(), HandlerActivity.class);
        intent.putExtra(SERIAL_NUM, mPhoneNumber);
        intent.putExtra(GEO_LOC_LAT, La);
        intent.putExtra(GEO_LOC_LON, Lon);
        startActivity(intent);
    }

    private void initilizarMapa() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setPadding(0, 1420, 15, 0);
            Log.e("APP", "PROBAR SI MAPA ANDA!");
            if (googleMap == null) { // check if map is created successfully or not
                Toast.makeText(getApplicationContext(), "No se pudo crear el mapa", Toast.LENGTH_SHORT).show();
                Log.e("APP", "NO FUNCIONO CREAR MAPA!");
            } else {
                googleMap.setMyLocationEnabled(true); //habilitar ubicacaion
                googleMap.getUiSettings().setZoomControlsEnabled(false); // Sacar botones zoom
                //googleMap.getUiSettings().setZoomGesturesEnabled(false); // Sacar zoom fun.
                //googleMap.getUiSettings().setScrollGesturesEnabled(false); //Sacar mover
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private boolean ubicacionEncendida() {
        boolean gpson = true;
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            gpson = false;
        }
        return gpson;
    }

    private void ubicacionEncendidaDia() {
        new AlertDialog.Builder(this)
                .setTitle("Habilitar Ubicación")
                .setMessage("Esta aplicación necesita usar los servicios de ubicación.")
                .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);// cerrar aplicacion
                    }
                })
                .setNegativeButton("Ir a Ajustes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS); //Ir a ajustes
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void googleplayDisp() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext()); //Traer status

        if (status == ConnectionResult.SUCCESS)
            Log.d("APP- Google PLay", "Google Play DISPONIBLE.");
        else
            Log.d("APP-Google Play", "Google Play NO DISPONIBLE.");

    }

    public String getProviderName() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        return locationManager.getBestProvider(criteria, true);
    }

    public void testRunFineLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        long minTime = 4 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
        locationManager.requestLocationUpdates(getProviderName(), minTime, 0, loclis);
    }

    private void ubicacionActual(Location location) {
        if (location != null) {
            currentloc = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(16)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                            //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Log.d("APP-LOCATION", "LOCATION NULL");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ubicacionEncendida() == false)
            ubicacionEncendidaDia();
        else
            testRunFineLocation();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HANDLER) {
            int respuesta = data.getExtras().getInt(HandlerActivity.UPLOAD); //traigo si se publico o no
            if (respuesta == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Denuncia Publicada")
                        .setMessage("La denuncia se ha publicado con éxito.")
                        .setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Denuncia NO Publicada")
                        .setMessage("La denuncia no se ha podido publicar. Intente nuevamente.")
                        .setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}

