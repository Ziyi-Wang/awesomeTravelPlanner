package entity;

public class Change {

	private String placeID;
	private int day;
	private int intradayIndex;

	public Change(String placeID, int day, int intradayIndex) {
		this.placeID = placeID;
		this.day = day;
		this.intradayIndex = intradayIndex;
	}

	public String getPlaceID() {
		return placeID;
	}

	public int getDay() {
		return day;
	}

	public int getIntradayIndex() {
		return intradayIndex;
	}

}
