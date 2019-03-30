package rpc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

@WebServlet("/UpdateInterestedPlaces")
public class UpdateInterestedPlaces extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userID = input.getString("userID");
			JSONArray changes = input.getJSONArray("newSchedule");

			Map<String, Integer> newSchedule = RpcHelper.parseSchedule(changes);
			conn.updateSchedule(userID, newSchedule);

			JSONObject obj = new JSONObject();
			obj.put("status", "OK");
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
}
