package io.github.marcosox.fsxsaas.fsx.models;

@SuppressWarnings("unused")
public class NDB {
	private final String id;
	private final String icao;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final int frequency;
	private final float magVar;

	public NDB(String icao, double latitude, double longitude, double altitude, int frequency, float magVar) {
		this.id = "ndb_" + icao;
		this.icao = icao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.frequency = frequency;
		this.magVar = magVar;
	}

	public String getIcao() {
		return icao;
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

	public int getFrequency() {
		return frequency;
	}

	public float getMagVar() {
		return magVar;
	}

	public String getId() {
		return id;
	}
}
