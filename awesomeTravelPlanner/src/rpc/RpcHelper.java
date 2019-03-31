package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Change;
import entity.Place;
import entity.Place.PlaceBuilder;

public class RpcHelper {

	// Parses a JSONObject from http request.
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sBuilder = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sBuilder.append(line);
			}
			return new JSONObject(sBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}

	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

	public static Map<String, Integer> parseSchedule(JSONArray changes) throws JSONException {
		Map<String, Integer> changeMap = new HashMap<>();
		for (int i = 0; i < changes.length(); i++) {
			JSONObject t = (JSONObject) changes.get(i);
			changeMap.put(t.getString("placeID"), t.getInt("day"));
		}
		return changeMap;
	}

	public static List<Change> parseChanges(JSONArray changes) throws JSONException {
		List<Change> res = new ArrayList<>();
		for (int i = 0; i < changes.length(); i++) {
			JSONObject obj = changes.getJSONObject(i);
			res.add(new Change(obj.getString("placeID"), obj.getInt("day"), obj.getInt("intradayIndex")));
		}
		return res;
	}

	public static List<Place> parseGeneratePath(JSONObject req) throws JSONException {
		List<Place> startPlaces = new ArrayList<>();
		JSONArray array = req.getJSONArray("startPlaces");

		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			PlaceBuilder builder = new PlaceBuilder();
			Place p = builder.setName(obj.getString("name")).setLat(obj.getDouble("lat")).setLon(obj.getDouble("lon"))
					.setPlaceID(obj.getString("placeID")).setType(obj.getString("type"))
					.setURL(obj.getString("imageURL")).build();

			startPlaces.add(p);
		}
		return startPlaces;
	}
}
