package external;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class GooglePlaceAPI {
	// correct URL
	// https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=API_KEY
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
				System.out.println("error in searchPlace, name = " + name);
				System.out.println("error code is " + responseCode);
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

			if (!obj.isNull("candidates")) {
				return getPlaceList(obj.getJSONArray("candidates")).get(0);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Place searchUsingPlaceID(String placeID) {
		// refer
		// https://developers.google.com/places/web-service/details#PlaceDetailsRequests
		String baseURL = "https://maps.googleapis.com/maps/api/place/details/json?";
		String URL = baseURL + "key=" + Keys.GOOGLE_API_KEY + "&" + "placeid=" + placeID;
		URL += "&fields=place_id,photos,name,rating,geometry";

		JSONObject response = APIUtils.queryURL(URL);
		try {
			JSONObject o = response.getJSONObject("result");

			JSONObject location = o.getJSONObject("geometry").getJSONObject("location");
			double lat = location.getDouble("lat");
			double lon = location.getDouble("lng");

			String name = o.getString("name");
			placeID = o.getString("place_id");
			return new PlaceBuilder().setPlaceID(placeID).setName(name).setLat(lat).setLon(lon).build();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Place> searchTopKPlaces(int k) {
		// String[] names = { "time square", "museum of modern art", "world trade
		// center", "NYU", "Columbus Circle" };
		List<String> names = GooglePlaceAPI.loadPlaceNames();
		List<Place> places = new ArrayList<>();
		for (int i = 0; i < k; i++) {
			places.add(searchPlace(names.get(i)));
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

		return placeList;
	}

	private static List<String> loadPlaceNames() {
		List<String> names = new ArrayList<>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(APIUtils.topPlacesFilePath));
			String line = reader.readLine();
			while (line != null) {
				names.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return names;
	}

	public static void main(String[] args) {
		searchUsingPlaceID("ChIJhRwB-yFawokR5Phil-QQ3zM").show();
	}
}
