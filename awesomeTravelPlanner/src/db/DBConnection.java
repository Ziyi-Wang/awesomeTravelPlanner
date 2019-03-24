package db;

import java.util.List;
import java.util.Map;

import entity.Place;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();

	public List<List<Place>> getInitialRecommend(String userID, int nDay);

	public void saveRoute(String userID, List<List<Place>> places);

	public void updateSchedule(String userID, Map<String, Integer> newSchedule);
}
