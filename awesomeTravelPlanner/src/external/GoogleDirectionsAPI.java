package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import secrets.Keys;

public class GoogleDirectionsAPI {
	// https://developers.google.com/maps/documentation/directions/intro
	private static final String URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String API_KEY = Keys.GOOGLE_API_KEY;

	public static JSONObject getDirection(String startPlaceID, String endPlaceID) {
		String query = String.format("origin=place_id:%s&destination=place_id:%s&key=%s", startPlaceID, endPlaceID,
				API_KEY);

		String url = URL + "?" + "language=en" + "&" + query;

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				System.out.println("in GetDirection, error status code is " + responseCode);
				return new JSONObject();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();

			return new JSONObject(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	public static Double getDistance(String startPlaceID, String endPlaceID) {
		JSONObject object = getDirection(startPlaceID, endPlaceID);
		try {
			if (!object.isNull("routes")) {
				JSONArray routes = object.getJSONArray("routes");
				for (int i = 0; i < routes.length(); i++) {
					JSONObject obj = routes.getJSONObject(i);
					if (!obj.isNull("legs")) {
						JSONArray legs = obj.getJSONArray("legs");
						for (int j = 0; j < legs.length(); j++) {
							JSONObject o = legs.getJSONObject(j);
							if (!o.isNull("distance")) {
								JSONObject distance = o.getJSONObject("distance");
								int meters = distance.getInt("value");
								double miles = meters * 0.000621371192;
								return (double) Math.round(miles * 10) / 10;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Integer getDuration(String startPlaceID, String endPlaceID) {
		JSONObject object = getDirection(startPlaceID, endPlaceID);
		try {
			if (!object.isNull("routes")) {
				JSONArray routes = object.getJSONArray("routes");
				for (int i = 0; i < routes.length(); i++) {
					JSONObject obj = routes.getJSONObject(i);
					if (!obj.isNull("legs")) {
						JSONArray legs = obj.getJSONArray("legs");
						for (int j = 0; j < legs.length(); j++) {
							JSONObject o = legs.getJSONObject(j);
							if (!o.isNull("duration")) {
								JSONObject duration = o.getJSONObject("duration");
								int second = duration.getInt("value");
								int minutes = second / 60;
								return minutes;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
