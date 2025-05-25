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
# $smartthings capabilities:create -n imageafter45121 --input acOpMode.yaml
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
#  $smartthings capabilities:presentation:create imageafter45121.acOpMode 1 --yaml --input=acOpMode-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acWindLevel 1 --yaml --input=acWindLevel-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acTempSet 1 --yaml --input=acTempSet-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acVolume 1 --yaml --input=acVolume-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acAutoClean 1 --yaml --input=acAutoClean-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acCoMode 1 --yaml --input=acCoMode-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acDirection 1 --yaml --input=acDirection-presentation.yaml
#  $smartthings capabilities:presentation:create imageafter45121.acOperation 1 --yaml --input=acOperation-presentation.yaml
#-------------------------------------------------------------------------------------
# Update Presentation custom capability
#  $smartthings capabilities:presentation:update imageafter45121.acOpMode 1 --yaml --input=acOpMode-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acWindLevel 1 --yaml --input=acWindLevel-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acTempSet 1 --yaml --input=acTempSet-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acVolume 1 --yaml --input=acVolume-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acAutoClean 1 --yaml --input=acAutoClean-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acCoMode 1 --yaml --input=acCoMode-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acDirection 1 --yaml --input=acDirection-presentation.yaml
#  $smartthings capabilities:presentation:update imageafter45121.acOperation 1 --yaml --input=acOperation-presentation.yaml
######################################################################################
# Updating DTH to use Custom Capabilities
# Go to Groovy IDE and use custom capability
######################################################################################
# Generate / Post Device Configuration
# It makes the lan-af-ha153-device-config.yaml based on dth. The dth uid can be found on groovy ide url
#  $smartthings presentation:device-config:generate f1f4018d-696d-451d-b84f-1cee0cc267b5 --dth --output=lan-af-ha153-device-config.yaml --yaml
######################################################################################
# Create VID
# It makes the vid based on lan-af-ha153-device-config.yaml
#  $smartthings presentation:device-config:create --yaml --input lan-af-ha153-device-config.yaml
#
# It will show vid and mnmn. Please keeps vid and mnmn
#    "vid": "1508c046-1429-3642-b115-a805a64ec459",
#    "mnmn": "SmartThingsCommunity",
#
# If vid is the same as previous (it happens when the capa list is not changed), it can be changed by adding below to lan-af-ha153-device-config.yaml
#  type: dth
#  presentationId: {previous vid}
#  manufacturerName: SmartThingsCommunity
#  vid: {previous vid}
#  mnmn: SmartThingsCommunity
#  version: 0.0.1
