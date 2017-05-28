package eden.com.ruedas;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.net.ParseException;
import android.util.Log;

public class Servidor {
	public String lat,  lon, usu,  inc ,respuestaServ="", sResponsefoto="",respues="";
	public byte[] foto;
	Thread t;
	
	Servidor(String lat, String lon, String usu, String inc, byte[] foto){
		this.lat=lat;
		this.lon=lon;
		this.usu=usu;
		this.inc=inc;
		this.foto=foto;
	}		
	
	public String postData(){
		Thread t=new Thread() {
		    public void run() {
		        try{
		            HttpClient httpClient = new DefaultHttpClient();
		            HttpPost httpPost = new HttpPost("http://ruedassinfronteras.herokuapp.com/geotag"); //URL
		            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		            nameValuePair.add(new BasicNameValuePair("lat", lat));
		            nameValuePair.add(new BasicNameValuePair("lon", lon));
		            nameValuePair.add(new BasicNameValuePair("usuario", usu));
		            nameValuePair.add(new BasicNameValuePair("incapacidad", inc));

	                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8);
	                httpPost.setEntity(ent);
	                HttpResponse responsePOST = httpClient.execute(httpPost);  
	                HttpEntity resEntity = responsePOST.getEntity();   //Traer respuesta
		                
	                InputStream in=resEntity.getContent();
	                BufferedReader b=new BufferedReader(new InputStreamReader(in));
		                
	                String s= b.readLine();
	                /*boolean be=false; //Sacar el id
	                for(int i=0;i<s.length();i++)  {  
	                	if(s.charAt(i)==':'){
	                		be=true;
	                	}
	                	else if (be){
	                		if(s.charAt(i)!=',')
	                		ss=ss+s.charAt(i);
	                		else
	                			i=s.length();
	                	}
	                }  */
	                respuestaServ=s;
	                
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		   
		};
		t.start();
		while(t.isAlive()){
		}
		return respuestaServ;
	} 
	public String sendPhoto(final String id){
		Thread t=new Thread() {
		    public void run() {
		    	try {
		    		
		            HttpClient httpClient = new DefaultHttpClient();
		            HttpContext localContext = new BasicHttpContext();
		            HttpPost httpPost = new HttpPost("http://ruedassinfronteras.herokuapp.com/picupload");
		            
		            InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(foto), "denuncia.jpg");
		            MultipartEntity multipartEntity = new MultipartEntity();
		            multipartEntity.addPart("upload", inputStreamBody);
		            multipartEntity.addPart("id", new StringBody(id));
		            httpPost.setEntity(multipartEntity);
		            /*MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		            entity.addPart("files[]",new ByteArrayBody(foto, "myImage.jpg"));*/
		            
		            HttpResponse response = httpClient.execute(httpPost,localContext);
		            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		            sResponsefoto = reader.readLine();
		            
		        } catch (Exception e) {
		            Log.e(e.getClass().getName(), e.getMessage(), e);}
		    }
		};
		t.start();
		while(t.isAlive()){
		}
		 return sResponsefoto;
	} 
}