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

@Field
        OP_MODE_VALUE = [
                0: [val: "@AC_MAIN_OPERATION_MODE_COOL_W", str: ["EN": "COOL", "KR": "냉방"]],
                1: [val: "@AC_MAIN_OPERATION_MODE_DRY_W", str: ["EN": "DRY", "KR": "제습"]],
                2: [val: "@AC_MAIN_OPERATION_MODE_FAN_W", str: ["EN": "FAN", "KR": "팬"]],
                3: [val: "@AC_MAIN_OPERATION_MODE_AI_W", str: ["EN": "AI", "KR": "인공지능"]],
                4: [val: "@AC_MAIN_OPERATION_MODE_HEAT_W", str: ["EN": "HEAT", "KR": "열"]],
                5: [val: "@AC_MAIN_OPERATION_MODE_AIRCLEAN_W", str: ["EN": "AIR CLEAN", "KR": "공기청정"]],
                6: [val: "@AC_MAIN_OPERATION_MODE_ACO_W", str: ["EN": "ACO", "KR": "ACO"]],
                7: [val: "@AC_MAIN_OPERATION_MODE_AROMA_W", str: ["EN": "AROMA", "KR": "아로마"]],
                8: [val: "@AC_MAIN_OPERATION_MODE_ENERGYSAVING_W", str: ["EN": "EVERGY SAVING", "KR": "절전"]]
        ]

@Field
        WIND_VALUE = [
                0    : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_W", str: "Slow"],
                1    : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_W", str: "Slow Low"],
                65287: [val: "@AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Auto Right"]
        ]

metadata {
    definition(name: "af ha15399", namespace: "imageafter45121", author: "obmaz", mnmn: "SmartThings", vid: "1508c046-1429-3642-b115-a805a64ec459", ocfDeviceType: 'oic.d.airconditioner') {
 		capability "Thermostat"
		capability "Thermostat Mode"
		capability "Thermostat Heating Setpoint"
		capability "Temperature Measurement"	
		capability "Thermostat Operating State"
		capability "Refresh"
		capability "Actuator"
		capability "imageafter45121.colorTemperatureMoon"

        command "coolMode"
        command "dryMode"
        command "aiMode"
        command "heatMode"
        command "airCleanMode"
        command "acoMode"
        command "aromaMode"
        command "evergySavingMode"

        command "wind", ["number"]
        command "wind1"
        command "wind2"
        command "wind3"
        command "wind4"
        command "wind5"
        command "wind6"

        command "setStatus"
        command "control", ["string", "string"]

//        attribute "mode", "string"
//        attribute "airClean", "string"
//        attribute "pm1", "number"
//        attribute "wind", "string"
//        attribute "sleepTime", "string"
//        attribute "", "string"
    }

    simulator {
    }

    preferences {
        input name: "language", title: "Select a language", type: "enum", required: true, options: ["EN", "KR"], defaultValue: "KR", description: "Language for DTH"

        input name: "powerOnValue", title: "Power On Value", type: "number", required: false, defaultValue: 257
        input name: "powerOffValue", title: "Power Off Value", type: "number", required: false, defaultValue: 0

        input name: "wind1", title: "Wind#1 Type", type: "number", required: false, defaultValue: 2056
        input name: "wind2", title: "Wind#2 Type", type: "number", required: false, defaultValue: 1286
        input name: "wind3", title: "Wind#3 Type", type: "number", required: false, defaultValue: 1028
        input name: "wind4", title: "Wind#4 Type", type: "number", required: false, defaultValue: 772
        input name: "wind5", title: "Wind#5 Type", type: "number", required: false, defaultValue: 1279
        input name: "wind6", title: "Wind#6 Type", type: "number", required: false, defaultValue: 65284
    }
}


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

            sendEvent(name: "mode", value: OP_MODE_VALUE[report["airState.opMode"]]["str"][language])
            sendEvent(name: "airConditionerMode", value: OP_MODE_VALUE[report["airState.opMode"]]["str"]["EN"])

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




def fanOn() {
    wind1()
}

def fanAuto() {
    wind2()
}

def setThermostatMode(mode) {
    log.debug "setThermostatMode " + mode
    switch (mode) {
        case "off":
            off()
            break
        case "cool":
            cool()
            break
        case "heat":
            heat()
            break
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

    sendEvent(name: "supportedThermostatFanModes", value: ["auto", "circulate", "followschedule", "on"])
    sendEvent(name: "supportedThermostatModes", value: ["auto", "cool", "heat", "off"])
}

def parse(String description) {
    log.debug "parse : $description"
}

def refresh() {
    log.debug "refresh"

    sendGetCommand("devicestate")
    updateLastTime()
}

def setCoolingSetpoint(level) {
    log.debug "setCoolingSetpoint : $level"

    if (level < 18) {
        level = 18
    } else if (level > 30) {
        level = 30
    }

    sendEvent(name: "coolingSetpoint", value: level)
    sendControlCommand("AC_FUN_TEMPSET/$level")
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
    log.debug "sendCommand : " + parent.getServerIP() + ":" + parent.getServerPort() + path
    def options = [
            "method" : "GET",
            "path"   : path,
            "headers": [
                    "HOST"        : parent.getServerIP() + ":" + parent.getServerPort(),
                    "Content-Type": "application/json"
            ],
    ]

    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: callback])
    sendHubCommand(myhubAction)
}

def callback(physicalgraph.device.HubResponse hubResponse) {
    def msg, json, status
    try {
        msg = parseLanMessage(hubResponse.description)
        def jsonObj = new JsonSlurper().parseText(msg.body)
        log.debug "callback : " + jsonObj
    } catch (e) {
        log.error "callback : Exception caught while parsing data: " + e
    }
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


def wind(val) {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.windStrength","dataValue":' + val + '}')
}

def wind1() {
    wind(wind1)
}

def wind2() {
    wind(wind2)
}

def wind3() {
    wind(wind3)
}

def wind4() {
    wind(wind4)
}

def wind5() {
    wind(wind5)
}

def wind6() {
    wind(wind6)
}


def cool() {
    makeCommand('', '{"command":"Operation","ctrlKey":"basicCtrl","dataKey":"airState.operation","dataValue":' + (powerOnValue.toString() == null ? "257" : powerOnValue.toString()) + '}')
}

def auto() {
    cool()
}

def heat() {
    makeCommand('', '{"command":"Operation","ctrlKey":"basicCtrl","dataKey":"airState.operation","dataValue":' + 4 + '}')
}

def off() {
    makeCommand('', '{"command":"Operation","ctrlKey":"basicCtrl","dataKey":"airState.operation","dataValue":' + (powerOffValue.toString() == null ? "0" : powerOffValue.toString()) + '}')
}

def airCleanOn() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.miscFuncState.airFast","dataValue":' + 1 + '}')
}

def airCleanOff() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.miscFuncState.airFast","dataValue":' + 0 + '}')
}

def coolMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 0 + '}')
}

def dryMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 1 + '}')
}

def fanMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 2 + '}')
}

def aiMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 3 + '}')
}

def heatMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 4 + '}')
}

def airCleanMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 5 + '}')
}

def acoMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 6 + '}')
}

def aromaMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 7 + '}')
}

def evergySavingMode() {
    makeCommand('', '{"command":"Set","ctrlKey":"basicCtrl","dataKey":"airState.opMode","dataValue":' + 8 + '}')
}


 */