# FSX simconnect-as-a-service

FSX status exposed as GeoJSON API server

This can be used to interface other programs with a running FSX instance via HTTP requests.

The exposed data is read-only (e.g. only LIST and RETRIEVE actions are supported).

An example of use of this application is ~~[FSX web ATC](https://marcosox.github.io/fsx-web-atc/)~~ (deprecated, new app coming out soon), which shows the data in a web map.

## Download and installation
FSX-saas is portable, you just need to download the latest jar from the [releases page] and run it.

## Usage
    java -jar path/to/fsx-saas-x.y.z.jar

To change the application parameters pass the path of a json configuration file:

    java -jar path/to/fsx-saas-x.y.z.jar -conf path/to/config.json

The complete configuration file is this:

    {
        "port": 8080
        "scanInterval": 1000
    }

- `port`: tcp listening port for the server
- `scanInterval`: milliseconds between aircraft requests to fsx

#### Vertx options
Since this application is packaged with a Vertx launcher, all the vertx options can be passed from the command line.
For more informations see the [help page](http://vertx.io/docs/vertx-core/java/#_the_vertx_command_line)

#### Executable version

There's also a Jsmooth project file to pack the release jar into a windows executable file

## Quick start and examples
- Start FSX and load a flight
- Start FSX-saas
- open your browser and point it to [http://localhost:8080/](http://localhost:8080/) to see all the available API endpoints

### Examples

- See all the aircrafts: [http://localhost:8080/aircrafts](http://localhost:8080/aircrafts)
```json
[
	{
		"type" : "Feature",
		"geometry" : {
		"type" : "Point",
		"coordinates" : [ -74.17495295410674, 40.690912621251364 ]
		},
		"properties" : {
			"id" : 1143,
			"title" : "Boeing 737-800 Paint4",
			"atcType" : "BOEING",
			"atcModel" : "B738",
			"atcID" : "N9404N",
			"atcAirline" : "Orbit",
			"atcFlightNumber" : "",
			"atcHeavy" : 0,
			"ifr" : true,
			"atcState" : "sleep",
			"from" : "KEWR",
			"to" : "KPDX",
			"latitude" : 40.690912621251364,
			"longitude" : -74.17495295410674,
			"altitude" : 8.52239990234375,
			"altAgl" : 9.961955279917955,
			"onGround" : 1,
			"airSpeed" : 0.0,
			"groundSpeed" : 7.44839864710621E-8,
			"verticalSpeed" : 0.002971694106236097,
			"pitch" : 0.014412254095077515,
			"bank" : 2.689358030113443E-4,
			"heading" : 42.488722508521455,
			"aileron" : 0.0,
			"elevator" : 0.0,
			"rudder" : 0.0,
			"throttle" : 0.0,
			"transponder" : "1234",
			"windSpeed" : 0.0,
			"windDirection" : 0.0,
			"visibility" : 1.0E7,
			"ambientTemperature" : 14.85124397277832,
			"ambientPressure" : 29.88893587128007,
			"barometerPressure" : 1012.152587890625
		}
	}, {
	...
]
```
- See user aircraft: http://localhost:8080/aircrafts/0 (or http://localhost:8080/helicopters/0 like in this case)
```json
{
	"type" : "Feature",
	"geometry" : {
		"type" : "Point",
		"coordinates" : [ -74.00087224530708, 40.71090559085022 ]
	},
	"properties" : {
		"id" : 0,
		"title" : "Bell 206B JetRanger Paint10",
		"atcType" : "JetRanger",
		"atcModel" : "helicopter",
		"atcID" : "N204TV",
		"atcAirline" : "",
		"atcFlightNumber" : "",
		"atcHeavy" : 0,
		"ifr" : false,
		"atcState" : "init",
		"from" : "",
		"to" : "",
		"latitude" : 40.71090559085022,
		"longitude" : -74.00087224530708,
		"altitude" : 171.41696166992188,
		"altAgl" : 4.250007481797638,
		"onGround" : 1,
		"airSpeed" : 0.0,
		"groundSpeed" : 4.349966438107348E-4,
		"verticalSpeed" : 3.964141797041517E-6,
		"pitch" : -0.0016183496918529272,
		"bank" : -4.919025488209822E-4,
		"heading" : 239.99973584943174,
		"aileron" : 0.056396484375,
		"elevator" : -0.00323486328125,
		"rudder" : -6.103515625E-5,
		"throttle" : 0.0,
		"transponder" : "1200",
		"windSpeed" : 0.0,
		"windDirection" : 0.0,
		"visibility" : 1.0E7,
		"ambientTemperature" : 13.903240203857422,
		"ambientPressure" : 29.318899557941837,
		"barometerPressure" : 992.8489990234375
	}
}
```
- See all the ndbs: http://localhost:8080/ndbs
```json
[
	{
		"type" : "Feature",
		"geometry" : {
			"type" : "Point",
			"coordinates" : [ -74.8984444141388, 40.21271958947182 ]
		},
		"properties" : {
			"id" : "ndb_TT",
			"icao" : "TT",
			"latitude" : 40.21271958947182,
			"longitude" : -74.8984444141388,
			"altitude" : 41.75700378417969,
			"frequency" : 369000,
			"magVar" : 12.0
		}
	}, {
		"type" : "Feature",
		"geometry" : {
			"type" : "Point",
			"coordinates" : [ -73.88296946883202, 40.568133406341076 ]
		},
		"properties" : {
			"id" : "ndb_OGY",
			"icao" : "OGY",
			"latitude" : 40.568133406341076,
			"longitude" : -73.88296946883202,
			"altitude" : 3.0480000972747803,
			"frequency" : 414000,
			"magVar" : 12.0
		}
	}, {
	...
]
```
- See all the airports: http://localhost:8080/airports
```json
[
	{
		"type" : "Feature",
		"geometry" : {
			"type" : "Point",
			"coordinates" : [ -73.12616676092148, 41.16347216069698 ]
		},
		"properties" : {
			"id" : "airport_KBDR",
			"icao" : "KBDR",
			"latitude" : 41.16347216069698,
			"longitude" : -73.12616676092148,
			"altitude" : 2.743000030517578
		}
	}, {
		"type" : "Feature",
		"geometry" : {
			"type" : "Point",
			"coordinates" : [ -74.39159452915192, 41.43196128308773 ]
		},
		"properties" : {
			"id" : "airport_06N",
			"icao" : "06N",
			"latitude" : 41.43196128308773,
			"longitude" : -74.39159452915192,
			"altitude" : 159.41000366210938
		}
	}, {
	...
]
```

- Linestring traces: http://localhost:8080/aircraft_traces
```json
[
	{
		"type" : "Feature",
		"geometry" : {
			"type" : "LineString",
			"coordinates" : [ [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ], [ -73.79349589322175, 40.64148006857894 ] ]
		},
		"properties" : {
			"id" : "1168_trace"
		}
	}, {
	...
]
```

- Point traces (history): http://localhost:8080/aircraft_points (same format as individual positions)

- METAR string: http://localhost:8080/metar
```
KNYC&A46 000000Z 00000KT&D985NG 27020KT&A1958NG 27025KT&A5958NG 100KM&B-491&D3048 2CU056&CU001FNMN000N 6CI392&CI001FNMN000N 15/05 Q1013 @@@ 66 15 270 20 | 197 15 270 25 |
```

- Parsed METAR object, following [microsoft ESP specification](https://learn.microsoft.com/en-us/previous-versions/microsoft-esp/cc526983(v=msdn.10)#metar-data-format): http://localhost:8080/parsed_metar
```json
{
  "stationInfo" : {
    "ICAO" : "KNYC",
    "elevationM" : 46
  },
  "time" : {
    "iso8601" : "2023-10-22T00:00:00Z",
    "d" : 22,
    "h" : 0,
    "m" : 0,
    "code" : "000000Z"
  },
  "surfaceWind" : {
    "direction" : 0,
    "speed" : 0,
    "gustSpeed" : null,
    "units" : "KT",
    "depth" : 985,
    "turbulence" : "None",
    "windshear" : "Gradual"
  },
  "windsAloft" : [ {
    "direction" : 270,
    "speed" : 20,
    "gustSpeed" : null,
    "units" : "KT",
    "altitude" : 1958,
    "turbulence" : "None",
    "windshear" : "Gradual",
    "temperature" : null
  }, {
    "direction" : 270,
    "speed" : 25,
    "gustSpeed" : null,
    "units" : "KT",
    "altitude" : 5958,
    "turbulence" : "None",
    "windshear" : "Gradual",
    "temperature" : null
  }, {
    "altitude" : 6600,
    "temperature" : 15,
    "direction" : 270,
    "speed" : 20,
    "units" : "KT",
    "turbulence" : null,
    "windshear" : null,
    "gustSpeed" : null
  }, {
    "altitude" : 19700,
    "temperature" : 15,
    "direction" : 270,
    "speed" : 25,
    "units" : "KT",
    "turbulence" : null,
    "windshear" : null,
    "gustSpeed" : null
  } ],
  "surfaceWindsVariation" : [ ],
  "visibilities" : [ {
    "lessThanMinimum" : false,
    "distance" : 100.0,
    "units" : "KM",
    "base" : -491,
    "depth" : 3048
  } ],
  "runwayVisualRanges" : [ ],
  "presentConditions" : { },
  "clouds" : [ {
    "coverageLevel" : 2,
    "type" : "Cumulus",
    "height" : 5600,
    "top" : "Flat",
    "turbulence" : "None",
    "precipitation" : "Moderate",
    "precipitationType" : "None",
    "baseHeight" : 0,
    "icingRate" : "None"
  }, {
    "coverageLevel" : 6,
    "type" : "Cirrus",
    "height" : 39200,
    "top" : "Flat",
    "turbulence" : "None",
    "precipitation" : "Moderate",
    "precipitationType" : "None",
    "baseHeight" : 0,
    "icingRate" : "None"
  } ],
  "temperatures" : [ {
    "temperature" : 15,
    "dewpoint" : 5
  } ],
  "altimeter" : {
    "QNH" : 1013,
    "altimeterUnits" : "mb"
  },
  "unparsedTokens" : [ ]
}
```

- sim status: http://localhost:8080/status
```json
{
	"paused": true,
	"simRunningStatus": false,
	"flightFilePath": "C:\Users\marcosox\Documents\Flight Simulator X Files\default.FLT",
	"airFilePath": "C:\Program Files (x86)\Microsoft Games\Microsoft Flight Simulator X\SimObjects\Airplanes\Airbus_A321\Airbus_A321.air"
}
```

Please note that data is limited to what FSX returns, so
aircrafts only exist inside a 199km radius around user aircraft.
Same thing happens for airports and navigation aids,
but since they have a fixed position they are kept in memory forever.

### Dependencies
FSX-saas depends on jSimconnect 0.8, which is **not** included with the code.
You can download it from the [original author site](http://lc0277.gratisim.fr/jsimconnect.html)
 or from [this github repository](https://github.com/mharj/jsimconnect).

It is needed only for development. Place it in the `lib/` folder and include it as a library in your IDE.
Library inclusion in the fat jar during build is managed by maven.

## F.A.Q.

#### Q: App doesn't start (log shows `Could not connect to FSX`)
**A:** Check your [simconnect.cfg file](https://docs.microsoft.com/en-us/previous-versions/microsoft-esp/cc526983(v=msdn.10)#the-simconnectcfg-file)

#### Q: App receives data from fsx only once then stops updating. One or more endpoints (usually VORs/Waypoints) always return an empty array
**A:** One or more add-on scenery includes something that makes Jsimconnect hang.
Disable add-on sceneries and try again to find the culprit.
Unfortunately there is nothing I can do from the app since it just stops receiving data,
without showing any error.

## License
This software is released under the LGPL V3 license.
The license terms are included in the LICENSE file.

[releases page]: https://github.com/marcosox/fsx-saas/releases
