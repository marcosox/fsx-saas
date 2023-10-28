package io.github.marcosox.fsxsaas.fsx;

import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class MetarParser {
	private static final Map<String, String> descriptors = Map.ofEntries(
			entry("MI", "shallow"),
			entry("PR", "partial"),
			entry("DC", "patches"),
			entry("DR", "low drifting"),
			entry("BL", "blowing"),
			entry("SH", "shower"),
			entry("TS", "thunderstorm"),
			entry("FZ", "freezing"));
	private static final Map<String, String> phenomena = Map.ofEntries(
			entry("DZ", "drizzle"),
			entry("RA", "rain"),
			entry("SN", "snow"),
			entry("SG", "snow grains"),
			entry("IC", "ice crystals"),
			entry("PE", "ice pellets"),
			entry("GR", "hail"),
			entry("GS", "small hail/snow pellets"),
			entry("UP", "unknown"),
			entry("BR", "mist"),
			entry("FG", "fog"),
			entry("FU", "smoke"),
			entry("VA", "volcanic ash"),
			entry("DU", "dust"),
			entry("SA", "sand"),
			entry("HZ", "haze"),
			entry("PY", "spray"),
			entry("PO", "dust whirls"),
			entry("SQ", "squalls"),
			entry("FC", "funnel cloud/tornado/waterspout"),
			entry("SS", "sandstorm"),
			entry("DS", "duststorm"));
	static final Map<String, String> intensities = Map.ofEntries(
			entry("N", "None"),
			entry("T", "Traces"),
			entry("S", "Severe"),
			entry("O", "Light"),
			entry("L", "Light"),
			entry("M", "Moderate"),
			entry("H", "Heavy"),
			entry("D", "VeryHigh")
	);
	static final Map<String, String> cloudTypes = Map.ofEntries(
			entry("CU", "Cumulus"),
			entry("CI", "Cirrus"),
			entry("CB", "Thunderstorm (Cumulonembus)"),
			entry("ST", "Stratus"));
	static final Map<String, String> cloudCoverages = Map.ofEntries(
			entry("CLR", "Clear"),
			entry("SKC", "Clear"),
			entry("FEW", "Few"),
			entry("SCT", "Scattered"),
			entry("BKN", "Broken"),
			entry("OVC", "Overcast"));
	static final Map<String, String> cloudTops = Map.ofEntries(
			entry("F", "Flat"),
			entry("R", "Round"),
			entry("A", "Anvil"));
	static final Map<String, String> turbulences = Map.ofEntries(
			entry("N", "None"),
			entry("O", "Light"),
			entry("L", "Light"),
			entry("M", "Moderate"),
			entry("H", "Heavy"),
			entry("S", "Severe"));
	static final Map<String, String> precipitations = Map.ofEntries(
			entry("V", "Very light"),
			entry("L", "Light"),
			entry("M", "Moderate"),
			entry("H", "Heavy"),
			entry("D", "Dense"));
	static final Map<String, String> precipitationTypes = Map.ofEntries(
			entry("N", "None"),
			entry("R", "Rain"),
			entry("F", "Freezing rain"),
			entry("H", "Hail"),
			entry("S", "Snow"));
	static final Map<String, String> icingRates = Map.ofEntries(
			entry("N", "None"),
			entry("T", "Traces"),
			entry("L", "Light"),
			entry("M", "Moderate"),
			entry("S", "Severe"));
	static final Map<String, String> windshears = Map.ofEntries(
			entry("G", "Gradual"),
			entry("M", "Moderate"),
			entry("S", "Steep"),
			entry("I", "Instantaneous"));
	private static final Pattern stationInfoPattern = Pattern.compile("(?<ident>[A-Z]{3,4})&A(?<elevation>[0-9]+)");
	private static final Pattern timestampPattern = Pattern.compile("(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<min>[0-9]{2})Z?");
	private static final Pattern windsPattern = Pattern.compile("(?<direction>[0-9]{3}|VRB)" +
																		"(?<speed>[0-9]{2,3})" +
																		"(?<gustSpeed>G" +
																		"(?<gustSpeedValue>[0-9]{2}))?" +
																		"(?<units>KMH|KT|MPS)" +
																		"(?<extension>&" +
																		"(?<depthAlt>(?<aloftOrDepth>[AD])(?<depthAltValue>[0-9]{1,4}))" +
																		"(?<turbulence>[NOLMHS])" +
																		"(?<windshear>[GMSI])" +
																		")?");
	private static final Pattern surfaceWindsVariationPattern = Pattern.compile("(?<windVarianceStart>[0-9]{3})V(?<windVarianceEnd>[0-9]{3})");
	private static final Pattern visibilityPattern = Pattern.compile("(?<visibility>" +
																			 "(?<lessThanQuarterMile>M1/4SM|<1/4SM)" +
																			 "|(?<kilometers>(?<kilometersValue>[0-9]{2,3})KM)" +
																			 "|(?<meters>(?<metersValue>[0-9]{4})" +
																			 "(?<direction>(?<noDirection>M|NDV|)|(?<cardinalDirection>(N|S|E|W|NW|NE|SW|SE))))" +
																			 "|(?<integerStatuteMiles>(?<integerStatuteMilesValue>[0-9])SM)" +
																			 "|(?<fractionStatuteMile>(?<numerator>[0-9])/(?<denominator>[0-9])SM)" +
																			 ")" +
																			 "(?<extension>&B(?<base>-?[0-9]{1,4})&D(?<depth>[0-9]{3,4}))");
	private static final Pattern rwyVisualRangePattern = Pattern.compile("R(?<runwayID>[0-9A-Z]{1,6})" +
																				 "((?<visualRange>[PM]?[0-9]{4})|" +
																				 "(?<varyingVisualRange>[0-9]{4}/[0-9]{4}))" +
																				 "FT");
	private static final Pattern presentConditionsPattern = Pattern.compile("(?<intensity>[+-]?(VC)?)" +
																					"(?<descriptor>MI|PR|DC|DR|BL|SH|TS|FZ)?" +
																					"(?<phenomena>DZ|RA|SN|SG|IC|PE|GR|GS|UP|BR|FG|FU|VA|DU|SA|HZ|PY|PO|SQ|FC|SS|DS)");
	private static final Pattern cloudsPattern = Pattern.compile("(?<type>OVC|CLR|SKC|FEW|SCT|BKN|[1-8](CI|CU|ST|CB))" +
																		 "(?<codedHeight>[0-9]{3}|///)?" +
																		 "(?<extension>&" +
																		 "(?<priorityCloudType>CI|CU|ST|CB)" +
																		 "[0-9]{3}" +
																		 "(?<top>[FRA])" +
																		 "(?<turbulence>[NOLMHS])" +
																		 "(?<precipitation>[VLMHD][NRFHS])" +
																		 "(?<codedBaseHeight>-?[0-9]{2,3})" +
																		 "(?<icingRate>[NTLMS]))?");
	private static final Pattern temperaturesPattern = Pattern.compile("(?<temperature>-?[0-9]{1,3})/(?<dewpoint>-?[0-9]{1,3})(&A(?<altitude>[0-9]+))?");
	private static final Pattern altimeterPattern = Pattern.compile("(?<units>[AQ])(?<altimeterValue>[0-9]{4})");
	private static final String defaultNotAvailableValue = "N/A";

	/**
	 * Documented <a href="https://learn.microsoft.com/en-us/previous-versions/microsoft-esp/cc526983(v=msdn.10)#metar-data-format">here</a>
	 *
	 * @param metarString the METAR string
	 * @return the parsed METAR object
	 */
	public static JsonObject parseMetarString(String metarString) {

		JsonObject parsedMetar = new JsonObject();

		JsonObject stationInfo = new JsonObject();
		parsedMetar.put("stationInfo", stationInfo);
		JsonObject time = new JsonObject();
		parsedMetar.put("time", time);
		JsonObject surfaceWind = new JsonObject();
		parsedMetar.put("surfaceWind", surfaceWind);
		List<JsonObject> windsAloft = new ArrayList<>();
		parsedMetar.put("windsAloft", windsAloft);
		List<JsonObject> surfaceWindsVariation = new ArrayList<>();
		parsedMetar.put("surfaceWindsVariation", surfaceWindsVariation);
		List<JsonObject> visibilities = new ArrayList<>();
		parsedMetar.put("visibilities", visibilities);
		List<JsonObject> runwayVisualRanges = new ArrayList<>();
		parsedMetar.put("runwayVisualRanges", runwayVisualRanges);
		JsonObject presentConditions = new JsonObject();
		parsedMetar.put("presentConditions", presentConditions);
		List<JsonObject> clouds = new ArrayList<>();
		parsedMetar.put("clouds", clouds);
		List<JsonObject> temperatures = new ArrayList<>();
		parsedMetar.put("temperatures", temperatures);
		JsonObject altimeter = new JsonObject();
		parsedMetar.put("altimeter", altimeter);
		List<String> unparsedTokens = new ArrayList<>();
		parsedMetar.put("unparsedTokens", unparsedTokens);

		StringTokenizer tokenizer = new StringTokenizer(metarString);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			Matcher stationInfoMatcher = stationInfoPattern.matcher(token);
			Matcher timestampMatcher = timestampPattern.matcher(token);
			Matcher windsMatcher = windsPattern.matcher(token);
			Matcher surfaceWindsVariationMatcher = surfaceWindsVariationPattern.matcher(token);
			Matcher visibilityMatcher = visibilityPattern.matcher(token);
			Matcher rwyVisualRangeMatcher = rwyVisualRangePattern.matcher(token);
			Matcher presentConditionsMatcher = presentConditionsPattern.matcher(token);
			Matcher cloudsMatcher = cloudsPattern.matcher(token);
			Matcher temperaturesMatcher = temperaturesPattern.matcher(token);
			Matcher altimeterMatcher = altimeterPattern.matcher(token);

			// ICAO and elevation
			if (stationInfoMatcher.matches()) {
				stationInfo.put("ICAO", stationInfoMatcher.group("ident"));
				stationInfo.put("elevationM", Integer.parseInt(stationInfoMatcher.group("elevation")));
			}
			// Time
			else if (timestampMatcher.matches()) {
				time.mergeIn(parseTimestamp(timestampMatcher));
			}
			// NIL
			else if (token.equals("NIL")) {
				parsedMetar.put("status", "N/A");
			}
			// Winds and winds aloft
			else if (windsMatcher.matches()) {
				JsonObject wind = parseWind(windsMatcher);
				if (windsMatcher.group("aloftOrDepth").equals("D")) {
					surfaceWind.mergeIn(wind);
				} else {
					// winds aloft object without temperature information
					wind.putNull("temperature");
					windsAloft.add(wind);
				}
			}
			// Surface winds variation
			else if (surfaceWindsVariationMatcher.matches()) {
				surfaceWindsVariation.add(parseWindVariation(surfaceWindsVariationMatcher));
			}
			// CAVOK
			else if (token.equals("CAVOK")) {
				clouds.add(new JsonObject().put("type", "Clear skies"));
				visibilities.add(new JsonObject().put("distance", 9999));
			}
			// Visibilities
			else if (visibilityMatcher.matches()) {
				visibilities.add(parseVisibility(visibilityMatcher));
			}
			// Runway visual range
			else if (rwyVisualRangeMatcher.matches()) {
				runwayVisualRanges.add(parseRunwayVisualRange(rwyVisualRangeMatcher));
			}
			// Present conditions
			else if (presentConditionsMatcher.matches()) {
				presentConditions.mergeIn(parsePresentConditions(presentConditionsMatcher));
			}
			// Sky conditions
			else if (cloudsMatcher.matches()) {
				clouds.add(parseClouds(cloudsMatcher));
			}
			// Temperatures
			else if (temperaturesMatcher.matches()) {
				temperatures.add(parseTemperatures(temperaturesMatcher));
			}
			// Altimeter
			else if (altimeterMatcher.matches()) {
				altimeter.mergeIn(parseAltimeter(altimeterMatcher));
			} else {
				unparsedTokens.add(token);
			}
		}
		// winds aloft are always at the end of the string
		if (!unparsedTokens.isEmpty() && unparsedTokens.get(0).equals("@@@")) {
			windsAloft.addAll(parseWindsAloft(String.join(" ", unparsedTokens).replaceAll("@@@", "")));
			unparsedTokens.clear();
		}
		return parsedMetar;
	}

	private static JsonObject parseWindVariation(Matcher matcher) {
		return new JsonObject()
				.put("start", Integer.parseInt(matcher.group("windVarianceStart")))
				.put("end", Integer.parseInt(matcher.group("windVarianceEnd")));
	}

	private static JsonObject parseTimestamp(Matcher matcher) {
		JsonObject result = new JsonObject();
		int day = 0;
		if (matcher.group("day") != null) {
			day = Integer.parseInt(matcher.group("day"));
		}
		int hour = Integer.parseInt(matcher.group("hour"));
		int min = Integer.parseInt(matcher.group("min"));
		OffsetDateTime now = OffsetDateTime.of(LocalDate.now(),
											   LocalTime.now(),
											   ZoneOffset.UTC);
		OffsetDateTime dt = OffsetDateTime.of(now.getYear(),
											  now.getMonthValue(),
											  day <= 0 ? now.getDayOfMonth() : day,
											  hour,
											  min,
											  0,
											  0,
											  ZoneOffset.UTC);
		String iso8601Date = dt.format(DateTimeFormatter.ISO_DATE_TIME);
		iso8601Date = iso8601Date.replace("+00:00", "Z");
		result.put("iso8601", iso8601Date);
		result.put("d", dt.getDayOfMonth());
		result.put("h", dt.getHour());
		result.put("m", dt.getMinute());
		result.put("code", matcher.group());
		return result;
	}

	public static JsonObject parseWind(Matcher matcher) {
		JsonObject result = new JsonObject();
		result.put("direction", Integer.parseInt(matcher.group("direction")));
		result.put("speed", Integer.parseInt(matcher.group("speed")));
		if (matcher.group("gustSpeed") != null) {
			result.put("gustSpeed", Integer.parseInt(matcher.group("gustSpeedValue")));
		} else {
			result.putNull("gustSpeed");
		}
		result.put("units", matcher.group("units"));
		if (matcher.group("extension") != null) {
			int depthOrAltitude = Integer.parseInt(matcher.group("depthAltValue"));
			if (matcher.group("aloftOrDepth").equals("D")) {
				result.put("depth", depthOrAltitude);
			} else {
				result.put("altitude", depthOrAltitude);
			}
			result.put("turbulence", intensities.getOrDefault(matcher.group("turbulence"), defaultNotAvailableValue));
			result.put("windshear", windshears.getOrDefault(matcher.group("windshear"), defaultNotAvailableValue));
		}
		return result;
	}

	public static JsonObject parseVisibility(Matcher matcher) {
		JsonObject result = new JsonObject();
		boolean lessThanMinimum = false;
		String units = "";
		double distance = 0;
		if (matcher.group("lessThanQuarterMile") != null) {
			result.put("lessThanMinimum", true);
			distance = 0.25;
			units = "SM";
		} else if (matcher.group("integerStatuteMiles") != null) {
			distance = Integer.parseInt(matcher.group("integerStatuteMilesValue"));
			units = "SM";
		} else if (matcher.group("kilometers") != null) {
			distance = Integer.parseInt(matcher.group("kilometersValue"));
			units = "KM";
		} else if (matcher.group("meters") != null) {
			distance = Integer.parseInt(matcher.group("metersValue"));
			units = "M";
		} else if (matcher.group("fractionStatuteMile") != null) {
			int numerator = Integer.parseInt(matcher.group("numerator"));
			int denominator = Integer.parseInt(matcher.group("denominator"));
			distance = (numerator * 1.0) / (denominator * 1.0);
			units = "SM";
		}
		result.put("lessThanMinimum", lessThanMinimum);
		result.put("distance", distance);
		result.put("units", units);

		if (matcher.group("extension") != null) {
			result.put("base", Integer.parseInt(matcher.group("base")));
			result.put("depth", Integer.parseInt(matcher.group("depth")));
		}
		return result;
	}

	private static JsonObject parseRunwayVisualRange(Matcher visualRangeMatcher) {
		JsonObject result = new JsonObject();
		result.put("runwayID", visualRangeMatcher.group("runwayID"));
		if (visualRangeMatcher.group("visualRange") != null) {
			String visualRange = visualRangeMatcher.group("visualRange");
			result.put("aboveMax", visualRange.startsWith("P"));
			result.put("belowMin", visualRange.startsWith("M"));
			result.put("visualRange", Integer.parseInt(visualRange.substring(visualRange.length() - 4)));
		} else {
			String varyingVisualRange = visualRangeMatcher.group("varyingVisualRange");
			String[] splitted = varyingVisualRange.split("/");
			result.put("minimumVisualRangeFt", Integer.parseInt(splitted[0]));
			result.put("maximumVisualRangeFt", Integer.parseInt(splitted[1]));
		}
		return result;
	}

	private static JsonObject parsePresentConditions(Matcher matcher) {
		JsonObject result = new JsonObject();
		String intensity = matcher.group("intensity");
		if (intensity.contains("+")) {
			result.put("intensity", "Severe");
		} else if (intensity.contains("-")) {
			result.put("intensity", "Light");
		} else {
			result.put("intensity", "Moderate");
		}
		result.put("vicinity", intensity.contains("VC"));
		result.put("descriptor", descriptors.getOrDefault(matcher.group("descriptor"), defaultNotAvailableValue));
		result.put("phenomena", phenomena.getOrDefault(matcher.group("phenomena"), defaultNotAvailableValue));
		return result;
	}

	private static JsonObject parseClouds(Matcher matcher) {
		JsonObject result = new JsonObject();
		String type = matcher.group("type");
		if (type.matches("[1-8](CI|CU|ST|CB)")) {
			result.put("coverageLevel", Integer.parseInt(type.substring(0, 1)));
			result.put("type", cloudTypes.getOrDefault(type.substring(1), defaultNotAvailableValue));
		} else {
			result.put("coverageWord", cloudCoverages.getOrDefault(type, defaultNotAvailableValue));
		}
		String codedHeightGroup = matcher.group("codedHeight");
		if (codedHeightGroup != null) {
			int codedHeight = Integer.parseInt(matcher.group("codedHeight"));
			if (codedHeight == 999) {
				result.put("height", 100000);
			} else {
				result.put("height", codedHeight * 100);
			}
		}
		if (matcher.group("extension") != null) {
			result.put("type", cloudTypes.getOrDefault(matcher.group("priorityCloudType"), defaultNotAvailableValue));
			result.put("top", cloudTops.getOrDefault(matcher.group("top"), defaultNotAvailableValue));
			result.put("turbulence", turbulences.getOrDefault(matcher.group("turbulence"), defaultNotAvailableValue));
			String precipitation = matcher.group("precipitation");
			result.put("precipitation", precipitations.getOrDefault(precipitation.substring(0, 1), defaultNotAvailableValue));
			result.put("precipitationType", precipitationTypes.getOrDefault(precipitation.substring(1, 2), defaultNotAvailableValue));
			int codedBaseHeight = Integer.parseInt(matcher.group("codedBaseHeight"));
			if (codedBaseHeight == 999) {
				result.put("baseHeight", 100000);
			} else {
				result.put("baseHeight", codedBaseHeight * 100);
			}
			String icingRate = matcher.group("icingRate");
			result.put("icingRate", icingRates.getOrDefault(icingRate, defaultNotAvailableValue));
		}
		return result;
	}

	public static JsonObject parseTemperatures(Matcher matcher) {
		JsonObject result = new JsonObject();
		result.put("temperature", Integer.parseInt(matcher.group("temperature")));
		result.put("dewpoint", Integer.parseInt(matcher.group("dewpoint")));
		if (matcher.group("altitude") != null) {
			result.put("altitude", Integer.parseInt(matcher.group("altitude")));
		}
		return result;
	}

	private static JsonObject parseAltimeter(Matcher matcher) {
		JsonObject result = new JsonObject();
		int value = Integer.parseInt(matcher.group("altimeterValue"));
		if (matcher.group("units").equals("Q")) {
			result.put("QNH", value);
			result.put("altimeterUnits", "mb");
		} else {
			result.put("QNH", value / 100.0);
			result.put("altimeterUnits", "mmHg");
		}
		return result;
	}

	private static List<JsonObject> parseWindsAloft(String tokens) {
		List<JsonObject> result = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(tokens, "|");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (!token.equals("@@@")) {
				String[] values = token.split(" ");
				JsonObject windAloftObj = new JsonObject();
				windAloftObj.put("altitude", Integer.parseInt(values[0]) * 100);
				windAloftObj.put("temperature", Integer.parseInt(values[1]));
				windAloftObj.put("direction", Integer.parseInt(values[2]));
				windAloftObj.put("speed", Integer.parseInt(values[3]));
				windAloftObj.put("units", "KT");
				windAloftObj.putNull("turbulence");
				windAloftObj.putNull("windshear");
				windAloftObj.putNull("gustSpeed");
				result.add(windAloftObj);
			}
		}
		return result;
	}
}
