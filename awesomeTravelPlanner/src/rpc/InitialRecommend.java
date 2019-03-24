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

/**
 * Servlet implementation class initial_recommend
 */
@WebServlet("/initial_recommend")
public class InitialRecommend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int day = 1;
		String userID = "testUser";
		DBConnection connection = DBConnectionFactory.getConnection("mysql");
		try {
			List<List<Place>> places = connection.getInitialRecommend(userID, day);
			JSONArray array = new JSONArray();
			for (int i = 0; i < places.size(); i++) {
				List<Place> dailyPlaces = places.get(i);
				for (int j = 0; j < dailyPlaces.size(); j++) {
					JSONObject obj = dailyPlaces.get(j).toJSONObject();
					obj.put("day", i);
					obj.put("intradayIndex", -1);
					array.put(obj);
				}
			}
			RpcHelper.writeJsonArray(response, array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}
}
