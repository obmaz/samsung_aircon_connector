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

definition(
        name: "Samsung Aircon Connector",
        namespace: "obmaz",
        author: "obmaz",
        description: "Samsung Aircon Connector",
        category: "My Apps",
        iconUrl: "http://baldeagle072.github.io/icons/standard-tile@1x.png",
        iconX2Url: "http://baldeagle072.github.io/icons/standard-tile@2x.png",
        iconX3Url: "http://baldeagle072.github.io/icons/standard-tile@3x.png")

preferences {
    // If it does not use page secction, "Assign a Name" Section will appear as a default
    page(name: "page", install: true) {
        section("Samsung Aircon Connector") {
            input "dthModel", "enum", title: "Model", options: ["af ha153"]
            input "serverIP", "text", title: "Server IP", description: "192.168.0.12"
            input "serverPort", "text", title: "Server Port", description: "20080"
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    def deviceId = app.id + "_" + dthModel
    log.debug(deviceId)
    def existing = getChildDevice(deviceId)
    if (!existing) {
        def childDevice = addChildDevice("obmaz", dthModel, deviceId, null, [label: dthModel])
    }
}

def uninstalled() {
    removeChildDevices(getChildDevices())
}

private removeChildDevices(delete) {
    delete.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}