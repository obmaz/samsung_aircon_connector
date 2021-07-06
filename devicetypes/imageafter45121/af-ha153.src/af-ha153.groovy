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

    runEvery1Minute(refresh)
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

    sendEvent(name: "switch", value: currentState.AC_FUN_POWER.toLowerCase())
    sendEvent(name: "temperature", value: currentState.AC_FUN_TEMPNOW.toInteger(), unit: "C")
    sendEvent(name: "coolingSetpoint", value: currentState.AC_FUN_TEMPSET.toInteger(), unit: "C")
    sendEvent(name: "thermostatMode", value: "auto")
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