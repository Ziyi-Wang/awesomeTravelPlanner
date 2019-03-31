package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Change;

/**
 * Servlet implementation class UpdatePaths
 */
@WebServlet("/UpdatePaths")
public class UpdatePaths extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject req = RpcHelper.readJSONObject(request);
		try {
			String userID = req.getString("userID");
			JSONArray changesArray = req.getJSONArray("newSchedule");
			List<Change> newSchedule = RpcHelper.parseChanges(changesArray);

			DBConnection conn = DBConnectionFactory.getConnection();
			conn.updateSchedule(userID, newSchedule);

			JSONObject obj = new JSONObject().put("status", "OK");
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
