package io.github.marcosox.fsxsaas.fsx.models;

@SuppressWarnings("unused")
public class VOR {
	private final String id;
	private final String icao;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final int frequency;
	private final float localizer;
	private final float glideSlopeAngle;
	private final double glideLat;
	private final double glideLon;
	private final double glideAlt;
	private final int flags;

	public VOR(String icao, double latitude, double longitude, double altitude, int frequency, float localizer, float glideSlopeAngle, double glideLat, double glideLon, double glideAlt, int flags) {
		this.id = "vor_" + icao;
		this.icao = icao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.frequency = frequency;
		this.localizer = localizer;
		this.glideSlopeAngle = glideSlopeAngle;
		this.glideLat = glideLat;
		this.glideLon = glideLon;
		this.glideAlt = glideAlt;
		this.flags = flags;
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

	public float getLocalizer() {
		return localizer;
	}

	public float getGlideSlopeAngle() {
		return glideSlopeAngle;
	}

	public double getGlideLat() {
		return glideLat;
	}

	public double getGlideLon() {
		return glideLon;
	}

	public double getGlideAlt() {
		return glideAlt;
	}

	public int getFlags() {
		return flags;
	}

	public String getId() {
		return id;
	}
}
