package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.DBConnection;
import entity.Place;
import entity.Place.PlaceBuilder;
import external.GooglePlaceAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<List<Place>> getInitialRecommend(String userID, int nDay) {
		int nPlaceDaily = 5;
		List<Place> places = GooglePlaceAPI.searchTopKPlaces(nDay * nPlaceDaily);
		List<List<Place>> res = new ArrayList<>();
		for (int i = 0; i < nDay; i++) {
			List<Place> t = new ArrayList<>();
			for (int j = 0; j < nPlaceDaily; j++) {
				t.add(places.get(i * nPlaceDaily + j));
			}
			res.add(t);
		}

		if (userID != null) {
			deleteRoute(userID);
			saveRoute(userID, res);
		}

		return res;
	}

	@Override
	public void saveRoute(String userID, List<List<Place>> places) {
		for (int i = 0; i < places.size(); i++) {
			List<Place> dailyPlace = places.get(i);
			for (int j = 0; j < dailyPlace.size(); j++) {
				Place place = dailyPlace.get(j);
				String sql = "INSERT IGNORE INTO routes VALUES (?, ?, ?, ?)";
				try {
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, userID);
					ps.setString(2, place.getPlaceID());
					ps.setString(3, String.valueOf(i));
					ps.setString(4, String.valueOf(j));
					ps.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void deleteRoute(String userID) {

		String sql = "DELETE FROM routes WHERE user_id = ? ";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userID);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void savePlace(List<Place> places) {
		for (int i = 0; i < places.size(); i++) {
			Place place = places.get(i);
			String sql = "INSERT IGNORE INTO places VALUES (?, ?, ?, ?, ?)";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, place.getPlaceID());
				ps.setString(2, place.getName());
				ps.setString(3, String.valueOf(place.getLat()));
				ps.setString(4, String.valueOf(place.getLon()));
				ps.setString(5, place.getImageURL());
				ps.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void updateSchedule(String userID, Map<String, Integer> newSchedule) {
		for (Map.Entry<String, Integer> entry : newSchedule.entrySet()) {
			try {
				String name = null;
				String sql = "SELECT user_id FROM routes WHERE user_id = ? AND place_id = ?";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, userID);
				statement.setString(2, entry.getKey());
				ResultSet rs = statement.executeQuery();

				while (rs.next()) {
					name = rs.getString("user_id");
				}
				// if the entry doesn't exist, do nothing
				if (name == null) {
					continue;
				}

				if (entry.getValue() != -1) {
					// update
					sql = "UPDATE routes SET day = ? WHERE user_id = ? AND place_id = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, entry.getValue());
					ps.setString(2, userID);
					ps.setString(3, entry.getKey());
					ps.execute();
				} else {
					// delete
					sql = "DELETE FROM routes WHERE user_id = ? AND place_id = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, userID);
					ps.setString(2, entry.getKey());
					ps.execute();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);

			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}

		try {
			String sql = "SELECT * FROM users WHERE user_id = ? AND password = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public List<List<Place>> generatePath(String userID, List<Place> startPlaces) {
		List<List<Place>> res = new ArrayList<List<Place>>();
		for (int i = 0; i < startPlaces.size(); i++) {
			res.add(generateDailyPath(userID, i, startPlaces.get(i)));
		}
		return res;
	}

	@Override
	public List<Place> getDailyPlaces(String userID, int day) {
		try {
			// 1. get placeID for this user on this day
			String sql = "SELECT * FROM routes WHERE user_id = ? AND day = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userID);
			statement.setInt(2, day);
			ResultSet rs = statement.executeQuery();
			List<String> placeIDs = new ArrayList<>();
			while (rs.next()) {
				placeIDs.add(rs.getString("place_id"));
			}
			System.out.println(placeIDs);

			// 2. query place database and get place objects
			List<Place> path = new ArrayList<>();

			sql = "SELECT * FROM places WHERE place_id = ? ";
			statement = conn.prepareStatement(sql);
			for (String placeID : placeIDs) {
				statement.setString(1, placeID);
				rs = statement.executeQuery();
				while (rs.next()) {
					String name = rs.getString("name");
					double lat = rs.getDouble("lat");
					double lon = rs.getDouble("lon");
					String url = rs.getString("imageURL");
					String type = "poi";
					Place p = new PlaceBuilder().setPlaceID(placeID).setName(name).setLat(lat).setLon(lon).setURL(url)
							.setType(type).build();
					path.add(p);
				}
			}
			return path;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	@Override
	public List<Place> generateDailyPath(String userID, int day, Place startPlace) {
		List<Place> middlePlaces = getDailyPlaces(userID, day);
		middlePlaces.add(0, startPlace);
		return middlePlaces;
	}

}
