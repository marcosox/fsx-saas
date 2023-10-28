package io.github.marcosox.fsxsaas.fsx;

import flightsim.simconnect.FacilityListType;
import flightsim.simconnect.SimConnectDataType;
import flightsim.simconnect.SimObjectType;
import io.github.marcosox.fsxsaas.fsx.helpers.MyDataDefinitionWrapper;
import io.github.marcosox.fsxsaas.fsx.models.*;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainManager {

	private final LinkedList<MyDataDefinitionWrapper> aircraftDataDefinitions;
	private final LinkedList<MyDataDefinitionWrapper> helicopterDataDefinitions;
	private final LinkedList<MyDataDefinitionWrapper> boatDataDefinitions;
	private final LinkedList<MyDataDefinitionWrapper> vehicleDataDefinitions;
	private final FacilitiesManager facilitiesManager;
	private final GenericObjectManager aircraftsManager;
	private final GenericObjectManager helicoptersManager;
	private final GenericObjectManager boatsManager;
	private final GenericObjectManager vehiclesManager;
	private final SimStatus simStatus;

	public MainManager() {
		this.facilitiesManager = new FacilitiesManager();
		this.aircraftsManager = new GenericObjectManager();
		this.helicoptersManager = new GenericObjectManager();
		this.boatsManager = new GenericObjectManager();
		this.vehiclesManager = new GenericObjectManager();
		this.simStatus = new SimStatus();
		aircraftDataDefinitions = new LinkedList<>();
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC TYPE", null, SimConnectDataType.STRING32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC MODEL", null, SimConnectDataType.STRING32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC ID", null, SimConnectDataType.STRING32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC AIRLINE", null, SimConnectDataType.STRING64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC FLIGHT NUMBER", null, SimConnectDataType.STRING8));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC HEAVY", null, SimConnectDataType.INT32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC STATE", null, SimConnectDataType.STRING32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC ISIFR", null, SimConnectDataType.INT32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC FROMAIRPORT", null, SimConnectDataType.STRING8));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC TOAIRPORT", null, SimConnectDataType.STRING8));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("TRANSPONDER CODE:1", null, SimConnectDataType.INT32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("STRUCT LATLONALT", null, SimConnectDataType.LATLONALT));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AIRSPEED TRUE", "KNOTS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("VERTICAL SPEED", "FEET", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("GROUND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE ALT ABOVE GROUND", "FEET", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("SIM ON GROUND", null, SimConnectDataType.INT32));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE PITCH DEGREES", "RADIANS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE BANK DEGREES", "RADIANS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE HEADING DEGREES TRUE", "DEGREES", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AILERON POSITION", "POSITION", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("ELEVATOR POSITION", "POSITION", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("RUDDER POSITION", "POSITION", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("GENERAL ENG THROTTLE LEVER POSITION:1", "PERCENT", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND DIRECTION", "DEGREES", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT TEMPERATURE", "CELSIUS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT PRESSURE", "inHg", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("BAROMETER PRESSURE", "MILLIBARS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT VISIBILITY", "KILOMETERS", SimConnectDataType.FLOAT64));
		aircraftDataDefinitions.addLast(new MyDataDefinitionWrapper("TITLE", null, SimConnectDataType.STRINGV));

		helicopterDataDefinitions = new LinkedList<>();
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC TYPE", null, SimConnectDataType.STRING32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC MODEL", null, SimConnectDataType.STRING32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC ID", null, SimConnectDataType.STRING32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC AIRLINE", null, SimConnectDataType.STRING32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC FLIGHT NUMBER", null, SimConnectDataType.STRING8));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ATC HEAVY", null, SimConnectDataType.INT32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC STATE", null, SimConnectDataType.STRING32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC FROMAIRPORT", null, SimConnectDataType.STRING8));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC TOAIRPORT", null, SimConnectDataType.STRING8));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("TRANSPONDER CODE:1", null, SimConnectDataType.INT32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("STRUCT LATLONALT", null, SimConnectDataType.LATLONALT));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AIRSPEED TRUE", "KNOTS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("VERTICAL SPEED", "FEET", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("GROUND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE ALT ABOVE GROUND", "FEET", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("SIM ON GROUND", null, SimConnectDataType.INT32));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE PITCH DEGREES", "RADIANS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE BANK DEGREES", "RADIANS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE HEADING DEGREES TRUE", "DEGREES", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AILERON POSITION", "POSITION", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("ELEVATOR POSITION", "POSITION", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("RUDDER POSITION", "POSITION", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("GENERAL ENG THROTTLE LEVER POSITION:1", "PERCENT", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND DIRECTION", "DEGREES", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT TEMPERATURE", "CELSIUS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT PRESSURE", "inHg", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("BAROMETER PRESSURE", "MILLIBARS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT VISIBILITY", "KILOMETERS", SimConnectDataType.FLOAT64));
		helicopterDataDefinitions.addLast(new MyDataDefinitionWrapper("TITLE", null, SimConnectDataType.STRINGV));

		boatDataDefinitions = new LinkedList<>();
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("STRUCT LATLONALT", null, SimConnectDataType.LATLONALT));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("AIRSPEED TRUE", "KNOTS", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("GROUND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE ALT ABOVE GROUND", "FEET", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE BANK DEGREES", "RADIANS", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE HEADING DEGREES TRUE", "DEGREES", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("RUDDER POSITION", "POSITION", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("GENERAL ENG THROTTLE LEVER POSITION:1", "PERCENT", SimConnectDataType.FLOAT64));
		boatDataDefinitions.addLast(new MyDataDefinitionWrapper("TITLE", null, SimConnectDataType.STRINGV));

		vehicleDataDefinitions = new LinkedList<>();
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AI TRAFFIC STATE", null, SimConnectDataType.STRING8));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("STRUCT LATLONALT", null, SimConnectDataType.LATLONALT));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("GROUND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("PLANE HEADING DEGREES TRUE", "DEGREES", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("RUDDER POSITION", "POSITION", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("GENERAL ENG THROTTLE LEVER POSITION:1", "PERCENT", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND VELOCITY", "KNOTS", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT WIND DIRECTION", "DEGREES", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT TEMPERATURE", "CELSIUS", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT PRESSURE", "inHg", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("BAROMETER PRESSURE", "MILLIBARS", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("AMBIENT VISIBILITY", "KILOMETERS", SimConnectDataType.FLOAT64));
		vehicleDataDefinitions.addLast(new MyDataDefinitionWrapper("TITLE", null, SimConnectDataType.STRINGV));
	}

	List<MyDataDefinitionWrapper> getDataDefinitions(SimObjectType type) {

		switch (type) {
			case AIRCRAFT: {
				return aircraftDataDefinitions;
			}
			case HELICOPTER: {
				return helicopterDataDefinitions;
			}
			case BOAT: {
				return boatDataDefinitions;
			}
			case GROUND: {
				return vehicleDataDefinitions;
			}
		}
		return aircraftDataDefinitions;
	}

	public JsonObject getUserObjectProperties() {
		if (aircraftsManager.getObjects().containsKey("0")) {
			return aircraftsManager.getObjects().get("0").getJsonObject("properties");
		} else if (helicoptersManager.getObjects().containsKey("0")) {
			return helicoptersManager.getObjects().get("0").getJsonObject("properties");
		}
		return null;
	}

	public void addAircraft(Aircraft aircraft) {
		aircraftsManager.addObject(aircraft);
	}

	public void addHelicopter(Aircraft helicopter) {
		helicoptersManager.addObject(helicopter);
	}

	public void addBoat(Boat boat) {
		boatsManager.addObject(boat);
	}

	public void addVehicle(Vehicle vehicle) {
		vehiclesManager.addObject(vehicle);
	}

	public void addAirport(Airport airport) {
		facilitiesManager.addAirport(airport);
	}

	public void addVOR(VOR vor) {
		facilitiesManager.addVOR(vor);
	}

	public void addNDB(NDB ndb) {
		facilitiesManager.addNDB(ndb);
	}

	public void addWaypoint(Waypoint waypoint) {
		facilitiesManager.addWaypoint(waypoint);
	}

	public Map<String, JsonObject> getObjects(SimObjectType type) {
		switch (type) {
			case AIRCRAFT: {
				return aircraftsManager.getObjects();
			}
			case HELICOPTER: {
				return helicoptersManager.getObjects();
			}
			case BOAT: {
				return boatsManager.getObjects();
			}
			case GROUND: {
				return vehiclesManager.getObjects();
			}
		}
		return new HashMap<>();
	}

	public Map<String, JsonObject> getTraces(SimObjectType type) {
		switch (type) {
			case AIRCRAFT: {
				return aircraftsManager.getTraces();
			}
			case HELICOPTER: {
				return helicoptersManager.getTraces();
			}
			case BOAT: {
				return boatsManager.getTraces();
			}
			case GROUND: {
				return vehiclesManager.getTraces();
			}
		}
		return new HashMap<>();
	}

	public Map<String, List<JsonObject>> getHistory(SimObjectType type) {
		switch (type) {
			case AIRCRAFT: {
				return aircraftsManager.getHistory();
			}
			case HELICOPTER: {
				return helicoptersManager.getHistory();
			}
			case BOAT: {
				return boatsManager.getHistory();
			}
			case GROUND: {
				return vehiclesManager.getHistory();
			}
		}
		return new HashMap<>();
	}

	public int getHistoryTrailLength(SimObjectType type) {
		switch (type) {
			case AIRCRAFT: {
				return aircraftsManager.historyMaxLength;
			}
			case HELICOPTER: {
				return helicoptersManager.historyMaxLength;
			}
			case BOAT: {
				return boatsManager.historyMaxLength;
			}
			case GROUND: {
				return vehiclesManager.historyMaxLength;
			}
		}
		return aircraftsManager.historyMaxLength;
	}

	public void removeObject(SimObjectType type, int objectID) {
		switch (type) {
			case AIRCRAFT: {
				aircraftsManager.removeObject(objectID);
				break;
			}
			case HELICOPTER: {
				helicoptersManager.removeObject(objectID);
				break;
			}
			case BOAT: {
				boatsManager.removeObject(objectID);
				break;
			}
			case GROUND: {
				vehiclesManager.removeObject(objectID);
				break;
			}
		}
	}

	public void clearObjects(SimObjectType type) {
		switch (type) {
			case AIRCRAFT: {
				aircraftsManager.clearObjects();
				break;
			}
			case HELICOPTER: {
				helicoptersManager.clearObjects();
				break;
			}
			case BOAT: {
				boatsManager.clearObjects();
				break;
			}
			case GROUND: {
				vehiclesManager.clearObjects();
				break;
			}
		}
	}

	public void clearAll() {
		facilitiesManager.clearAll();
		aircraftsManager.clearObjects();
		helicoptersManager.clearObjects();
		boatsManager.clearObjects();
		vehiclesManager.clearObjects();
	}

	public Map<String, JsonObject> getFacilities(FacilityListType type) {
		switch (type) {
			case AIRPORT: {
				return facilitiesManager.getAirports();
			}
			case VOR: {
				return facilitiesManager.getVors();
			}
			case NDB: {
				return facilitiesManager.getNDBs();
			}
			case WAYPOINT: {
				return facilitiesManager.getWaypoints();
			}
		}
		return new HashMap<>();
	}

	public void setMetar(String metar) {
		facilitiesManager.setMetar(metar);
	}

	public String getMetar() {
		return facilitiesManager.getMetar();
	}

	public JsonObject getParsedMetar() {
		return facilitiesManager.getParsedMetar();
	}

	public void setPausedStatus(boolean paused) {
		simStatus.setPaused(paused);
	}

	public void setSimRunningStatus(boolean simRunning) {
		simStatus.setSimRunningStatus(simRunning);
	}

	public void setFlightFilePath(String fileName) {
		simStatus.setFlightFilePath(fileName);
	}

	public void setAirFilePath(String fileName) {
		simStatus.setAirFilePath(fileName);
	}

	public SimStatus getSimStatus() {
		return this.simStatus;
	}
}
