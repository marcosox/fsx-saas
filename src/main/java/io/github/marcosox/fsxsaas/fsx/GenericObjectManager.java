package io.github.marcosox.fsxsaas.fsx;

import io.github.marcosox.fsxsaas.fsx.helpers.ConversionUtils;
import io.github.marcosox.fsxsaas.fsx.models.GeoJSONSerializable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericObjectManager {

	public final int tracesMaxLength = 100;
	public final int historyMaxLength = 100;

	private final Map<String, JsonObject> objects = new HashMap<>();
	private final Map<String, JsonObject> traces = new HashMap<>();
	private final Map<String, List<JsonObject>> history = new HashMap<>();
	private final Object lock = new Object();

	public GenericObjectManager() {
	}

	void addObject(GeoJSONSerializable receivedObject) {
		synchronized (this.lock) {

			JsonObject jsonObject = ConversionUtils.convertToGeoJsonPoint(receivedObject);
			String objectId = String.valueOf(receivedObject.getId());

			// add to the positions
			objects.put(objectId, jsonObject);

			// add to the line trace
			if (!this.traces.containsKey(objectId)) {
				// need to create an empty geojson linestring, so that only the coordinates can be updated later
				JsonObject defaultLineTraceObject = new JsonObject();
				defaultLineTraceObject.put("type", "Feature");
				JsonObject lineTraceGeometry = new JsonObject();
				lineTraceGeometry.put("type", "LineString");
				lineTraceGeometry.put("coordinates", new JsonArray());
				defaultLineTraceObject.put("geometry", lineTraceGeometry);
				JsonObject lineTraceProperties = new JsonObject();
				lineTraceProperties.put("id", objectId + "_trace");
				defaultLineTraceObject.put("properties", lineTraceProperties);
				this.traces.put(objectId, defaultLineTraceObject);
			}
			JsonObject lineTrace = this.traces.get(objectId);

			// JsonArray has no .add(int, Object) method
			JsonArray newCoordinates = new JsonArray();
			newCoordinates.add(new JsonArray().add(receivedObject.getLongitude()).add(receivedObject.getLatitude()))
					.addAll(lineTrace.getJsonObject("geometry").getJsonArray("coordinates"));
			// truncate line trace
			if (newCoordinates.size() > tracesMaxLength) {
				newCoordinates.getList().subList(tracesMaxLength, newCoordinates.size()).clear();
			}
			lineTrace.getJsonObject("geometry").put("coordinates", newCoordinates);
			this.traces.put(objectId + "_trace", lineTrace);

			// add to the points trace
			List<JsonObject> objectHistory = this.history.getOrDefault(objectId, new ArrayList<>());
//			JsonObject aircraftTracePoint = aircraftObject.copy();	// this doesn't work because it raises Illegal type in JsonObject: class java.math.BigDecimal
			JsonObject historyPoint = ConversionUtils.convertToGeoJsonPoint(receivedObject);

			int pointIndex = 0;
			if (!objectHistory.isEmpty()) {
				pointIndex = objectHistory.get(0).getJsonObject("properties").getInteger("pointIndex") + 1;
			}
			JsonObject pointProperties = historyPoint.getJsonObject("properties");
			pointProperties.put("pointIndex", pointIndex);
			pointProperties.put("id", objectId + "_trace_" + pointIndex);
			objectHistory.add(0, historyPoint);

			if (objectHistory.size() > historyMaxLength) {
				objectHistory.subList(historyMaxLength, objectHistory.size()).clear();
			}
			this.history.put(objectId, objectHistory);
		}
	}

	public Map<String, JsonObject> getObjects() {
		Map<String, JsonObject> val;
		synchronized (this.lock) {
			val = new HashMap<>(this.objects);
		}
		return val;
	}

	public Map<String, JsonObject> getTraces() {
		return this.traces;
	}

	public Map<String, List<JsonObject>> getHistory() {
		return this.history;
	}

	public void clearObjects() {
		synchronized (this.lock) {
			this.objects.clear();
		}
	}

	public void removeObject(int objectID) {
		synchronized (this.lock) {
			this.objects.remove(String.valueOf(objectID));
			this.traces.remove(String.valueOf(objectID));
			this.history.remove(String.valueOf(objectID));
		}
	}
}
