package io.github.marcosox.fsxsaas.fsx;

import flightsim.simconnect.FacilityListType;
import flightsim.simconnect.SimConnect;
import flightsim.simconnect.SimObjectType;
import flightsim.simconnect.recv.DispatcherTask;
import io.github.marcosox.fsxsaas.fsx.helpers.MyDataDefinitionWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FSX {
	private final MainManager manager;
	private final Vertx vertx;
	private SimConnect simconnect;
	private DispatcherTask dispatcherTask;
	private long trafficScanID = -1;
	private final long scanInterval;

	private enum DATA_DEFINITION_ID {
		BOAT_DETAIL, AIRCRAFT_DETAIL, VEHICLE_DETAIL, HELICOPTER_DETAIL
	}

	public FSX(Vertx vertx,
			   MainManager manager,
			   int scanInterval) {
		this.vertx = vertx;
		this.manager = manager;
		this.scanInterval = scanInterval;
		try {
			startSimConnect();
		} catch (IOException e) {
			System.out.println("Could not connect to FSX! The server will not show any object until fsx is running. Start fsx and trigger /cmd/restart");
			e.printStackTrace();
		}
	}

	public void startSimConnect() throws IOException {
		try {
			if (simconnect != null) {
				stopSimConnect();
				simconnect = null;
			}
		} catch (IOException e) {
			System.out.println("warning closing old instance of simconnect");
			e.printStackTrace();
		}
		simconnect = new SimConnect("fsx-saas");
		dispatcherTask = new DispatcherTask(simconnect);
		FSXListener listener = new FSXListener(this.manager);
		dispatcherTask.addHandlers(listener);
		new Thread(dispatcherTask).start();
		startRequests();
	}

	public void stopSimConnect() throws IOException {
		if (simconnect != null) {
			stopRequests();
		}
		if (dispatcherTask != null) {
			dispatcherTask.tryStop();
		}
		if (simconnect != null) {
			simconnect.close();
			simconnect = null;
		}
	}

	public void restartSimConnect() throws IOException {
		stopSimConnect();
		startSimConnect();
	}

	private void startRequests() throws IOException {

		List<Map.Entry<SimObjectType, DATA_DEFINITION_ID>> objectsRequests = new ArrayList<>();
		objectsRequests.add(new AbstractMap.SimpleEntry<>(SimObjectType.AIRCRAFT, DATA_DEFINITION_ID.AIRCRAFT_DETAIL));
		objectsRequests.add(new AbstractMap.SimpleEntry<>(SimObjectType.HELICOPTER, DATA_DEFINITION_ID.HELICOPTER_DETAIL));
		objectsRequests.add(new AbstractMap.SimpleEntry<>(SimObjectType.BOAT, DATA_DEFINITION_ID.BOAT_DETAIL));
		objectsRequests.add(new AbstractMap.SimpleEntry<>(SimObjectType.GROUND, DATA_DEFINITION_ID.VEHICLE_DETAIL));
		for (Map.Entry<SimObjectType, DATA_DEFINITION_ID> e : objectsRequests) {
			for (MyDataDefinitionWrapper d : this.manager.getDataDefinitions(e.getKey())) {
				simconnect.addToDataDefinition(e.getValue(), d.getVarName(), d.getUnitsName(), d.getDataType());
			}
		}
		simconnect.subscribeToFacilities(FacilityListType.AIRPORT, EVENT_ID.AIRPORTS_SCAN);
		simconnect.subscribeToFacilities(FacilityListType.VOR, EVENT_ID.VOR_SCAN);
		simconnect.subscribeToFacilities(FacilityListType.NDB, EVENT_ID.NDB_SCAN);
		simconnect.subscribeToFacilities(FacilityListType.WAYPOINT, EVENT_ID.WAYPOINTS_SCAN);

		this.trafficScanID = vertx.setPeriodic(scanInterval, e -> {
			try {
				// check if user position is known and request metar
				JsonObject userAircraftProperties = this.manager.getUserObjectProperties();
				if (this.manager.getUserObjectProperties() != null) {
					simconnect.weatherRequestObservationAtNearestStation(REQUEST_ID.METAR,
																		 userAircraftProperties.getFloat("latitude"),
																		 userAircraftProperties.getFloat("longitude"));
				}
				simconnect.requestDataOnSimObjectType(REQUEST_ID.AIRCRAFTS_SCAN, DATA_DEFINITION_ID.AIRCRAFT_DETAIL, 0, SimObjectType.AIRCRAFT);
				simconnect.requestDataOnSimObjectType(REQUEST_ID.HELICOPTERS_SCAN, DATA_DEFINITION_ID.HELICOPTER_DETAIL, 0, SimObjectType.HELICOPTER);
				simconnect.requestDataOnSimObjectType(REQUEST_ID.BOATS_SCAN, DATA_DEFINITION_ID.BOAT_DETAIL, 0, SimObjectType.BOAT);
				simconnect.requestDataOnSimObjectType(REQUEST_ID.VEHICLES_SCAN, DATA_DEFINITION_ID.VEHICLE_DETAIL, 0, SimObjectType.GROUND);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// this one is needed to remove a single object from the status
		simconnect.subscribeToSystemEvent(EVENT_ID.ON_OBJECT_REMOVED, "ObjectRemoved");    // handleEventObject
		// these are needed for the sim status endpoint
		simconnect.subscribeToSystemEvent(EVENT_ID.PAUSE, "Pause");    // handleEvent
		simconnect.subscribeToSystemEvent(EVENT_ID.SIM_STATUS_CHANGED, "Sim");    // handleEvent
		simconnect.subscribeToSystemEvent(EVENT_ID.POSITION_CHANGED, "PositionChanged");    // handleEvent
		simconnect.subscribeToSystemEvent(EVENT_ID.AIRCRAFT_LOADED, "AircraftLoaded");    // handleFilename
		simconnect.subscribeToSystemEvent(EVENT_ID.FLIGHT_LOADED, "FlightLoaded");    // handleFilename
	}

	private void stopRequests() {
		for (FacilityListType f : new FacilityListType[]{
				FacilityListType.AIRPORT,
				FacilityListType.WAYPOINT,
				FacilityListType.VOR,
				FacilityListType.NDB
		}) {
			try {
				simconnect.unSubscribeToFacilities(f);
			} catch (IOException ioe) {
				System.out.println("exception unsubscribing to facility type " + f + ":");
				ioe.printStackTrace();
			}
		}
		vertx.cancelTimer(this.trafficScanID);
	}
}
