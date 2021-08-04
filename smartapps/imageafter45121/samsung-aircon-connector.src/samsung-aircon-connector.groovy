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

import groovy.transform.Field

@Field nameSpace = 'imageafter45121'

definition(
        name: "Samsung Aircon Connector",
        namespace: nameSpace,
        author: "obmaz",
        description: "Samsung Aircon Connector",
        category: "My Apps",
        iconUrl: "http://baldeagle072.github.io/icons/standard-tile@1x.png",
        iconX2Url: "http://baldeagle072.github.io/icons/standard-tile@2x.png",
        iconX3Url: "http://baldeagle072.github.io/icons/standard-tile@3x.png")

preferences {
    page(name: "firstPage")
}

def firstPage() {
    // If it does not use page, "location.hubs.size" will make error "java.lang.NullPointerException: Cannot get property 'physicalHubs' on null object"
    dynamicPage(name: "firstPage", title: "Setting", install: true) {
        if (location.hubs.size() < 1) {
            section() {
                paragraph "[ERROR]\nSmartThings Hub not found.\nYou need a SmartThings Hub to use Samsung Aircon Connector."
            }
            return
        }

        section("Smartthings") {
            input "devHub", "enum", title: "Hub", required: true, multiple: false, options: getHubs()
        }

        section("Connector Server") {
            input "dthModel", "enum", title: "Model", required: true, options: ["af ha153"]
            input "serverIP", "text", title: "Server IP", required: true, description: "ex) 192.168.0.71"
            input "serverPort", "text", title: "Server Port", required: true, description: "ex) 20080"
        }
        
        section("Device") {
            input "deviceName", "text", title: "Device Name", required: true, description: "ex) My Air Conditioner"
        }
    }
}

def installed() {
    log.debug "installed : ${settings}"

    initialize()
}

def updated() {
    log.debug "updated : ${settings}"

    initialize()
}

def initialize() {
    log.debug "initialize"

    def deviceId = app.id + "_" + dthModel
    def existing = getChildDevice(deviceId)

    if (!existing) {
        def childDevice = addChildDevice(nameSpace, dthModel, deviceId, getLocationID(), [label: deviceName])
    }
}

def getServerIP() {
    return settings.serverIP
}

def getServerPort() {
    return settings.serverPort
}

def getLocationID() {
    def locationID = null
    try {
        locationID = getHubID(devHub)
    } catch (err) {
    }
    return locationID
}

def getHubs() {
    def list = []
    location.getHubs().each {
        hub -> list.push(hub.name)
    }
    return list
}

def getHubID(name) {
    def id = null
    location.getHubs().each {
        hub ->
            if (hub.name == name) {
                id = hub.id
            }
    }
    return id
}

def uninstalled() {
    log.debug "uninstalled"

    removeChildDevices(getChildDevices())
}

private removeChildDevices(delete) {
    delete.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}
