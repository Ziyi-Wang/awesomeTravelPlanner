package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class APIUtils {
	public static final String topPlacesFilePath = "E:\\LaiProject\\finalProject\\awesomeTravelPlanner\\src\\external\\topPlaces.txt";

	public static JSONObject queryURL(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				System.out.println("Error Sending request to url: " + url);
				System.out.println("Error code is " + responseCode);
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

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
