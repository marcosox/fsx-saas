package io.github.marcosox.fsxsaas.fsx.models;

@SuppressWarnings("unused")
public class Waypoint {
	private final String id;
	private final String icao;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final float magVar;

	public Waypoint(String icao, double latitude, double longitude, double altitude, float magVar) {
		this.id = "waypoint_" + icao;
		this.icao = icao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.magVar = magVar;
	}

	public String getIcao() {
		return icao;
	}

	public String getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public float getMagVar() {
		return magVar;
	}
}
