package io.github.marcosox.fsxsaas.fsx.models;

import flightsim.simconnect.data.LatLonAlt;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Vehicle implements GeoJSONSerializable {
	private final int id;
	private final String title;
	private final String atcState;
	private final double latitude;
	private final double longitude;
	private final double groundSpeed;
	private final double heading;
	private final double rudder;
	private final double throttle;
	private final double windSpeed;
	private final double windDirection;
	private final double visibility;
	private final double ambientTemperature;
	private final double ambientPressure;
	private final double barometerPressure;

	public Vehicle(int id, HashMap<String, Object> map) {
		this.id = id;
		this.title = (String) map.getOrDefault("TITLE", "");
		this.atcState = (String) map.getOrDefault("AI TRAFFIC STATE", "");
		this.latitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).latitude;
		this.longitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).longitude;
		this.groundSpeed = (double) map.getOrDefault("GROUND VELOCITY", 0.0);
		this.heading = (double) map.getOrDefault("PLANE HEADING DEGREES TRUE", 0.0);
		this.rudder = (double) map.getOrDefault("RUDDER POSITION", 0.0);
		this.throttle = (double) map.getOrDefault("GENERAL ENG THROTTLE LEVER POSITION:1", 0.0);
		this.windSpeed = (double) map.getOrDefault("AMBIENT WIND VELOCITY", 0.0);
		this.windDirection = (double) map.getOrDefault("AMBIENT WIND DIRECTION", 0.0);
		this.ambientTemperature = (double) map.getOrDefault("AMBIENT TEMPERATURE", 0.0);
		this.ambientPressure = (double) map.getOrDefault("AMBIENT PRESSURE", 0.0);
		this.barometerPressure = (double) map.getOrDefault("BAROMETER PRESSURE", 0.0);
		this.visibility = (double) map.getOrDefault("AMBIENT VISIBILITY", 0.0);
	}

	public double getRudder() {
		return rudder;
	}

	public double getThrottle() {
		return throttle;
	}

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getGroundSpeed() {
		return groundSpeed;
	}

	public double getHeading() {
		return heading;
	}

	public String getTitle() {
		return title;
	}

	public String getAtcState() {
		return atcState;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public double getWindDirection() {
		return windDirection;
	}

	public double getVisibility() {
		return visibility;
	}

	public double getAmbientTemperature() {
		return ambientTemperature;
	}

	public double getAmbientPressure() {
		return ambientPressure;
	}

	public double getBarometerPressure() {
		return barometerPressure;
	}
}
