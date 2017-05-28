package eden.com.ruedas;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mrengineer13.snackbar.SnackBar;


public class InfoActivity extends AppCompatActivity {
    final static String TIPO = "TIPO";
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Desea volver atás?")
                .setMessage("Los cambios no se guardarán.")
                .setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(TIPO, 0);
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_menu_revert)
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btndenunciar);
        button.setSize(FloatingActionButton.SIZE_NORMAL);
        button.setColorNormalResId(R.color.accent);
        button.setIcon(R.drawable.ic_done_white_24dp);

        List<String> list = new ArrayList<String>(); //Info Spinner 1
        list.add("Acondroplastia");
        list.add("Auditiva");
        list.add("Intelectual");
        list.add("Motora");
        list.add("Visceral");
        list.add("Visual");

		/*List<String> list2=new ArrayList<String>(); //Info Spinner 2
		list2.add("No puede pasar");
		list2.add("Puede pasar con dificultad");*/

        Spinner stipo = (Spinner) findViewById(R.id.sptipo);
        //Spinner sproblema= (Spinner) findViewById(R.id.spproblema);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stipo.setAdapter(dataAdapter);
		/*ArrayAdapter<String> dataAdapter2= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list2);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sproblema.setAdapter(dataAdapter2);*/



        Intent i = getIntent();
        ImageView imagev = (ImageView) findViewById(R.id.imagen);
        byte[] array = i.getByteArrayExtra(HandlerActivity.FOTO);
        Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
        imagev.setImageBitmap(bitmap);

        findViewById(R.id.btndenunciar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner s = (Spinner) findViewById(R.id.sptipo);
                //Spinner s2= (Spinner) findViewById(R.id.spproblema);
                Intent returnIntent = new Intent();
                String info = String.valueOf(s.getSelectedItem().toString().toLowerCase());
                Switch sw = (Switch) findViewById(R.id.swpasar);
                //String pasar = String.valueOf(s2.getSelectedItem().toString());
                if (!sw.isChecked())
                    info += ";-";
                else
                    info += ";+";
                returnIntent.putExtra(TIPO, info);
                setResult(RESULT_OK, returnIntent);

                Toast t = Toast.makeText(getApplicationContext(), "Subiendo denuncia...", Toast.LENGTH_LONG);

               /* Snackbar.make(findViewById(android.R.id.content), "Had a snack at Snackbar", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show();*/
                t.show();
                finish();
            }
        });

    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
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
	}*/
}
