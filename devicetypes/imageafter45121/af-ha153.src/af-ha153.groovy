/**
 *  Samsung Aircon Connector
 *
 * MIT License
 *
 * Copyright (c) 2021 zambobmaz@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import groovy.json.JsonSlurper
import groovy.transform.Field

@Field currentState = [:]

metadata {
    definition(name: "af ha153", namespace: "imageafter45121", author: "obmaz", mnmn: "SmartThings", vid: "d03db54d-7dd3-3d0a-8716-fa5ee657e561", ocfDeviceType: 'oic.d.airconditioner') {
        capability "Switch"
		capability "Temperature Measurement"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Fan Mode"
        capability "Thermostat Mode"

        capability "Refresh"
        capability "imageafter45121.colorTemperatureMoon"

        attribute "lastCheckin", "Date"
//		attribute "temperature", [string]
    }

    simulator {
    }

    preferences {
        input name: "language", title: "Select a language", type: "enum", required: true, options: ["EN", "KR"], defaultValue: "KR", description: "Language for DTH"
        input name: "powerOnValue", title: "Power On Value", type: "number", required: false, defaultValue: 257
        input name: "powerOffValue", title: "Power Off Value", type: "number", required: false, defaultValue: 0
    }
}

def updateLastTime() {
    log.debug "updateLastTime"
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def updated() {
    log.debug "updated"
    // Only can remove the enum, cannot add or modify due to "additionalProperties : false" in capavility definition
    sendEvent(name: "supportedThermostatFanModes", value: ["auto", "circulate", "followschedule", "on"])
    sendEvent(name: "supportedThermostatModes", value: ["auto", "eco", "rush our", "cool", "off"])
}

def installed() {
    log.debug "installed"
}

// To use parse when hubaction should call with DNI that is MAC of server
// It is too complicate and mess way since user should know MAC address
def parse(String description) {
    log.debug "parse"
}

def refresh() {
    log.debug "refresh"

    sendGetCommand("devicestate")
}

def fanAuto() {
    log.debug "fanAuto"
}

def fanCirculate() {
    log.debug "fanCirculate"
}

def fanOn() {
    log.debug "fanOn"
}

// Switch
def on() {
    log.debug "on"
    sendControlCommand("AC_FUN_POWER/Off")
}

def off() {
    log.debug "off"
    sendControlCommand("AC_FUN_POWER/On")
}

// Thermostat Fan Mode
def setThermostatFanMode(mode) {
    log.debug "setThermostatFanMode : $mode"
    
    switch (mode) {
        case "auto":
            fanAuto()
            break
        case "circulate":
            fanCirculate()
            break
        case "on":
            fanOn()
            break
    }
}

// Thermostat Mode
def setThermostatMode(mode) {
    log.debug "setThermostatMode : $mode"
}

// Thermostat Cooling Setpoint
def setCoolingSetpoint(setpoint) {
    log.debug "setCoolingSetpoint : $setpoint"

    if (setpoint < 18) {
        setpoint = 18
    } else if (setpoint > 30) {
        setpoint = 30
    }

    sendControlCommand("AC_FUN_TEMPSET/$setpoint")
}

def sendControlCommand(command) {
    log.debug "sendControlCommand : $command"

    def path = "/control/$command"
    sendCommand(path)
}

def sendGetCommand(command) {
    log.debug "sendGetCommand : $command"

    def path = "/get/$command"
    sendCommand(path)
}

def sendCommand(path) {
    log.debug "sendCommand : ${parent.getServerIP()} : ${parent.getServerPort()}${path}"
    def params = [
            "method" : "GET",
            "path"   : path,
            "headers": [
                    "HOST"        : parent.getServerIP() + ":" + parent.getServerPort(),
                    "Content-Type": "application/json"
            ]
    ]

    def myhubAction = new physicalgraph.device.HubAction(params, null, [callback: callback])
    sendHubCommand(myhubAction)   
}

def callback(physicalgraph.device.HubResponse hubResponse) {
    log.debug "callback"

    try {
        def msg = parseLanMessage(hubResponse.description)
        def jsonObj = new JsonSlurper().parseText(msg.body)
		def attrCount = jsonObj.data.deviceState.device.attr.size()

for (def i = 0; i < attrCount; i++) {
		   currentState[jsonObj.data.deviceState.device.attr[i].id] = jsonObj.data.deviceState.device.attr[i].value
		}
    } catch (e) {
        log.error "callback : Exception caught while parsing data: " + e
    }

	log.debug "jsonMap : $currentState"
    updateAttribute()
}

def updateAttribute() {
    log.debug "updateAttribute"

    sendEvent(name: "switch", value: currentState[AC_FUN_POWER])
    sendEvent(name: "temperature", value: currentState[AC_FUN_TEMPNOW], unit: "C")
	sendEvent(name: "coolingSetpoint", value: currentState[AC_FUN_TEMPSET], unit: "C")

	sendEvent(name: "thermostatMode", value: "cool")
  	sendEvent(name: "thermostatFanMode", value: "auto")
	
    updateLastTime()
}

/*
AC_ADD_AUTOCLEAN/On
AC_ADD_SPI/Off
AC_ADD_SPI/On
AC_FUN_COMODE/Off
AC_FUN_COMODE/Quiet
AC_FUN_COMODE/Smart
AC_FUN_DIRECTION/Fixed
AC_FUN_DIRECTION/Rotation
AC_FUN_DIRECTION/SwingUD
AC_FUN_OPMODE/Auto
AC_FUN_OPMODE/Cool
AC_FUN_OPMODE/Dry
AC_FUN_OPMODE/Wind
AC_FUN_OPMODE/Heat

	AC_FUN_POWER/Off
	AC_FUN_POWER/On
	AC_FUN_POWER/Toggle
	AC_FUN_TEMPSET/{number}
	AC_FUN_TEMPSET/Up
	AC_FUN_TEMPSET/Down
AC_FUN_WINDLEVEL/Auto
AC_FUN_WINDLEVEL/Low
AC_FUN_WINDLEVEL/Mid
*/


