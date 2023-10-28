package io.github.marcosox.fsxsaas.fsx.models;

import flightsim.simconnect.data.LatLonAlt;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Boat implements GeoJSONSerializable {
	private final int id;
	private final String title;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final double altAgl;
	private final double airspeed;
	private final double groundSpeed;
	private final double bank;
	private final double heading;
	private final double rudder;
	private final double throttle;

	public Boat(int id, HashMap<String, Object> map) {
		this.id = id;
		this.title = (String) map.getOrDefault("TITLE", "");
		this.latitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).latitude;
		this.longitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).longitude;
		this.altitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).altitude;
		this.airspeed = (double) map.getOrDefault("AIRSPEED TRUE", 0.0);
		this.groundSpeed = (double) map.getOrDefault("GROUND VELOCITY", 0.0);
		this.altAgl = (double) map.getOrDefault("PLANE ALT ABOVE GROUND", 0.0);
		this.bank = (double) map.getOrDefault("PLANE BANK DEGREES", 0.0);
		this.heading = (double) map.getOrDefault("PLANE HEADING DEGREES TRUE", 0.0);
		this.rudder = (double) map.getOrDefault("RUDDER POSITION", 0.0);
		this.throttle = (double) map.getOrDefault("GENERAL ENG THROTTLE LEVER POSITION:1", 0.0);
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
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

	public double getAltAgl() {
		return altAgl;
	}

	public double getAirspeed() {
		return airspeed;
	}

	public double getGroundSpeed() {
		return groundSpeed;
	}

	public double getBank() {
		return bank;
	}

	public double getHeading() {
		return heading;
	}

	public double getRudder() {
		return rudder;
	}

	public double getThrottle() {
		return throttle;
	}
}
