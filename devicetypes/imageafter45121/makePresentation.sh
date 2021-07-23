#!/bin/bash
######################################################################################
# Smartthings CLI        : https://github.com/SmartThingsCommunity/smartthings-cli
# Capabilities Reference : https://smartthings.developer.samsung.com/docs/api-ref/capabilities.html
# Custom Capabilities    : https://smartthings.developer.samsung.com/docs/Capabilities/custom-capabilities.html
# Community              : https://community.smartthings.com/t/custom-capability-and-cli-developer-preview/197296
# Presentation check     : https://api.smartthings.com/v1/capabilities/imageafter45121.thermostatCoolingSetpoint/1/presentation
######################################################################################
# Capability is device definition
# Presentation is control definition of capability
# VID is a set of capavilites
######################################################################################
# Creating Custom Capabilities
# $smartthings capabilities:create -n imageafter45121
#
# It generates the uid like "imageafter45121.acOpMode"
#-------------------------------------------------------------------------------------
# Show capabilities
# $smartthings capabilities
#-------------------------------------------------------------------------------------
# Delete capability
# $smartthings capabilities:delete {id}
######################################################################################
# Creating Capabilities Presentations
# it might register custom capavilitiy to smartthings server
#
# $smartthings capabilities:presentation:create imageafter45121.acOpMode 1 --yaml --input=acOpMode.yaml
# $smartthings capabilities:presentation:create imageafter45121.acWindLevel 1 --yaml --input=acWindLevel.yaml
# $smartthings capabilities:presentation:create imageafter45121.acTempSet 1 --yaml --input=acTempSet.yaml
# $smartthings capabilities:presentation:create imageafter45121.acVolume 1 --yaml --input=acVolume.yaml
# $smartthings capabilities:presentation:create imageafter45121.acAutoClean 1 --yaml --input=acAutoClean.yaml
# $smartthings capabilities:presentation:create imageafter45121.acCoMode 1 --yaml --input=acCoMode.yaml
# $smartthings capabilities:presentation:create imageafter45121.acDirection 1 --yaml --input=acDirection.yaml
# $smartthings capabilities:presentation:create imageafter45121.acOperation 1 --yaml --input=acOperation.yaml
#-------------------------------------------------------------------------------------
# Update Presentation custom capability
# $smartthings capabilities:presentation:update imageafter45121.acOpMode 1 --yaml --input=acOpMode.yaml
# $smartthings capabilities:presentation:update imageafter45121.acWindLevel 1 --yaml --input=acWindLevel.yaml
# $smartthings capabilities:presentation:update imageafter45121.acTempSet 1 --yaml --input=acTempSet.yaml
# $smartthings capabilities:presentation:update imageafter45121.acVolume 1 --yaml --input=acVolume.yaml
# $smartthings capabilities:presentation:update imageafter45121.acAutoClean 1 --yaml --input=acAutoClean.yaml
# $smartthings capabilities:presentation:update imageafter45121.acCoMode 1 --yaml --input=acCoMode.yaml
# $smartthings capabilities:presentation:update imageafter45121.acDirection 1 --yaml --input=acDirection.yaml
# $smartthings capabilities:presentation:update imageafter45121.acOperation 1 --yaml --input=acOperation.yaml
######################################################################################
# Updating DTH to use Custom Capabilities
# Go to Groovy IDE and use custom capability
######################################################################################
# Generate / Post Device Configuration
# It makes the deviceConfig.yaml based on dth. The dth uid can be found on groovy ide url
# $smartthings presentation:device-config:generate f1f4018d-696d-451d-b84f-1cee0cc267b5 --dth --output=deviceConfig.yaml --yaml
######################################################################################
# Create VID
# it makes the vid based on deviceConfig.yaml
# $smartthings presentation:device-config:create --yaml --input deviceConfig.yaml
#
# It will show vid and mnmn. Please keeps vid and mnmn
#    "vid": "1508c046-1429-3642-b115-a805a64ec459",
#    "mnmn": "SmartThingsCommunity",
#
# if vid is the same as previous (it happens when the capa list is not changed), it can be changed by adding below to deviceConfig.yaml
#  type: dth
#  presentationId: {previous vid}
#  manufacturerName: SmartThingsCommunity
#  vid: {previous vid}
#  mnmn: SmartThingsCommunity
#  version: 0.0.1
######################################################################################
# Publish DTH with updated display keys
# Go to Groovy IDE and update VID in DTH
######################################################################################
