package eden.com.ruedas;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

class MarkerInfo {
	private double lat, lon;
	private String incapacidad;
	private int importancia;
	private long id;

	public MarkerInfo(int impo, double lat, double lon, String incapacidad, long id) {
		this.incapacidad = incapacidad;
		this.importancia = impo;
		this.lat = lat;
		this.lon = lon;
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public int getImportancia () {
		return importancia;
	}

	public String getIncapacidad () {
		return incapacidad;
	}

	public double getLat () {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public static List<MarkerInfo> todosLosMarcadores (String json) {
		JSONTokener jt = new JSONTokener(json);

		JSONArray todo = null;
		try {
			todo = (JSONArray) jt.nextValue();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		int i = 0;
		List<MarkerInfo> vals = new ArrayList<MarkerInfo>();
		while(i < todo.length()) {
			try {
				vals.add(toMI((JSONObject)todo.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			i++;
		}
		return vals;
	}

	private static MarkerInfo toMI (JSONObject jo) throws JSONException {
		return new MarkerInfo(jo.getInt("importancia"), jo.getDouble("lat"), jo.getDouble("lon"), jo.getString("incapacidad"), jo.getLong("id"));
	}

}
