package io.github.marcosox.fsxsaas.fsx;

import flightsim.simconnect.SimConnect;
import flightsim.simconnect.SimObjectType;
import flightsim.simconnect.recv.*;
import io.github.marcosox.fsxsaas.fsx.helpers.MyDataDefinitionWrapper;
import io.github.marcosox.fsxsaas.fsx.models.*;

import java.util.HashMap;

public class FSXListener implements SimObjectDataTypeHandler,
		FacilitiesListHandler,
		WeatherObservationHandler,
		EventObjectHandler,
		EventHandler,
		EventFilenameHandler,
		ExceptionHandler {
	private final MainManager manager;

	/**
	 * Creates the object responsible for receiving the simconnect responses
	 */
	FSXListener(MainManager manager) {
		this.manager = manager;
	}

	@Override
	public void handleAirportList(SimConnect simConnect, RecvAirportList list) {
		FacilityAirport[] airports = list.getFacilities();
		System.out.println("Received list of " + airports.length + " airports");
		for (FacilityAirport f : airports) {
			manager.addAirport(new Airport(f.getIcao(),
										   f.getLatitude(),
										   f.getLongitude(),
										   f.getAltitude()));
		}
	}

	@Override
	public void handleWaypointList(SimConnect simConnect, RecvWaypointList list) {
		FacilityWaypoint[] waypoints = list.getFacilities();
		System.out.println("Received list of " + waypoints.length + " waypoints");
		for (FacilityWaypoint f : waypoints) {
			manager.addWaypoint(new Waypoint(f.getIcao(),
											 f.getLatitude(),
											 f.getLongitude(),
											 f.getAltitude(),
											 f.getMagVar()));
		}
	}

	@Override
	public void handleVORList(SimConnect simConnect, RecvVORList list) {
		FacilityVOR[] vors = list.getFacilities();
		System.out.println("Received list of " + vors.length + " VOR");
		for (FacilityVOR f : vors) {
			manager.addVOR(new VOR(f.getIcao(),
								   f.getLatitude(),
								   f.getLongitude(),
								   f.getAltitude(),
								   f.getFrequency(),
								   f.getLocalizer(),
								   f.getGlideSlopeAngle(),
								   f.getGlideLat(),
								   f.getGlideLon(),
								   f.getGlideAlt(),
								   f.getFlags()));
		}
	}

	@Override
	public void handleNDBList(SimConnect simConnect, RecvNDBList list) {
		FacilityNDB[] ndbs = list.getFacilities();
		System.out.println("Received list of " + ndbs.length + " NDB");
		for (FacilityNDB f : ndbs) {
			manager.addNDB(new NDB(f.getIcao(),
								   f.getLatitude(),
								   f.getLongitude(),
								   f.getAltitude(),
								   f.getFrequency(),
								   f.getMagVar()));
		}
	}

	@Override
	public void handleException(SimConnect simConnect, RecvException e) {
		System.err.println("Simconnect exception: " + e.getException().getMessage() + " on request " + e.getSendID());
	}

	@Override
	public void handleSimObjectType(SimConnect simConnect, RecvSimObjectDataByType e) {
		int requestID = e.getRequestID();
		int objectID = e.getObjectID();
		if (objectID == 1) {
			objectID = 0;    // fix user id to 0 only
		}
		int entryNumber = e.getEntryNumber();
		int outOf = e.getOutOf();
		HashMap<String, Object> dataValuesMap = new HashMap<>();
		if (requestID == REQUEST_ID.AIRCRAFTS_SCAN.ordinal()) {
			if (entryNumber == 1) {
				System.out.println("Received list of " + outOf + " aircrafts");
			} else if (entryNumber == 0) {
				manager.clearObjects(SimObjectType.AIRCRAFT);
				System.out.println("no aircrafts present at the moment.");
			}
			if (entryNumber > 0) {
				for (MyDataDefinitionWrapper d : manager.getDataDefinitions(SimObjectType.AIRCRAFT)) {
					dataValuesMap.put(d.getVarName(), d.getValue(e));
				}
				manager.addAircraft(new Aircraft(objectID, dataValuesMap));
			}
		} else if (requestID == REQUEST_ID.HELICOPTERS_SCAN.ordinal()) {
			if (entryNumber == 1) {
				System.out.println("Received list of " + outOf + " helicopters");
			} else if (entryNumber == 0) {
				manager.clearObjects(SimObjectType.HELICOPTER);
				System.out.println("no helicopters present at the moment.");
			}
			if (entryNumber > 0) {
				for (MyDataDefinitionWrapper d : manager.getDataDefinitions(SimObjectType.HELICOPTER)) {
					dataValuesMap.put(d.getVarName(), d.getValue(e));
				}
				manager.addHelicopter(new Aircraft(objectID, dataValuesMap));
			}
		} else if (requestID == REQUEST_ID.BOATS_SCAN.ordinal()) {
			if (entryNumber == 1) {
				System.out.println("Received list of " + outOf + " boats");
			} else if (entryNumber == 0) {
				manager.clearObjects(SimObjectType.BOAT);
				System.out.println("no boats present at the moment.");
			}
			if (entryNumber > 0) {
				for (MyDataDefinitionWrapper d : manager.getDataDefinitions(SimObjectType.BOAT)) {
					dataValuesMap.put(d.getVarName(), d.getValue(e));
				}
				manager.addBoat(new Boat(objectID, dataValuesMap));
			}
		} else if (requestID == REQUEST_ID.VEHICLES_SCAN.ordinal()) {
			if (entryNumber == 1) {
				System.out.println("Received list of " + entryNumber + " ground vehicles");
			} else if (entryNumber == 0) {
				manager.clearObjects(SimObjectType.GROUND);
				System.out.println("no ground vehicles present at the moment.");
			}
			if (entryNumber > 0) {
				for (MyDataDefinitionWrapper d : manager.getDataDefinitions(SimObjectType.GROUND)) {
					dataValuesMap.put(d.getVarName(), d.getValue(e));
				}
				manager.addVehicle(new Vehicle(objectID, dataValuesMap));
			}
		}
	}

	@Override
	public void handleWeatherObservation(SimConnect simConnect, RecvWeatherObservation e) {
		int requestID = e.getRequestID();
		if (requestID == REQUEST_ID.METAR.ordinal()) {
			String metar = e.getMetar();
			System.out.println("received METAR: " + metar);
			manager.setMetar(metar);
		}
	}

	@Override
	public void handleEventObject(SimConnect simConnect, RecvEventAddRemove recvEventAddRemove) {
		if (recvEventAddRemove.getEventID() == EVENT_ID.ON_OBJECT_REMOVED.ordinal()) {
			int objectID = recvEventAddRemove.getData();
			manager.removeObject(recvEventAddRemove.getType(), objectID);
		}
	}

	@Override
	public void handleEvent(SimConnect simConnect, RecvEvent recvEvent) {
		if (recvEvent.getEventID() == EVENT_ID.PAUSE.ordinal()) {
			int value = recvEvent.getData();
			manager.setPausedStatus(value == 1);
		} else if (recvEvent.getEventID() == EVENT_ID.SIM_STATUS_CHANGED.ordinal()) {
			int value = recvEvent.getData();
			manager.setSimRunningStatus(value == 1);
		}
	}

	@Override
	public void handleFilename(SimConnect simConnect, RecvEventFilename recvEventFilename) {
		int eventID = recvEventFilename.getEventID();
		if (eventID == EVENT_ID.FLIGHT_LOADED.ordinal()) {
			manager.setFlightFilePath(recvEventFilename.getFileName());
		} else if (eventID == EVENT_ID.AIRCRAFT_LOADED.ordinal()) {
			manager.setAirFilePath(recvEventFilename.getFileName());
		}
	}
}
