package entity;

import org.json.JSONObject;

public class Place {
	private String type;
	private String name;
	private String placeID;
	private double lat;
	private double lon;
	private String imageURL;

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getPlaceID() {
		return placeID;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getImageURL() {
		return imageURL;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("type", type);
			obj.put("name", name);
			obj.put("placeID", placeID);
			obj.put("lat", lat);
			obj.put("lon", lon);
			obj.put("imageURL", imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public void show() {
		System.out.println(type);
		System.out.println(name);
		System.out.println(lat);
		System.out.println(lon);
		System.out.println(imageURL);
	}

	private Place(PlaceBuilder builder) {
		this.type = builder.type;
		this.name = builder.name;
		this.lat = builder.lat;
		this.lon = builder.lon;
		this.imageURL = builder.imageURL;
		this.placeID = builder.placeID;
	}

	public static class PlaceBuilder {
		private String placeID;
		private String type;
		private String name;
		private double lat;
		private double lon;
		private String imageURL;

		public Place build() {
			return new Place(this);
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public void setURL(String URL) {
			this.imageURL = URL;
		}

		public void setPlaceID(String placeID) {
			this.placeID = placeID;
		}
	}
}
