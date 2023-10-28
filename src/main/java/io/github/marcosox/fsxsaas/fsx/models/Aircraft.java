package io.github.marcosox.fsxsaas.fsx.models;

import flightsim.simconnect.data.LatLonAlt;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Aircraft implements GeoJSONSerializable {
	private final int id;
	private final String title;
	private final String atcType;
	private final String atcModel;
	private final String atcID;
	private final String atcAirline;
	private final String atcFlightNumber;
	private final int atcHeavy;
	private final boolean ifr;
	private final String atcState;
	private final String from;
	private final String to;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final double altAgl;
	private final int onGround;
	private final double airSpeed;
	private final double groundSpeed;
	private final double verticalSpeed;
	private final double pitch;
	private final double bank;
	private final double heading;
	private final double aileron;
	private final double elevator;
	private final double rudder;
	private final double throttle;
	private final String transponder;
	private final double windSpeed;
	private final double windDirection;
	private final double visibility;
	private final double ambientTemperature;
	private final double ambientPressure;
	private final double barometerPressure;

	public Aircraft(int id, HashMap<String, Object> map) {
		this.id = id;
		this.title = (String) map.getOrDefault("TITLE", "");
		this.atcType = (String) map.getOrDefault("ATC TYPE", "");
		this.atcModel = (String) map.getOrDefault("ATC MODEL", "");
		this.atcID = (String) map.getOrDefault("ATC ID", "");
		this.atcAirline = (String) map.getOrDefault("ATC AIRLINE", "");
		this.atcFlightNumber = (String) map.getOrDefault("ATC FLIGHT NUMBER", "");
		this.atcHeavy = (int) map.getOrDefault("ATC HEAVY", 0);
		this.atcState = (String) map.getOrDefault("AI TRAFFIC STATE", "");
		this.from = (String) map.getOrDefault("AI TRAFFIC FROMAIRPORT", "");
		this.to = (String) map.getOrDefault("AI TRAFFIC TOAIRPORT", "");
		this.ifr = ((int) map.getOrDefault("AI TRAFFIC ISIFR", 0)) == 1;
		StringBuilder transponderStr = new StringBuilder(String.valueOf(map.getOrDefault("TRANSPONDER CODE:1", "")));
		while (transponderStr.length() < 4) {
			transponderStr.insert(0, "0");    // leftpad(map.getOrDefault(...), 4, "0");
		}
		this.transponder = transponderStr.toString();
		this.latitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).latitude;
		this.longitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).longitude;
		this.altitude = ((LatLonAlt) map.getOrDefault("STRUCT LATLONALT", new LatLonAlt(0, 0, 0))).altitude;
		this.airSpeed = (double) map.getOrDefault("AIRSPEED TRUE", 0.0);
		this.verticalSpeed = (double) map.getOrDefault("VERTICAL SPEED", 0.0);
		this.groundSpeed = (double) map.getOrDefault("GROUND VELOCITY", 0.0);
		this.pitch = (double) map.getOrDefault("PLANE PITCH DEGREES", 0.0);
		this.bank = (double) map.getOrDefault("PLANE BANK DEGREES", 0.0);
		this.heading = (double) map.getOrDefault("PLANE HEADING DEGREES TRUE", 0.0);
		this.altAgl = (double) map.getOrDefault("PLANE ALT ABOVE GROUND", 0.0);
		this.onGround = (int) map.getOrDefault("SIM ON GROUND", 0);
		this.aileron = (double) map.getOrDefault("AILERON POSITION", 0.0);
		this.elevator = (double) map.getOrDefault("ELEVATOR POSITION", 0.0);
		this.rudder = (double) map.getOrDefault("RUDDER POSITION", 0.0);
		this.throttle = (double) map.getOrDefault("GENERAL ENG THROTTLE LEVER POSITION:1", 0.0);
		this.windSpeed = (double) map.getOrDefault("AMBIENT WIND VELOCITY", 0.0);
		this.windDirection = (double) map.getOrDefault("AMBIENT WIND DIRECTION", 0.0);
		this.ambientTemperature = (double) map.getOrDefault("AMBIENT TEMPERATURE", 0.0);
		this.ambientPressure = (double) map.getOrDefault("AMBIENT PRESSURE", 0.0);
		this.barometerPressure = (double) map.getOrDefault("BAROMETER PRESSURE", 0.0);
		this.visibility = (double) map.getOrDefault("AMBIENT VISIBILITY", 0.0);
	}

	public double getAileron() {
		return aileron;
	}

	public double getElevator() {
		return elevator;
	}

	public double getRudder() {
		return rudder;
	}

	public double getThrottle() {
		return throttle;
	}

	public String getAtcType() {
		return atcType;
	}

	public String getAtcModel() {
		return atcModel;
	}

	public String getAtcID() {
		return atcID;
	}

	public String getAtcAirline() {
		return atcAirline;
	}

	public String getAtcFlightNumber() {
		return atcFlightNumber;
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

	public double getAltitude() {
		return altitude;
	}

	public double getAltAgl() {
		return altAgl;
	}

	public double getAirSpeed() {
		return airSpeed;
	}

	public double getGroundSpeed() {
		return groundSpeed;
	}

	public double getPitch() {
		return pitch;
	}

	public double getBank() {
		return bank;
	}

	public double getHeading() {
		return heading;
	}

	public String getTitle() {
		return title;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public int getAtcHeavy() {
		return atcHeavy;
	}

	public boolean getIFR() {
		return ifr;
	}

	public String getAtcState() {
		return atcState;
	}

	public String getTransponder() {
		return transponder;
	}

	public double getVerticalSpeed() {
		return verticalSpeed;
	}

	public int getOnGround() {
		return onGround;
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
