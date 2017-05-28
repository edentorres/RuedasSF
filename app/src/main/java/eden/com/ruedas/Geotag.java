package eden.com.ruedas;

public class Geotag {
	public byte[] foto;
	public String lat, lon, usu, inc;
	
	public Geotag (String lat, String lon, String usu, String inc, byte[] foto){
		this.lat=lat;
		this.lon=lon;
		this.usu=usu;
		this.inc=inc;
		this.foto=foto;
	}		

}
