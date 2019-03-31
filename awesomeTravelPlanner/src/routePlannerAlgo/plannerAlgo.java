package routePlannerAlgo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import external.GoogleDirectionsAPI;

class Res {
	double minDistance;
	List<Integer> bestPath;

	public Res() {
		minDistance = Double.MAX_VALUE;
	}
}

public class plannerAlgo {

	public static List<String> getPath(String startPlaceID, List<String> midPlaces) {
		// graph[i][j] is the traveling distance between place i and place j
		// staring point is index 0 in the graph
		// first place in midPlaces has index 1, second place in midPlaces has index 2,
		// etc
		List<String> places = new ArrayList<>();
		places.add(startPlaceID);
		places.addAll(midPlaces);

		double[][] graph = createDistanceGraph(places);

		List<Integer> currPath = new ArrayList<>();
		currPath.add(0); // add starting point

		HashSet<Integer> visited = new HashSet<>();
		Res res = new Res();
		dfs(graph, currPath, 0, visited, res);

		List<String> resPath = new ArrayList<>();
		for (int i = 1; i < res.bestPath.size(); i++) {
			resPath.add(midPlaces.get(res.bestPath.get(i) - 1));
		}

		return resPath;

	}

	private static void dfs(double[][] graph, List<Integer> currPath, double curDistance, HashSet<Integer> visited,
			Res res) {
		if (currPath.size() == graph.length) {
			// add the distance of last place to starting point
			curDistance += graph[currPath.get(currPath.size() - 1)][0];
			if (curDistance < res.minDistance) {
				res.minDistance = curDistance;
				res.bestPath = new ArrayList<>(currPath);
			}
			return;
		}

		for (int i = 0; i < graph.length; i++) {
			if (!visited.contains(i)) {
				visited.add(i);
				currPath.add(i);

				dfs(graph, currPath, curDistance + graph[currPath.get(currPath.size() - 1)][i], visited, res);
				currPath.remove(currPath.size() - 1);
				visited.remove(i);
			}
		}
	}

	private static double[][] createDistanceGraph(List<String> places) {
		int nNode = places.size();
		double[][] graph = new double[nNode][nNode];
		for (int i = 0; i < nNode; i++) {
			for (int j = 0; j < i; j++) {
				graph[i][j] = GoogleDirectionsAPI.getDistance(places.get(i), places.get(j));
			}
		}

		for (int i = 0; i < nNode; i++) {
			for (int j = i + 1; j < nNode; j++) {
				graph[i][j] = graph[j][i];
			}
		}
		return graph;
	}

}
