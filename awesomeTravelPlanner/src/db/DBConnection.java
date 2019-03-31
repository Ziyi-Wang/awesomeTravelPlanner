package db;

import java.util.List;

import entity.Change;
import entity.Place;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();

	public List<List<Place>> getInitialRecommend(String userID, int nDay);

	public void saveRoute(String userID, List<List<Place>> places);

	public void deleteRoute(String userID);

	public void savePlace(List<Place> places);

	public void updateSchedule(String userID, List<Change> newSchedule);

	public boolean registerUser(String userId, String password, String firstname, String lastname);

	public boolean verifyLogin(String userId, String password);

	public List<List<Place>> generatePath(String userID, List<Place> startPlaces);

	public List<Place> generateDailyPath(String userID, int day, Place startPlace);

	public List<Place> getDailyPlaces(String userID, int day);
}