/*
@Field
        AIR_CLEAN_VALUE = [
                0: [val: "@AC_MAIN_AIRCLEAN_OFF_W", str: "OFF"],
                1: [val: "@AC_MAIN_AIRCLEAN_ON_W", str: "ON"]
        ]

@Field
        OPERATION_VALUE = [
                0  : [val: "@AC_MAIN_OPERATION_OFF_W", str: "OFF"],
                1  : [val: "@AC_MAIN_OPERATION_RIGHT_ON_W", str: "RIGHT ON"],
                256: [val: "@AC_MAIN_OPERATION_LEFT_ON_W", str: "LEFT ON"],
                257: [val: "@AC_MAIN_OPERATION_ALL_ON_W", str: "ALL ON"]
        ]

def setInfo(String app_url, String address) {
    log.debug "setInfo : ${app_url}, ${address}"
    state.app_url = app_url
    state.id = address
}

def setData(dataList) {
    for (data in dataList) {
        state[data.id] = data.code
    }
}

def setStatus(data) {
    setCoolingSetpoint(19)

    log.debug "setStatus Update >> ${data.key} >> ${data.data}"
    def jsonObj = new JsonSlurper().parseText(data.data)

    if (jsonObj.data.state.reported != null) {
        def report = jsonObj.data.state.reported

        if (report["airState.operation"] != null) {
            sendEvent(name: "switch", value: report["airState.operation"] == 0 ? "off" : "on")
            def thermostatMode = "cool"
            def thermostatOperatingState = "cooling"
            if (state.lastOpMode == 0) {
                thermostatMode = "cool"
                thermostatOperatingState = "cooling"
            } else if (state.lastOpMode == 4) {
                thermostatMode = "heat"
                thermostatOperatingState = "heating"
            }
            if (report["airState.operation"] == 0) {
                thermostatOperatingState = "idle"
                thermostatMode = "off"
            }

            sendEvent(name: "thermostatOperatingState", value: thermostatOperatingState)
            sendEvent(name: "thermostatMode", value: thermostatMode)

        }

        if (report["airState.opMode"] != null) {
            state.lastOpMode = report["airState.opMode"]

            //sendEvent(name: "mode", value: OP_MODE_VALUE[report["airState.opMode"]]["str"][language])
            //sendEvent(name: "airConditionerMode", value: OP_MODE_VALUE[report["airState.opMode"]]["str"]["EN"])

            if (device.currentValue("switch") == "on") {
                switch (report["airState.opMode"]) {
                    case 0:
                        sendEvent(name: "thermostatOperatingState", value: "cooling")
                        sendEvent(name: "thermostatMode", value: "cool")
                        break
                    case 4:
                        sendEvent(name: "thermostatOperatingState", value: "heating")
                        sendEvent(name: "thermostatMode", value: "heat")
                        break
                }
            } else {
                sendEvent(name: "thermostatOperatingState", value: "idle")
                sendEvent(name: "thermostatMode", value: "off")
            }
        }

        if (report["airState.tempState.current"] != null) {
            sendEvent(name: "temperature", value: report["airState.tempState.current"], unit: "C", displayed: false)
        }

        if (report["airState.tempState.target"] != null) {
            sendEvent(name: "coolingSetpoint", value: report["airState.tempState.target"])
        }

        if (report["airState.windStrength"] != null) {
            //sendEvent(name: "wind", value: WIND_VALUE[report["airState.windStrength"]]["str"])
        }
        if (report["airState.humidity.current"] != null) {
            sendEvent(name: "humidity", value: (report["airState.humidity.current"] / 10), displayed: false)
        }


        if (report["airState.quality.PM1"] != null) {
            sendEvent(name: "pm1", value: report["airState.quality.PM1"], displayed: false)
        }
        if (report["airState.quality.PM10"] != null) {
            sendEvent(name: "dustLevel", value: report["airState.quality.PM10"], displayed: false)
        }
        if (report["airState.quality.PM2"] != null) {
            sendEvent(name: "fineDustLevel", value: report["airState.quality.PM2"], displayed: false)
        }

        if (report["airState.reservation.sleepTime"] != null) {
            sendEvent(name: "sleepTime", value: report["airState.reservation.sleepTime"], displayed: false)
        }

        if (report["airState.wDir.upDown"] != null) {
            sendEvent(name: "windUpDown", value: report["airState.wDir.upDown"] == 0 ? "off" : "on", displayed: false)
        }
        if (report["airState.wDir.upDown"] != null) {
            sendEvent(name: "windUpDown", value: report["airState.wDir.upDown"] == 0 ? "off" : "on", displayed: false)
        }
    }

    sendEvent(name: "thermostatFanMode", value: "auto")
    updateLastTime();
}

*/