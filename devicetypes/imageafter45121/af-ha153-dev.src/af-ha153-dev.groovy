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

@Field Map currentState = [:]

metadata {
    definition(name: "af ha153 dev", namespace: "imageafter45121", author: "obmaz", mnmn: "SmartThingsCommunity", vid: "66b8f0e7-f6d0-32e8-9b5d-6c2be91368ca", ocfDeviceType: 'oic.d.airconditioner') {
        capability "Switch"
        capability "Temperature Measurement"
        capability "Thermostat Cooling Setpoint"
        capability "imageafter45121.thermostatFanMode"

        capability "Thermostat Mode"
        capability "Refresh"

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

}

def installed() {
    log.debug "installed"

    // Only can remove the enum, cannot add or modify due to "additionalProperties : false" in capavility definition
    sendEvent(name: "supportedThermostatFanModes", value: ["auto", "circulate", "followschedule", "on"])
    sendEvent(name: "supportedThermostatModes", value: ["auto", "eco", "rush our", "cool", "off"])

    runEvery1Minute(refresh)
}

// To use parse when hubaction should call with DNI that is MAC of server
// It is too complicate and mess way since user should know MAC address
def parse(String description) {
    log.debug "parse"
}

def refresh() {
    log.debug "refresh"
    sendCommand("/get/devicestate", refreshCallback)
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
    sendCommand("/control/AC_FUN_POWER/On")
}

def off() {
    log.debug "off"
    sendCommand("/control/AC_FUN_POWER/Off")
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

    sendCommand("/control/AC_FUN_TEMPSET/$setpoint")
}

def sendCommand(path) {
    sendCommand(path, null)
    refresh()
}

def sendCommand(path, callback) {
    log.debug "sendCommand : ${parent.getServerIP()}:${parent.getServerPort()}${path}"
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

def refreshCallback(physicalgraph.device.HubResponse hubResponse) {
    log.debug "refreshCallback"

    try {
        def msg = parseLanMessage(hubResponse.description)
        def jsonObj = new JsonSlurper().parseText(msg.body)
        def attrCount = jsonObj.data.deviceState.device.attr.size()

        for (def i = 0; i < attrCount; i++) {
            currentState[jsonObj.data.deviceState.device.attr[i].id] = jsonObj.data.deviceState.device.attr[i].value
        }
    } catch (e) {
        log.error "refreshCallback : Exception caught while parsing data: " + e
    }

    updateAttribute()
}

def updateAttribute() {
    log.debug "updateAttribute"

    if (currentState.AC_FUN_COMODE != null) {
        sendEvent(name: "thermostatMode", value: "auto")
    }
    if (currentState.AC_FUN_DIRECTION != null) {
        sendEvent(name: "thermostatFanMode", value: "auto")
    }
    if (currentState.AC_FUN_OPERATION != null) {

    }
    if (currentState.AC_FUN_OPMODE != null) {

    }
    if (currentState.AC_FUN_POWER != null) {
        sendEvent(name: "switch", value: currentState.AC_FUN_POWER.toLowerCase())
    }
    if (currentState.AC_FUN_TEMPSET != null) {
        sendEvent(name: "coolingSetpoint", value: currentState.AC_FUN_TEMPSET.toInteger(), unit: "C")
    }
    if (currentState.AC_FUN_TEMPNOW != null) {
        sendEvent(name: "temperature", value: currentState.AC_FUN_TEMPNOW.toInteger(), unit: "C")
    }
    if (currentState.AC_FUN_WINDLEVEL != null) {

    }
    if (currentState.AC_ADD_AUTOCLEAN != null) {

    }
    if (currentState.AC_ADD_VOLUME != null) {

    }
}

/*
AC_FUN_COMODE/Off
AC_FUN_COMODE/Quiet //Silent Mode
AC_FUN_COMODE/Smart //Smart Sensor Mode based on Camera
AC_FUN_COMODE/Speed //Temporary Speed up
AC_FUN_DIRECTION/Off
AC_FUN_DIRECTION/Center
AC_FUN_DIRECTION/Wide
AC_FUN_DIRECTION/Long
AC_FUN_DIRECTION/Left
AC_FUN_DIRECTION/Right
AC_FUN_OPERATION/Family
AC_FUN_OPERATION/Solo
AC_FUN_OPMODE/Auto
AC_FUN_OPMODE/Wind
AC_FUN_OPMODE/Cool
AC_FUN_OPMODE/Dry
AC_FUN_OPMODE/DryClean
AC_FUN_OPMODE/CoolClean
AC_FUN_POWER/Off
AC_FUN_POWER/On
AC_FUN_POWER/Toggle
AC_FUN_TEMPSET/{number}
AC_FUN_TEMPSET/Up
AC_FUN_TEMPSET/Down
AC_FUN_WINDLEVEL/Auto
AC_FUN_WINDLEVEL/Mid
AC_FUN_WINDLEVEL/High
AC_FUN_WINDLEVEL/Turbo

AC_ADD_AUTOCLEAN/On
AC_ADD_AUTOCLEAN/Off
AC_ADD_SMARTON/Off // Lock off Official App
AC_ADD_SMARTON/On  // Lock on Official App
AC_ADD_VOLUME/Mute
AC_ADD_VOLUME/33
AC_ADD_VOLUME/66
AC_ADD_VOLUME/100
*/

/*
{
  "timestamp": "Wed, 05 Aug 2020 17:28:03 +0900",
  "command": "Devicestate",
  "status": "Success",
  "data": {
    "type": "DeviceState",
    "status": "Okay",
    "deviceState": {
      "device": {
        "duid": "30144A125XXX",
        "groupId": "AC",
        "modelId": "AC",
        "attr": [
          {
            "id": "AC_FUN_ENABLE",
            "type": "RW",
            "value": "Enable"
          },
          {
            "id": "AC_FUN_POWER",
            "type": "RW",
            "value": "On"
          },
          {
            "id": "AC_FUN_OPERATION",
            "type": "RW",
            "value": "Family"
          },
          {
            "id": "AC_FUN_OPMODE",
            "type": "RW",
            "value": "Dry"
          },
          {
            "id": "AC_FUN_COMODE",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_FUN_WINDLEVEL",
            "type": "RW",
            "value": "Auto"
          },
          {
            "id": "AC_FUN_DIRECTION",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_FUN_TEMPSET",
            "type": "RW",
            "value": "25"
          },
          {
            "id": "AC_FUN_TEMPNOW",
            "type": "R",
            "value": "26"
          },
          {
            "id": "AC_FUN_ONTIMER",
            "type": "RW",
            "value": "0"
          },
          {
            "id": "AC_FUN_OFFTIMER",
            "type": "RW",
            "value": "0"
          },
          {
            "id": "AC_FUN_SLEEP",
            "type": "RW",
            "value": "0"
          },
          {
            "id": "AC_FUN_ERROR",
            "type": "R",
            "value": "00000000"
          },
          {
            "id": "AC_ADD_AUTOCLEAN",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_ADD_STERILIZE",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_ADD_HUMIDI",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_ADD_PANEL",
            "type": "RW",
            "value": "Close"
          },
          {
            "id": "AC_ADD_LIGHT",
            "type": "RW",
            "value": "On"
          },
          {
            "id": "AC_ADD_SMARTON",
            "type": "RW",
            "value": "Off"
          },
          {
            "id": "AC_ADD_WEATHER",
            "type": "W",
            "value": "35"
          },
          {
            "id": "AC_ADD_VOLUME",
            "type": "RW",
            "value": "Mute"
          },
          {
            "id": "AC_ADD_SETKWH",
            "type": "RW",
            "value": "0"
          },
          {
            "id": "AC_ADD_STOP_WIFI",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_ADD2_MACHIGH",
            "type": "W",
            "value": "4608"
          },
          {
            "id": "AC_ADD2_MACMID",
            "type": "W",
            "value": "24320"
          },
          {
            "id": "AC_ADD2_MACLOW",
            "type": "W",
            "value": "56320"
          },
          {
            "id": "AC_ADD2_USEDWATT",
            "type": "R",
            "value": "1"
          },
          {
            "id": "AC_ADD2_WIFI",
            "type": "w",
            "value": "Connected"
          },
          {
            "id": "AC_ADD2_INTERNET",
            "type": "w",
            "value": "Connected"
          },
          {
            "id": "AC_TODAY_TEMP",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TODAY_PREC",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TODAY_WEATHER",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_NOW_HUM",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_NOW_SANDY",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TOMO_MIN",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TOMO_MAX",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TOMO_PREC",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_TOMO_WEATHER",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_WEATHER_NET",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_WEATHER_LOC",
            "type": "W",
            "value": "0"
          },
          {
            "id": "AC_ADD_START_WIFI",
            "type": "RW",
            "value": "Default"
          },
          {
            "id": "AC_ADD2_MONITORING",
            "type": "w",
            "value": "NotMonitoring"
          },
          {
            "id": "AC_ADD2_OPTIONCODE",
            "type": "w",
            "value": "27175"
          },
          {
            "id": "AC_ADD2_TESTMODE",
            "type": "w",
            "value": "0"
          },
          {
            "id": "AC_ADD2_WATT_NOTICE",
            "type": "w",
            "value": "0"
          }
        ]
      }
    }
  }
}

*/