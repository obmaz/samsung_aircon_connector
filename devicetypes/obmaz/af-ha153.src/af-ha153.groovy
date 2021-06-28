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
                2    : [val: "@AC_MAIN_WIND_STRENGTH_LOW_W", str: "Low"],
                3    : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_W", str: "Low Mid"],
                4    : [val: "@AC_MAIN_WIND_STRENGTH_MID_W", str: "Mid"],
                5    : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_W", str: "Mid High"],
                6    : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_W", str: "High"],
                7    : [val: "@AC_MAIN_WIND_STRENGTH_POWER_W", str: "Power"],
                8    : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_W", str: "Auto"],
                9    : [val: "@AC_MAIN_WIND_STRENGTH_LONGPOWER_W", str: "Long Power"],
                10   : [val: "@AC_MAIN_WIND_STRENGTH_SHOWER_W", str: "Shower"],
                11   : [val: "@AC_MAIN_WIND_STRENGTH_FOREST_W", str: "Forest"],
                12   : [val: "@AC_MAIN_WIND_STRENGTH_TURBO_W", str: "Turbo"],
                256  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Slow Low Left & Slow Right"],
                257  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Slow Low Left & Slow Low Right"],
                258  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Slow Low Left & Low Right"],
                259  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Slow Low Left & Low Mid Right"],
                260  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Slow Low Left & Mid Right"],
                261  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Slow Low Left & Mid High Right"],
                262  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Slow Low Left & High Right"],
                263  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Slow Low Left & Power Right"],
                264  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Slow Low Left & Auto Right"],
                511  : [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_LEFT_W", str: "Slow Low Left"],
                512  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Low Left & Slow Right"],
                513  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Low Left & Slow Low Right"],
                514  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Low Left & Low Right"],
                515  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Low Left & Low Mid Right"],
                516  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Low Left & Mid Right"],
                517  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Low Left & Mid High Right"],
                518  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Low Left & High Right"],
                519  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Low Left & Power Right"],
                520  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Low Left & Auto Right"],
                767  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_LEFT_W", str: "Low Left & Auto Right"],
                768  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Low Mid Left & Slow Right"],
                769  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Low Mid Left & Slow Low Right"],
                770  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Low Mid Left & Low Right"],
                771  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Low Mid Left & Low Mid Right"],
                772  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Low Mid Left & Mid Right"],
                773  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Low Mid Left & Mid High Right"],
                774  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Low Mid Left & High Right"],
                775  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Low Mid Left & Power Right"],
                776  : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Low Mid Left & Auto Right"],
                1023 : [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_LEFT_W", str: "Low Mid Left & Auto Right"],
                1024 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Mid Left & Slow Right"],
                1025 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Mid Left & Slow Low Right"],
                1026 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Mid Left & Low Right"],
                1027 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Mid Left & Low Mid Right"],
                1028 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Mid Left & Mid Right"],
                1029 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Mid Left & Mid High Right"],
                1030 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Mid Left & High Right"],
                1031 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Mid Left & Power Right"],
                1032 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Mid Left & Auto Right"],
                1279 : [val: "@AC_MAIN_WIND_STRENGTH_MID_LEFT_W", str: "Mid Left"],
                1280 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Mid High Left & Slow Right"],
                1281 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Mid High Left & Slow Low Right"],
                1282 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Mid High Left & Low Right"],
                1283 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Mid High Left & Low Mid Right"],
                1284 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Mid High Left & Mid Right Right"],
                1285 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Mid High Left & Mid High Right"],
                1286 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Mid High Left & High Right"],
                1287 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Mid High Left & Power Right"],
                1288 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Mid High Left & Auto Right"],
                1535 : [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_LEFT_W", str: "Mid High Left"],
                1536 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "High Left & Slow Right"],
                1537 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "High Left & Slow Low Right"],
                1538 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "High Left & Low Right"],
                1539 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "High Left & Low Mid Right"],
                1540 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "High Left & Mid Right"],
                1541 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "High Left & Mid High Right"],
                1542 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "High Left & High Right"],
                1543 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "High Left & Power Right"],
                1544 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "High Left & Auto Right"],
                1791 : [val: "@AC_MAIN_WIND_STRENGTH_HIGH_LEFT_W", str: "High Left"],
                1792 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Power Left & Slow Right"],
                1793 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Power Left & Slow Low Right"],
                1794 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Power Left & Low Right"],
                1795 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Power Left & Low Mid Right"],
                1796 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Power Left & Mid Right"],
                1797 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Power Left & Mid High Right"],
                1798 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Power Left & High Right"],
                1799 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Power Left & Power Right"],
                1800 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Power Left & Auto Right"],
                2047 : [val: "@AC_MAIN_WIND_STRENGTH_POWER_LEFT_W", str: "Power Left"],
                2048 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Auto Left & Slow Right"],
                2049 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Auto Left & Slow Low Right"],
                2050 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Auto Left & Low Right"],
                2051 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Auto Left & Low Right"],
                2052 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Auto Left & Mid Right"],
                2053 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Auto Left & Mid High Right"],
                2054 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "Auto Left & High Right"],
                2055 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_POWER_RIGHT_W", str: "Auto Left & Power Right"],
                2056 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W|AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Auto Left & Auto Right"],
                2303 : [val: "@AC_MAIN_WIND_STRENGTH_AUTO_LEFT_W", str: "Auto Left"],
                2313 : [val: "@AC_MAIN_WIND_STRENGTH_LONGPOWER_LEFT_W|AC_MAIN_WIND_STRENGTH_LONGPOWER_RIGHT_W", str: "Long Power All"],
                2570 : [val: "@AC_MAIN_WIND_STRENGTH_SHOWER_LEFT_W|AC_MAIN_WIND_STRENGTH_SHOWER_RIGHT_W", str: "Shower All"],
                2827 : [val: "@AC_MAIN_WIND_STRENGTH_FOREST_LEFT_W|AC_MAIN_WIND_STRENGTH_FOREST_RIGHT_W", str: "Forest All"],
                3084 : [val: "@AC_MAIN_WIND_STRENGTH_TURBO_LEFT_W|AC_MAIN_WIND_STRENGTH_TURBO_RIGHT_W", str: "Turbo All"],
                65280: [val: "@AC_MAIN_WIND_STRENGTH_SLOW_RIGHT_W", str: "Slow Right"],
                65281: [val: "@AC_MAIN_WIND_STRENGTH_SLOW_LOW_RIGHT_W", str: "Log Right"],
                65282: [val: "@AC_MAIN_WIND_STRENGTH_LOW_RIGHT_W", str: "Low Right"],
                65283: [val: "@AC_MAIN_WIND_STRENGTH_LOW_MID_RIGHT_W", str: "Low Mid Right"],
                65284: [val: "@AC_MAIN_WIND_STRENGTH_MID_RIGHT_W", str: "Mid Right"],
                65285: [val: "@AC_MAIN_WIND_STRENGTH_MID_HIGH_RIGHT_W", str: "Mid Hight Right"],
                65286: [val: "@AC_MAIN_WIND_STRENGTH_HIGH_RIGHT_W", str: "High Right"],
                65287: [val: "@AC_MAIN_WIND_STRENGTH_AUTO_RIGHT_W", str: "Auto Right"]
        ]

