import io.github.marcosox.fsxsaas.fsx.MetarParser;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class MetarTests {

	private void checkStationInfo(JsonObject obj, String icao, int elevationM) {
		assertEquals(new JsonObject()
							 .put("ICAO", icao)
							 .put("elevationM", elevationM),
					 obj);

	}

	private void checkTime(JsonObject obj, int day, int hour, int min, String code) {
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
		assertEquals(new JsonObject()
							 .put("iso8601", iso8601Date)
							 .put("d", dt.getDayOfMonth())
							 .put("h", dt.getHour())
							 .put("m", dt.getMinute())
							 .put("code", code),
					 obj);
	}

	private JsonObject buildWind(int dir, int spd, int gustsSpeed, String units, int depth, String turbulence, String windShear) {
		JsonObject result = new JsonObject()
				.put("direction", dir)
				.put("speed", spd)
				.put("units", units)
				.put("depth", depth);
		if (turbulence.isEmpty()) {
			result.putNull("turbulence");
		} else {
			result.put("turbulence", turbulence);
		}
		if (windShear.isEmpty()) {
			result.putNull("windshear");
		} else {
			result.put("windshear", windShear);
		}
		if (gustsSpeed != 0) {
			result.put("gustSpeed", gustsSpeed);
		} else {
			result.putNull("gustSpeed");
		}
		return result;
	}

	private JsonObject buildVisibility(boolean lessThanMinimum, double distance, String units, int base, int depth) {
		return new JsonObject()
				.put("lessThanMinimum", lessThanMinimum)
				.put("distance", distance)
				.put("units", units)
				.put("base", base)
				.put("depth", depth);
	}

	private JsonObject buildWindAloft(int dir, int spd, int alt, String units, int gustsSpeed, String turbulence, String windShear, int temperature) {
		JsonObject result = buildWind(dir, spd, gustsSpeed, units, alt, turbulence, windShear);
		result.put("altitude", result.remove("depth"));
		if (temperature == 999) {
			result.putNull("temperature");
		} else {
			result.put("temperature", temperature);
		}
		return result;
	}

	private void checkArray(String message, JsonArray expected, JsonArray actual) {
		assertEquals(message + " array size:", expected.size(), actual.size());
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(message + " at array element " + (i + 1), expected.getJsonObject(i), actual.getJsonObject(i));
		}
	}

	private void checkEmptyObject(JsonObject parsedMetar, String key) {
		assertEquals(key, new JsonObject(), parsedMetar.getJsonObject(key));
	}

	private void checkEmptyArray(JsonObject parsedMetar, String key) {
		assertEquals(key, new JsonArray(), parsedMetar.getJsonArray(key));
	}

	@Test
	public void testFair() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 190337Z 00000KT&D0NG 27019KT&A1989NG 27024KT&A5989NG 100KM&B-460&D3048 CLR 15/05 Q1013 @@@ 66 15 270 19 | 197 15 270 24 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 19, 3, 37, "190337Z");
		assertEquals(buildWind(0, 0, 0, "KT", 0, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("windsAloft", new JsonArray()
						   .add(buildWindAloft(270, 19, 1989, "KT", 0, "None", "Gradual", 999))
						   .add(buildWindAloft(270, 24, 5989, "KT", 0, "None", "Gradual", 999))
						   .add(buildWindAloft(270, 19, 6600, "KT", 0, "", "", 15))
						   .add(buildWindAloft(270, 24, 19700, "KT", 0, "", "", 15)),
				   parsedMetar.getJsonArray("windsAloft"));
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageWord", "Clear")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()
						   .add(new JsonObject()
										.put("temperature", 15)
										.put("dewpoint", 5)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 1013)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "surfaceWindsVariation");
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 100, "KM", -460, 3048)),
					 parsedMetar.getJsonArray("visibilities"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testWindy() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 24705G06KT&D975NG 13520KT&A987NG 129V141 16KM&B-462&D3500 +VCTSRA 8ST007&ST001FNMR000N 6CU024&CU001FNLR000N 5CI294&CI001FNVN000N 13/12 07/05&A987 Q1009 @@@ 33 7 135 20 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(247, 5, 6, "KT", 975, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("windsAloft",
				   new JsonArray()
						   .add(buildWindAloft(135, 20, 987, "KT", 0, "None", "Gradual", 999))
						   .add(buildWindAloft(135, 20, 3300, "KT", 0, "", "", 7)),
				   parsedMetar.getJsonArray("windsAloft"));
		assertEquals("surfaceWindsVariation",
					 new JsonArray()
							 .add(new JsonObject().put("start", 129).put("end", 141)),
					 parsedMetar.getJsonArray("surfaceWindsVariation"));
		assertEquals("visibilities",
					 new JsonArray().add(buildVisibility(false, 16, "KM", -462, 3500)),
					 parsedMetar.getJsonArray("visibilities"));
		assertEquals("presentConditions",
					 new JsonObject().put("intensity", "Severe").put("vicinity", true).put("descriptor", "thunderstorm").put("phenomena", "rain"),
					 parsedMetar.getJsonObject("presentConditions"));
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageLevel", 8)
										.put("type", "Stratus")
										.put("height", 700)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Moderate")
										.put("precipitationType", "Rain")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 6)
										.put("type", "Cumulus")
										.put("height", 2400)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Light")
										.put("precipitationType", "Rain")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 5)
										.put("type", "Cirrus")
										.put("height", 29400)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Very light")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures",
				   new JsonArray()
						   .add(new JsonObject().put("temperature", 13).put("dewpoint", 12))
						   .add(new JsonObject().put("temperature", 7).put("dewpoint", 5).put("altitude", 987)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject().put("QNH", 1009).put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testThunderstorm() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 32518G28KT&D980MM 320V330 31522G30KT&A987MM 311V318 80KM&B-462&D3048 8CB016&CB001FSDR000N 4CI327&CI001FMVN000N 19/17 Q0989 @@@ 33 19 315 22 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(325, 18, 28, "KT", 980, "Moderate", "Moderate"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("windsAloft",
				   new JsonArray()
						   .add(buildWindAloft(315, 22, 987, "KT", 30, "Moderate", "Moderate", 999))
						   .add(buildWindAloft(315, 22, 3300, "KT", 0, "", "", 19)),
				   parsedMetar.getJsonArray("windsAloft"));
		checkArray("surfaceWindsVariation",
				   new JsonArray()
						   .add(new JsonObject().put("start", 320).put("end", 330))
						   .add(new JsonObject().put("start", 311).put("end", 318)),
				   parsedMetar.getJsonArray("surfaceWindsVariation"));
		assertEquals("visibilities",
					 new JsonArray().add(buildVisibility(false, 80, "KM", -462, 3048)),
					 parsedMetar.getJsonArray("visibilities"));
		checkEmptyObject(parsedMetar, "presentConditions");
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageLevel", 8)
										.put("type", "Thunderstorm (Cumulonembus)")
										.put("height", 1600)
										.put("top", "Flat")
										.put("turbulence", "Severe")
										.put("precipitation", "Dense")
										.put("precipitationType", "Rain")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 4)
										.put("type", "Cirrus")
										.put("height", 32700)
										.put("top", "Flat")
										.put("turbulence", "Moderate")
										.put("precipitation", "Very light")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures",
				   new JsonArray()
						   .add(new JsonObject().put("temperature", 19).put("dewpoint", 17)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject().put("QNH", 989).put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testCold() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 19505KT&D985NG 20510G13KT&A1000NG 202V207 21515G18KT&A2000NG 211V219 32KM&B-449&D3048 3ST016&ST001FNLS000T 2CU055&CU001FNLN000T -3/-8 -8/-16&A1000 Q1015 @@@ 33 -8 205 10 | 66 -8 215 15 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(195, 5, 0, "KT", 985, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("windsAloft", new JsonArray()
						   .add(buildWindAloft(205, 10, 1000, "KT", 13, "None", "Gradual", 999))
						   .add(buildWindAloft(215, 15, 2000, "KT", 18, "None", "Gradual", 999))
						   .add(buildWindAloft(205, 10, 3300, "KT", 0, "", "", -8))
						   .add(buildWindAloft(215, 15, 6600, "KT", 0, "", "", -8)),
				   parsedMetar.getJsonArray("windsAloft"));
		checkArray("surfaceWindsVariation",
				   new JsonArray()
						   .add(new JsonObject().put("start", 202).put("end", 207))
						   .add(new JsonObject().put("start", 211).put("end", 219)),
				   parsedMetar.getJsonArray("surfaceWindsVariation"));
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageLevel", 3)
										.put("type", "Stratus")
										.put("height", 1600)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Light")
										.put("precipitationType", "Snow")
										.put("baseHeight", 0)
										.put("icingRate", "Traces"))
						   .add(new JsonObject()
										.put("coverageLevel", 2)
										.put("type", "Cumulus")
										.put("height", 5500)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Light")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "Traces")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()
						   .add(new JsonObject()
										.put("temperature", -3)
										.put("dewpoint", -8))
						   .add(new JsonObject()
										.put("temperature", -8)
										.put("dewpoint", -16)
										.put("altitude", 1000)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 1015)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 32, "KM", -449, 3048)),
					 parsedMetar.getJsonArray("visibilities"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testFairWeather() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 18000KT&D985NG 18000KT&A2000NG 0804&B-449&D850 8ST013&ST001FNMN000N 4CU082&CU001FNMN000T 20/19 Q1010 @@@ 66 20 180 0 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(180, 0, 0, "KT", 985, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("windsAloft", new JsonArray()
						   .add(buildWindAloft(180, 0, 2000, "KT", 0, "None", "Gradual", 999))
						   .add(buildWindAloft(180, 0, 6600, "KT", 0, "", "", 20)),
				   parsedMetar.getJsonArray("windsAloft"));
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 804, "M", -449, 850)),
					 parsedMetar.getJsonArray("visibilities"));
		checkEmptyArray(parsedMetar, "surfaceWindsVariation");
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageLevel", 8)
										.put("type", "Stratus")
										.put("height", 1300)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Moderate")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 4)
										.put("type", "Cumulus")
										.put("height", 8200)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Moderate")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "Traces")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()
						   .add(new JsonObject()
										.put("temperature", 20)
										.put("dewpoint", 19)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 1010)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testLightRain() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 22805KT&D900LM 227V229 24510G12KT&A1000MG 243V246 23516G28KT&A2000MM 233V236 32KM&B-449&D5999 2CU042&CU001FMMN000N 8CI295&CI001FMLN000N 26/25 25/25&A1300 Q0989 @@@ 33 25 245 10 | 66 25 235 16 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(228, 5, 0, "KT", 900, "Light", "Moderate"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("surfaceWindsVariation",
				   new JsonArray()
						   .add(new JsonObject().put("start", 227).put("end", 229))
						   .add(new JsonObject().put("start", 243).put("end", 246))
						   .add(new JsonObject().put("start", 233).put("end", 236)),
				   parsedMetar.getJsonArray("surfaceWindsVariation"));
		checkArray("windsAloft", new JsonArray()
						   .add(buildWindAloft(245, 10, 1000, "KT", 12, "Moderate", "Gradual", 999))
						   .add(buildWindAloft(235, 16, 2000, "KT", 28, "Moderate", "Moderate", 999))
						   .add(buildWindAloft(245, 10, 3300, "KT", 0, "", "", 25))
						   .add(buildWindAloft(235, 16, 6600, "KT", 0, "", "", 25)),
				   parsedMetar.getJsonArray("windsAloft"));
		checkArray("clouds", new JsonArray()    // 2CU042&CU001FMMN000N 8CI295&CI001FMLN000N
						   .add(new JsonObject()
										.put("coverageLevel", 2)
										.put("type", "Cumulus")
										.put("height", 4200)
										.put("top", "Flat")
										.put("turbulence", "Moderate")
										.put("precipitation", "Moderate")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 8)
										.put("type", "Cirrus")
										.put("height", 29500)
										.put("top", "Flat")
										.put("turbulence", "Moderate")
										.put("precipitation", "Light")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()
						   .add(new JsonObject()
										.put("temperature", 26)
										.put("dewpoint", 25))
						   .add(new JsonObject()
										.put("temperature", 25)
										.put("dewpoint", 25)
										.put("altitude", 1300)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 989)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 32, "KM", -449, 5999)),
					 parsedMetar.getJsonArray("visibilities"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testRain2() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 000000Z 30110KT&D900NG 300V302 30125KT&A1000LG 295V307 80KM&B-449&D3048 1CU032&CU001FLLN000N 6CI328&CI001FNMN000L 20/12 15/12&A1000 Q1010 @@@ 33 15 301 25 |");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 0, 0, 0, "000000Z");
		assertEquals(buildWind(301, 10, 0, "KT", 900, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkArray("surfaceWindsVariation",
				   new JsonArray()
						   .add(new JsonObject().put("start", 300).put("end", 302))
						   .add(new JsonObject().put("start", 295).put("end", 307)),
				   parsedMetar.getJsonArray("surfaceWindsVariation"));
		checkArray("windsAloft", new JsonArray()
						   .add(buildWindAloft(301, 25, 1000, "KT", 0, "Light", "Gradual", 999))
						   // 33 15 301 25
						   .add(buildWindAloft(301, 25, 3300, "KT", 0, "", "", 15)),
				   parsedMetar.getJsonArray("windsAloft"));
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 80, "KM", -449, 3048)),
					 parsedMetar.getJsonArray("visibilities"));
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageLevel", 1)
										.put("type", "Cumulus")
										.put("height", 3200)
										.put("top", "Flat")
										.put("turbulence", "Light")
										.put("precipitation", "Light")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "None"))
						   .add(new JsonObject()
										.put("coverageLevel", 6)
										.put("type", "Cirrus")
										.put("height", 32800)
										.put("top", "Flat")
										.put("turbulence", "None")
										.put("precipitation", "Moderate")
										.put("precipitationType", "None")
										.put("baseHeight", 0)
										.put("icingRate", "Light")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()    // 20/12 15/12&A1000
						   .add(new JsonObject()
										.put("temperature", 20)
										.put("dewpoint", 12))
						   .add(new JsonObject()
										.put("temperature", 15)
										.put("dewpoint", 12)
										.put("altitude", 1000)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 1010)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testClearSkies() {
		JsonObject parsedMetar = MetarParser.parseMetarString("LIRE&A12 070146Z 00000KT&D609NG 100KM&B-1512&D6072 CLR 14/04 Q1013");
		checkStationInfo(parsedMetar.getJsonObject("stationInfo"), "LIRE", 12);
		checkTime(parsedMetar.getJsonObject("time"), 7, 1, 46, "070146Z");
		assertEquals(buildWind(0, 0, 0, "KT", 609, "None", "Gradual"),
					 parsedMetar.getJsonObject("surfaceWind"));
		checkEmptyArray(parsedMetar, "surfaceWindsVariation");
		checkEmptyArray(parsedMetar, "windsAloft");
		assertEquals("visibilities",
					 new JsonArray()
							 .add(buildVisibility(false, 100, "KM", -1512, 6072)),
					 parsedMetar.getJsonArray("visibilities"));
		checkArray("clouds", new JsonArray()
						   .add(new JsonObject()
										.put("coverageWord", "Clear")),
				   parsedMetar.getJsonArray("clouds"));
		checkArray("temperatures", new JsonArray()
						   .add(new JsonObject()
										.put("temperature", 14)
										.put("dewpoint", 4)),
				   parsedMetar.getJsonArray("temperatures"));
		assertEquals("altimeter",
					 new JsonObject()
							 .put("QNH", 1013)
							 .put("altimeterUnits", "mb"),
					 parsedMetar.getJsonObject("altimeter"));
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}

	@Test
	public void testEmptyString() {
		JsonObject parsedMetar = MetarParser.parseMetarString("");
		checkEmptyObject(parsedMetar, "stationInfo");
		checkEmptyObject(parsedMetar, "time");
		checkEmptyObject(parsedMetar, "surfaceWind");
		checkEmptyObject(parsedMetar, "presentConditions");
		checkEmptyObject(parsedMetar, "altimeter");

		checkEmptyArray(parsedMetar, "windsAloft");
		checkEmptyArray(parsedMetar, "surfaceWindsVariation");
		checkEmptyArray(parsedMetar, "visibilities");
		checkEmptyArray(parsedMetar, "runwayVisualRanges");
		checkEmptyArray(parsedMetar, "clouds");
		checkEmptyArray(parsedMetar, "temperatures");
		checkEmptyArray(parsedMetar, "unparsedTokens");
	}
}
