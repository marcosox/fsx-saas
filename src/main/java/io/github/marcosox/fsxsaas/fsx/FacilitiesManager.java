package io.github.marcosox.fsxsaas.fsx;

import io.github.marcosox.fsxsaas.fsx.helpers.ConversionUtils;
import io.github.marcosox.fsxsaas.fsx.models.Airport;
import io.github.marcosox.fsxsaas.fsx.models.NDB;
import io.github.marcosox.fsxsaas.fsx.models.VOR;
import io.github.marcosox.fsxsaas.fsx.models.Waypoint;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FacilitiesManager {

	private final Map<String, JsonObject> airports = new HashMap<>();
	private final Map<String, JsonObject> vor = new HashMap<>();
	private final Map<String, JsonObject> ndb = new HashMap<>();
	private final Map<String, JsonObject> waypoints = new HashMap<>();
	private String metar = "";
	private JsonObject parsedMetar = new JsonObject();

	void addAirport(Airport airport) {
		this.airports.put(airport.getIcao(), ConversionUtils.convertToGeoJsonPoint(airport));
	}

	void addWaypoint(Waypoint waypoint) {
		this.waypoints.put(waypoint.getIcao(), ConversionUtils.convertToGeoJsonPoint(waypoint));
	}

	void addVOR(VOR vor) {
		this.vor.put(vor.getIcao(), ConversionUtils.convertToGeoJsonPoint(vor));
	}

	void addNDB(NDB ndb) {
		this.ndb.put(ndb.getIcao(), ConversionUtils.convertToGeoJsonPoint(ndb));
	}

	public Map<String, JsonObject> getAirports() {
		return this.airports;
	}

	public Map<String, JsonObject> getWaypoints() {
		return this.waypoints;
	}

	public Map<String, JsonObject> getVors() {
		return this.vor;
	}

	public Map<String, JsonObject> getNDBs() {
		return this.ndb;
	}

	private void clearAirports() {
		this.airports.clear();
	}

	private void clearVORs() {
		this.vor.clear();
	}

	private void clearNDBs() {
		this.ndb.clear();
	}

	private void clearWaypoints() {
		this.waypoints.clear();
	}

	public void clearAll() {
		clearAirports();
		clearVORs();
		clearNDBs();
		clearWaypoints();
	}

	void setMetar(String metar) {
		this.metar = metar;
		this.parsedMetar = MetarParser.parseMetarString(metar);
	}

	public String getMetar() {
		return metar;
	}

	public JsonObject getParsedMetar() {
		return parsedMetar;
	}
}