metadata {
    definition(name: "af ha153", namespace: "obmaz", author: "obmaz", mnmn: "SmartThings", vid: "SmartThings-smartthings-Z-Wave_Thermostat") {
        capability "Thermostat"
        capability "Thermostat Cooling Setpoint"
        //capability "Thermostat Heating Setpoint"
        capability "Thermostat Operating State"
        capability "Thermostat Mode"
        capability "Thermostat Fan Mode"
        capability "Relative Humidity Measurement"
        capability "Refresh"
        //capability "Dust Sensor"

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

        attribute "mode", "string"
        attribute "airClean", "string"
        attribute "pm1", "number"
        attribute "wind", "string"
        attribute "sleepTime", "string"
        attribute "windUpDown", "string"
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


def updated() {
    //최초 인스톨시? 디바이스 updated 시?
    log.debug "Updated"
    sendEvent(name: "wind1", value: WIND_VALUE[wind1]["str"])
    sendEvent(name: "wind2", value: WIND_VALUE[wind2]["str"])
    sendEvent(name: "wind3", value: WIND_VALUE[wind3]["str"])
    sendEvent(name: "wind4", value: WIND_VALUE[wind4]["str"])
    sendEvent(name: "wind5", value: WIND_VALUE[wind5]["str"])
    sendEvent(name: "wind6", value: WIND_VALUE[wind6]["str"])
}

def setInfo(String app_url, String address) {
    log.debug "${app_url}, ${address}"
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
            sendEvent(name: "wind", value: WIND_VALUE[report["airState.windStrength"]]["str"])
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

def installed() {
    log.debug "Installed"
    sendEvent(name: "supportedThermostatFanModes", value: ["auto", "circulate", "followschedule", "on"])
    sendEvent(name: "supportedThermostatModes", value: ["auto", "cool", "heat", "off"])
}

def updateLastTime() {
    log.debug "updateLastTime"
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
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





def refresh() {
    log.debug "refresh"
    setCoolingSetpoint(26)
}

def parse(String description) {
    log.debug "parse : $description"
}

def setCoolingSetpoint(level) {
    log.debug "setCoolingSetpoint : $level"

    if (level < 18 || level > 30) {
        level = 25
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
    log.debug "sendGetCommand : command"

    def path = "/get/$command"
    sendCommand(path)
}

def sendCommand(path) {
    log.debug "sendCommand : $path"
    def options = [
            "method" : "GET",
            "path"   : path,
            "headers": [
                    "HOST"        : "192.168.0.71:20080",
                    "Content-Type": "application/json"
            ],
    ]

    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

/*
/get/devicestate


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

def control(cmd, value) {
    makeCommand(cmd, value)
}

 */