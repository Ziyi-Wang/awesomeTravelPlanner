package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Place;

@WebServlet("/GeneratePaths")
public class GeneratePaths extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userID = request.getParameter("userID");
		DBConnection connection = DBConnectionFactory.getConnection("mysql");
		List<List<Place>> paths = connection.getPlaces(userID);

		JSONArray array = new JSONArray();

		try {
			for (int i = 0; i < paths.size(); i++) {
				List<Place> dailyPlaces = paths.get(i);
				for (int j = 0; j < dailyPlaces.size(); j++) {
					JSONObject obj = dailyPlaces.get(j).toJSONObject();
					obj.put("day", i);
					obj.put("intradayIndex", j);
					array.put(obj);
				}
			}

			JSONObject obj = new JSONObject().put("places", array);
			System.out.println("this is" + obj);
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			JSONObject req = RpcHelper.readJSONObject(request);
			String userID = req.getString("userID");
			DBConnection connection = DBConnectionFactory.getConnection("mysql");

			List<Place> startPlaces = RpcHelper.parseGeneratePath(req);

			connection.savePlace(startPlaces);

			List<List<Place>> paths = connection.generatePath(userID, startPlaces);

			JSONArray array = new JSONArray();
			for (int i = 0; i < paths.size(); i++) {
				List<Place> dailyPlaces = paths.get(i);
				for (int j = 0; j < dailyPlaces.size(); j++) {
					JSONObject obj = dailyPlaces.get(j).toJSONObject();
					obj.put("day", i);
					obj.put("intradayIndex", j);
					array.put(obj);
				}
			}
			JSONObject obj = new JSONObject().put("places", array);
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
