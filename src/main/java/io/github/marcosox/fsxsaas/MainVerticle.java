package io.github.marcosox.fsxsaas;

import flightsim.simconnect.FacilityListType;
import flightsim.simconnect.SimObjectType;
import io.github.marcosox.fsxsaas.fsx.FSX;
import io.github.marcosox.fsxsaas.fsx.MainManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainVerticle extends AbstractVerticle {
	private final static String APP_NAME = "FSX simconnect as a service";
	private final String APP_VERSION = getClass().getPackage().getSpecificationVersion();
	private final static int DEFAULT_PORT = 8080;
	private final static int DEFAULT_TRAFFIC_SCAN_INTERVAL_MS = 1000;

	private MainManager manager;
	private FSX fsx;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(MainVerticle.class.getName());
	}

	/**
	 * Main entry point
	 *
	 * @param fut Vert.x Future object
	 */
	@Override
	public void start(Promise<Void> fut) {
		System.out.println("Welcome to " + APP_NAME + " version " + APP_VERSION);
		System.out.println("MainVerticle.start()");
		setup();
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.route().handler(CorsHandler.create().addRelativeOrigin("http[s]?://*:*")
									   .allowedMethod(HttpMethod.GET)
									   .allowedMethod(HttpMethod.POST)
									   .allowedHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.toString())
									   .allowedHeader(HttpHeaders.CONTENT_TYPE.toString())
									   .allowedHeader(HttpHeaders.ORIGIN.toString()));

		router.get("/aircrafts").handler(r -> this.handleGetAll(r, manager.getObjects(SimObjectType.AIRCRAFT)));
		router.get("/aircraft_traces").handler(r -> this.handleGetAll(r, manager.getTraces(SimObjectType.AIRCRAFT)));
		router.get("/aircraft_points").handler(r -> this.handleGetArrayMap(r, manager.getHistory(SimObjectType.AIRCRAFT), manager.getHistoryTrailLength(SimObjectType.AIRCRAFT)));
		router.get("/aircrafts/:id").handler(r -> this.handleGetItem(r, manager.getObjects(SimObjectType.AIRCRAFT)));

		router.get("/helicopters").handler(r -> this.handleGetAll(r, manager.getObjects(SimObjectType.HELICOPTER)));
		router.get("/helicopter_traces").handler(r -> this.handleGetAll(r, manager.getTraces(SimObjectType.HELICOPTER)));
		router.get("/helicopter_history").handler(r -> this.handleGetArrayMap(r, manager.getHistory(SimObjectType.HELICOPTER), manager.getHistoryTrailLength(SimObjectType.HELICOPTER)));
		router.get("/helicopters/:id").handler(r -> this.handleGetItem(r, manager.getObjects(SimObjectType.HELICOPTER)));

		router.get("/boats").handler(r -> this.handleGetAll(r, manager.getObjects(SimObjectType.BOAT)));
		router.get("/boat_traces").handler(r -> this.handleGetAll(r, manager.getTraces(SimObjectType.BOAT)));
		router.get("/boat_history").handler(r -> this.handleGetArrayMap(r, manager.getHistory(SimObjectType.BOAT), manager.getHistoryTrailLength(SimObjectType.BOAT)));
		router.get("/boats/:id").handler(r -> this.handleGetItem(r, manager.getObjects(SimObjectType.BOAT)));

		router.get("/vehicles").handler(r -> this.handleGetAll(r, manager.getObjects(SimObjectType.GROUND)));
		router.get("/vehicle_traces").handler(r -> this.handleGetAll(r, manager.getTraces(SimObjectType.GROUND)));
		router.get("/vehicle_history").handler(r -> this.handleGetArrayMap(r, manager.getHistory(SimObjectType.GROUND), manager.getHistoryTrailLength(SimObjectType.GROUND)));
		router.get("/vehicles/:id").handler(r -> this.handleGetItem(r, manager.getObjects(SimObjectType.GROUND)));

		router.get("/airports").handler(r -> this.handleGetAll(r, manager.getFacilities(FacilityListType.AIRPORT)));
		router.get("/airports/:id").handler(r -> this.handleGetItem(r, manager.getFacilities(FacilityListType.AIRPORT)));
		router.get("/vors").handler(r -> this.handleGetAll(r, manager.getFacilities(FacilityListType.VOR)));
		router.get("/vors/:id").handler(r -> this.handleGetItem(r, manager.getFacilities(FacilityListType.VOR)));
		router.get("/ndbs").handler(r -> this.handleGetAll(r, manager.getFacilities(FacilityListType.NDB)));
		router.get("/ndbs/:id").handler(r -> this.handleGetItem(r, manager.getFacilities(FacilityListType.NDB)));
		router.get("/waypoints").handler(r -> this.handleGetAll(r, manager.getFacilities(FacilityListType.WAYPOINT)));
		router.get("/waypoints/:id").handler(r -> this.handleGetItem(r, manager.getFacilities(FacilityListType.WAYPOINT)));
		router.get("/metar").handler(this::handleMetar);
		router.get("/parsed_metar").handler(r -> this.handleGetObject(r, manager.getParsedMetar()));
		router.get("/status").handler(r -> this.handleGetObject(r, manager.getSimStatus()));
		router.get("/cmd/:command").handler(this::handleFsxCommand);
		router.get("/shutdown").handler(r -> this.quit(r, 0));
		List<Route> routes = router.getRoutes();
		router.get("/").handler(r -> this.handleRootURL(r, routes));
		router.errorHandler(500, rc -> {
			Throwable failure = rc.failure();
			if (failure != null) {
				failure.printStackTrace();
			}
		});
		int port = config().getInteger("port", DEFAULT_PORT);
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(router).listen(port, (connectFuture) -> {
			if (connectFuture.failed()) {
				connectFuture.cause().printStackTrace();
				vertx.close();
				System.exit(1);
			}
		});
		System.out.println("HTTP server ready and listening on port " + port);
	}

	private void handleGetObject(RoutingContext routingContext, Object o) {
		routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(o));
	}

	private void handleMetar(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/plain").end(manager.getMetar());
	}

	/**
	 * Quits the application
	 *
	 * @param routingContext http request routing context
	 * @param status         exit status
	 */
	private void quit(RoutingContext routingContext, int status) {
		routingContext.response().putHeader("content-type", "text/plain").end("BYE");
		routingContext.vertx().close();
		System.exit(status);
	}

	/**
	 * Root url handler
	 *
	 * @param routingContext http request routing context
	 * @param routes         list of routes to display
	 */
	private void handleRootURL(RoutingContext routingContext, List<Route> routes) {
		StringBuilder routesList = new StringBuilder();
		routesList.append("<ul>");
		for (Route r : routes) {
			if (r.getPath() != null) {
				routesList.append("<li><a href='").append(r.getPath()).append("'>").append(r.getPath()).append("</a></li>");
			}
		}
		routesList.append("</ul>");
		routingContext.response().putHeader("content-type", "text/html").end(routesList.toString());
	}

	/**
	 * Handler for the LIST action
	 *
	 * @param routingContext http request routing context
	 * @param map            items map
	 */
	private void handleGetAll(RoutingContext routingContext, Map<String, ?> map) {
		routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(map.values()));
	}

	private int getActualTrailLength(RoutingContext r, int managerHistoryMaxLength) {

		String requestedTrailLengthParam = r.request().getParam("max_length");
		int actualTrailLength = managerHistoryMaxLength;
		if (requestedTrailLengthParam != null) {
			actualTrailLength = Integer.parseInt(requestedTrailLengthParam);
		}
		return Math.max(1, actualTrailLength);
	}

	/**
	 * Special Handler for the history array LIST action
	 *
	 * @param r           http request routing context
	 * @param history     http request routing context
	 * @param trailLength http request routing context
	 */
	private void handleGetArrayMap(RoutingContext r, Map<String, List<JsonObject>> history, int trailLength) {
		int actualTrailLength = getActualTrailLength(r, trailLength);

		String spacingParam = r.request().getParam("spacing");
		int spacing = 1;
		if (spacingParam != null) {
			spacing = Integer.parseInt(spacingParam);
		}
		spacing = Math.min(spacing, actualTrailLength);

		// recompute trace counters while flattening the list
		List<JsonObject> result = recomputeTrail(history, actualTrailLength, spacing);
		r.response().putHeader("content-type", "application/json").end(Json.encodePrettily(result));
	}

	private static List<JsonObject> recomputeTrail(Map<String, List<JsonObject>> map, int trailLength, int spacing) {
		List<JsonObject> result = new ArrayList<>();
		for (List<JsonObject> history : map.values()) {
			int i = 0;
			for (JsonObject historyPoint : history) {
				if (i > trailLength) {
					break;
				}
				int pointIndex = historyPoint.getJsonObject("properties").getInteger("pointIndex");
				if (pointIndex % spacing == 0) {
					JsonObject pointProperties = historyPoint.getJsonObject("properties");
					pointProperties.put("trail_counter", i);
					pointProperties.put("trail_length", trailLength);
					pointProperties.put("trail_percent", (i * 1.0) / (trailLength * 1.0));
					result.add(historyPoint);
				}
				i++;
			}
		}
		return result;
	}

	/**
	 * Handler for the RETRIEVE action
	 *
	 * @param r     http request routing context
	 * @param items items map
	 */
	private void handleGetItem(RoutingContext r, Map<String, ?> items) {
		String id = r.request().getParam("id");
		if (id != null) {
			Object item = items.get(id);
			if (item != null) {
				r.response().putHeader("content-type", "application/json").end(Json.encodePrettily(item));
			} else {
				if (id.equals("0")) {
					// user aircraft not found until sim is unpaused and the first info is sent
					r.response().setStatusCode(449).end("Unpause FSX for a second and try again.");
				} else {
					r.response().setStatusCode(404).end("item id " + id + " not found");
				}
			}
		}
	}

	/**
	 * Handles fsx commands: start/stop/restart/clear
	 *
	 * @param routingContext http request routing context
	 */
	private void handleFsxCommand(RoutingContext routingContext) {
		String cmd = routingContext.request().getParam("command");
		String msg;
		switch (cmd) {
			case "help": {
				msg = "<ul>" +
						"<li>start: start simconnect</li>" +
						"<li>stop: stop simconnect</li>" +
						"<li>restart: guess what</li>" +
						"<li>clear: clears all fsx objects from memory (aircrafts, vors, airports etc.)</li>" +
						"</ul>";
				break;
			}
			case "stop": {
				try {
					fsx.stopSimConnect();
					msg = "OK";
				} catch (IOException e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				break;
			}
			case "start": {
				try {
					fsx.startSimConnect();
					msg = "OK";
				} catch (IOException e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				break;
			}
			case "restart": {
				try {
					fsx.restartSimConnect();
					msg = "OK";
				} catch (IOException e) {
					e.printStackTrace();
					msg = e.getMessage();
				}
				break;
			}
			case "clear": {
				manager.clearAll();
				msg = "OK";
				break;
			}
			default: {
				msg = "not a command - try help";
				break;
			}
		}
		JsonObject responseMsg = new JsonObject();
		responseMsg.put("response", msg);
		routingContext.response().putHeader("content-type", "text/html").end(msg);
	}

	/**
	 * Starts simconnect and the object manager
	 */
	private void setup() {
		this.manager = new MainManager();
		this.fsx = new FSX(vertx,
						   this.manager,
						   config().getInteger("scanInterval", DEFAULT_TRAFFIC_SCAN_INTERVAL_MS));
	}
}
