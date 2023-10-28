package io.github.marcosox.fsxsaas.fsx.helpers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConversionUtils {

	public static JsonObject convertToGeoJsonPoint(Object o) {
		JsonObject properties = JsonObject.mapFrom(o);
		JsonObject result = new JsonObject();
		result.put("type", "Feature");
		JsonObject geometry = new JsonObject();
		geometry.put("type", "Point");
		JsonArray coordinates = new JsonArray();
		coordinates.add(properties.getDouble("longitude"));
		coordinates.add(properties.getDouble("latitude"));
		geometry.put("coordinates", coordinates);
		result.put("geometry", geometry);
		result.put("properties", properties);
		return result;
	}

}
