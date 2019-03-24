package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Place;
import entity.Place.PlaceBuilder;
import secrets.Keys;

public class GoogleAPI {
	// correct URL
	// https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyC7icI6IG_bNd1Km1iKkFzn2exDMb17BuU
	public static Place searchPlace(String name) {
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
			// Can be safely ignored because UTF-8 is always supported
		}

		String baseURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
		String url = baseURL + "input=" + name;
		url += "&inputtype=textquery&fields=place_id,photos,formatted_address,name,rating,opening_hours,geometry&key=";
		url += Keys.GOOGLE_API_KEY;

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				return null;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject obj = new JSONObject(response.toString());

			System.out.println(response.toString());

			if (!obj.isNull("candidates")) {
				return getPlaceList(obj.getJSONArray("candidates")).get(0);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Place> searchTopKPlaces(int k) {
		String[] names = { "time square", "museum of modern art", "world trade center", "NYU", "Columbus Circle" };
		List<Place> places = new ArrayList<>();
		for (String name : names) {
			places.add(searchPlace(name));
		}
		return places;
	}

	private static List<Place> getPlaceList(JSONArray places) throws JSONException {
		List<Place> placeList = new ArrayList<>();
		for (int i = 0; i < places.length(); i++) {

			JSONObject place = places.getJSONObject(i);
			PlaceBuilder builder = new PlaceBuilder();

//			if (!place.isNull("formatted_address")) {
//				builder.setAddr(place.getString("formatted_address"));
//			}
			if (!place.isNull("name")) {
				builder.setName(place.getString("name"));
			}

			if (!place.isNull("place_id")) {
				builder.setPlaceID(place.getString("place_id"));
			}

			if (!place.isNull("geometry")) {
				JSONObject geo = place.getJSONObject("geometry");
				if (!geo.isNull("location")) {
					JSONObject loc = geo.getJSONObject("location");
					if (!loc.isNull("lat")) {
						builder.setLat(loc.getDouble("lat"));
					}
					if (!loc.isNull("lng")) {
						builder.setLon(loc.getDouble("lng"));
					}
				}
			}

			builder.setType("poi");
			placeList.add(builder.build());
		}

		System.out.println("finished placeList");
		for (Place p : placeList) {
			p.show();
		}
		return placeList;
	}
}
