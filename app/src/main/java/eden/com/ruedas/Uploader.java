package eden.com.ruedas;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Uploader extends AsyncTask<Geotag, Integer, String> {
	static Activity act;

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(Geotag... params) {
		for (Geotag g : params) {
			String s;
			try{
            	HttpClient httpClient = new DefaultHttpClient();
            	HttpPost httpPost = new HttpPost("http://ruedassinfronteras.herokuapp.com/geotag"); //URL
            	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
            	nameValuePair.add(new BasicNameValuePair("lat", g.lat));
            	nameValuePair.add(new BasicNameValuePair("lon", g.lon));
            	nameValuePair.add(new BasicNameValuePair("usuario", g.usu));
            	nameValuePair.add(new BasicNameValuePair("incapacidad", g.inc));

            	UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8);
            	httpPost.setEntity(ent);
            	HttpResponse responsePOST = httpClient.execute(httpPost);  
            	HttpEntity resEntity = responsePOST.getEntity();   //Traer respuesta
                
            	InputStream in = resEntity.getContent();
            	BufferedReader b = new BufferedReader(new InputStreamReader(in));
                
            	s = b.readLine();
            
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
			
			publishProgress(50);
			
			//sube foto
			try {
	            HttpClient httpClient = new DefaultHttpClient();
	            HttpContext localContext = new BasicHttpContext();
	            HttpPost httpPost = new HttpPost("http://ruedassinfronteras.herokuapp.com/picupload");
	            
	            InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(g.foto), "denuncia.jpg");
	            MultipartEntity multipartEntity = new MultipartEntity();
	            multipartEntity.addPart("upload", inputStreamBody);
	            multipartEntity.addPart("id", new StringBody(s));
	            httpPost.setEntity(multipartEntity);
	            
	            HttpResponse response = httpClient.execute(httpPost,localContext);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	            String sResponse = reader.readLine();
	            return sResponse;
	        } catch (Exception e) {
	            Log.e(e.getClass().getName(), e.getMessage(), e);
	            return null;
	        }
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		new AlertDialog.Builder(act)
			.setTitle("Denuncia Publicada")
			.setMessage("La denuncia se ha publicado con éxito.")
			.setPositiveButton("Volver", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) {} })
			.setIcon(android.R.drawable.ic_dialog_map)
			.show();
	}
	
	@Override
	protected void onCancelled() {
		new AlertDialog.Builder(act)
			.setTitle("Denuncia NO Publicada")
			.setMessage("La denuncia no se ha podido publicar. Intente nuevamente.")
			.setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { } })
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
	}

}
