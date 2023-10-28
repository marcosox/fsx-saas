package io.github.marcosox.fsxsaas.fsx.helpers;

import flightsim.simconnect.SimConnectDataType;
import flightsim.simconnect.recv.RecvSimObjectData;

public class MyDataDefinitionWrapper {
	private final String varName;
	private final String unitsName;
	private final SimConnectDataType dataType;

	public MyDataDefinitionWrapper(String varName, String unitsName, SimConnectDataType dataType) {
		this.varName = varName;
		this.unitsName = unitsName;
		this.dataType = dataType;
	}

	public String getVarName() {
		return varName;
	}

	public String getUnitsName() {
		return unitsName;
	}

	public SimConnectDataType getDataType() {
		return dataType;
	}

	public Object getValue(RecvSimObjectData e) {
		if (dataType == SimConnectDataType.FLOAT32) {
			return e.getDataFloat32();
		} else if (dataType == SimConnectDataType.FLOAT64) {
			return e.getDataFloat64();
		} else if (dataType == SimConnectDataType.INT32) {
			return e.getDataInt32();
		} else if (dataType == SimConnectDataType.INT64) {
			return e.getDataInt64();
		} else if (dataType == SimConnectDataType.INITPOSITION) {
			return e.getInitPosition();
		} else if (dataType == SimConnectDataType.LATLONALT) {
			return e.getLatLonAlt();
		} else if (dataType == SimConnectDataType.STRING8) {
			return e.getDataString8();
		} else if (dataType == SimConnectDataType.STRING32) {
			return e.getDataString32();
		} else if (dataType == SimConnectDataType.STRING64) {
			return e.getDataString64();
		} else if (dataType == SimConnectDataType.STRING128) {
			return e.getDataString128();
		} else if (dataType == SimConnectDataType.STRING256) {
			return e.getDataString256();
		} else if (dataType == SimConnectDataType.STRING260) {
			return e.getDataString260();
		} else if (dataType == SimConnectDataType.STRINGV) {
			return e.getDataStringV();
		} else if (dataType == SimConnectDataType.WAYPOINT) {
			return e.getWaypoint();
		} else if (dataType == SimConnectDataType.XYZ) {
			return e.getXYZ();
		} else if (dataType == SimConnectDataType.MARKERSTATE) {
			return e.getMarkerState();
		} else {
			System.err.println("Couldn't get value for " + getVarName());
			return null;
		}
	}
}
