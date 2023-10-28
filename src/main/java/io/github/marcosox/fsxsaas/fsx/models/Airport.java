package io.github.marcosox.fsxsaas.fsx.models;

@SuppressWarnings("unused")
public class Airport {
	private final String id;
	private final String icao;
	private final double latitude;
	private final double longitude;
	private final double altitude;

	public Airport(String icao, double latitude, double longitude, double altitude) {
		this.id = "airport_" + icao;
		this.icao = icao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public String getIcao() {
		return icao;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getId() {
		return id;
	}
}
