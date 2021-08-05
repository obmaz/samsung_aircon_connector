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

metadata {
    definition(name: "af ha153", namespace: "imageafter45121", author: "obmaz", mnmn: "SmartThingsCommunity", vid: "7058efc0-258b-3f62-8500-062816ef1bf7", ocfDeviceType: 'oic.d.airconditioner') {
        capability "Switch"
        capability "Temperature Measurement"
        capability "imageafter45121.acTempSet"
        capability "imageafter45121.acOpMode"
        capability "imageafter45121.acWindLevel"
        capability "imageafter45121.acDirection"
        capability "imageafter45121.acCoMode"
        capability "imageafter45121.acVolume"
        capability "imageafter45121.acOperation"
        capability "imageafter45121.acAutoClean"
        capability "Refresh"

        attribute "lastCheckin", "Date"
        }

    preferences {
        input name: "language", title: "Select a language", type: "enum", required: true, options: ["EN", "KR"], defaultValue: "KR", description: "Language for DTH"
    }
}

def updateLastTime() {
    log.debug "updateLastTime"
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def installed() {
    log.debug "installed"
}

def updated() {
    log.debug "updated"
    refresh()
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

// Switch
def on() {
    log.debug "on"
    sendCommand("/control/AC_FUN_POWER/On")
}

def off() {
    log.debug "off"
    sendCommand("/control/AC_FUN_POWER/Off")
}

// imageafter45121.acTempSet
def setAcTempSet(mode) {
    log.debug "setAcTempSet : $mode"
    sendCommand("/control/AC_FUN_TEMPSET/$mode")
}

// imageafter45121.acWindLevel
def setAcWindLevel(mode) {
    log.debug "setAcWindLevel : $mode"
    sendCommand("/control/AC_FUN_WINDLEVEL/$mode")
}

// imageafter45121.setOpMode
def setAcOpMode(mode) {
    log.debug "setOpMode : $mode"
    sendCommand("/control/AC_FUN_OPMODE/$mode")
}

// imageafter45121.acVolume
def setAcVolume(mode) {
    log.debug "setAcVolume : $mode"
    sendCommand("/control/AC_ADD_VOLUME/$mode")
}

// imageafter45121.acOperation
def setAcOperation(mode) {
    log.debug "setAcOperation : $mode"
    sendCommand("/control/AC_FUN_OPERATION/$mode")
}

// imageafter45121.acDirection
def setAcDirection(mode) {
    log.debug "setAcDirection : $mode"
    sendCommand("/control/AC_FUN_DIRECTION/$mode")
}

// imageafter45121.acCoMode
def setAcCoMode(mode) {
    log.debug "setAcCoMode : $mode"
    sendCommand("/control/AC_FUN_COMODE/$mode")
}

// imageafter45121.acAutoClean
def setAcAutoClean(mode) {
    log.debug "setAcAutoClean : $mode"
    sendCommand("/control/AC_ADD_AUTOCLEAN/$mode")
}

def sendCommand(path) {
    sendCommand(path, null)
    refresh()
}

def getServerIP() {
	return parent.getServerIP()
}

def getServerPort() {
	return parent.getServerPort()
}

def sendCommand(path, callback) {
    log.debug "sendCommand : $serverIP:$serverPort$path"
    def params = [
            "method" : "GET",
            "path"   : path,
            "headers": [
                    "HOST"        : serverIP + ":" + serverPort,
                    "Content-Type": "application/json"
            ]
    ]

    def myhubAction = new physicalgraph.device.HubAction(params, null, [callback: callback])
    sendHubCommand(myhubAction)
}

def refreshCallback(physicalgraph.device.HubResponse hubResponse) {
    log.debug "refreshCallback"
	def currentState = [:]
    
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

    updateAttribute(currentState)
}

def updateAttribute(currentState) {
    log.debug "updateAttribute"

    if (currentState.AC_ADD_AUTOCLEAN != null) {
        sendEvent(name: "acAutoClean", value: currentState.AC_ADD_AUTOCLEAN)
    }
    if (currentState.AC_ADD_VOLUME != null) {
        sendEvent(name: "acVolume", value: currentState.AC_ADD_VOLUME)
    }
    if (currentState.AC_FUN_COMODE != null) {
        sendEvent(name: "acCoMode", value: currentState.AC_FUN_COMODE)
    }
    if (currentState.AC_FUN_DIRECTION != null) {
        sendEvent(name: "acDirection", value: currentState.AC_FUN_DIRECTION)
    }
    if (currentState.AC_FUN_OPERATION != null) {
        sendEvent(name: "acOperation", value: currentState.AC_FUN_OPERATION)
    }
    if (currentState.AC_FUN_OPMODE != null) {
        sendEvent(name: "acOpMode", value: currentState.AC_FUN_OPMODE)
    }
    if (currentState.AC_FUN_POWER != null) {
        sendEvent(name: "switch", value: currentState.AC_FUN_POWER.toLowerCase())
    }
    if (currentState.AC_FUN_TEMPNOW != null) {
        sendEvent(name: "temperature", value: currentState.AC_FUN_TEMPNOW.toInteger(), unit: "C")
    }
    if (currentState.AC_FUN_TEMPSET != null) {
        sendEvent(name: "acTempSet", value: currentState.AC_FUN_TEMPSET.toInteger(), unit: "C")
    }
    if (currentState.AC_FUN_WINDLEVEL != null) {
        sendEvent(name: "acWindLevel", value: currentState.AC_FUN_WINDLEVEL)
    }
}