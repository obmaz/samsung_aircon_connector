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
# It generates the uid like "imageafter45121.coolingSetpoint"
# ./smartthings capabilities:create -n imageafter45121
#-------------------------------------------------------------------------------------
# Show capabilities
# ./smartthings capabilities
#-------------------------------------------------------------------------------------
# Delete capability
# ./smartthings capabilities:delete {id}
######################################################################################
# Creating Capabilities Presentations
# it might register custom capavilitiy to smartthings server
# ./smartthings capabilities:presentation:create imageafter45121.thermostatCoolingSetpoint 1 -j --input=thermostatCoolingSetpoint.json
# ./smartthings capabilities:presentation:create imageafter45121.thermostatFanMode 1 -j --input=thermostatFanMode.json
# ./smartthings capabilities:presentation:create imageafter45121.thermostatMode 1 -j --input=thermostatMode.json
# ./smartthings capabilities:presentation:create imageafter45121.muteMode 1 -j --input=muteMode.json

#-------------------------------------------------------------------------------------
# Update Presentation custom capability
# ./smartthings capabilities:presentation:update imageafter45121.thermostatCoolingSetpoint 1 -j --input=thermostatCoolingSetpoint.json
# ./smartthings capabilities:presentation:update imageafter45121.thermostatFanMode 1 -j --input=thermostatFanMode.json
# ./smartthings capabilities:presentation:update imageafter45121.thermostatMode 1 -j --input=thermostatMode.json
# ./smartthings capabilities:presentation:update imageafter45121.muteMode 1 -j --input=muteMode.json
######################################################################################
# Updating DTH to use Custom Capabilities
# Go to Groovy IDE and use custom capability
######################################################################################
# Generate / Post Device Configuration
# The dth uid can be found on groovy ide url / af ha153 dth key : 604b033e-3a68-494f-8871-bccf303f7a3c
# It makes the deviceConfig.json based on dth
# ./smartthings presentation:device-config:generate 604b033e-3a68-494f-8871-bccf303f7a3c --dth --output=deviceConfig2.json -j
######################################################################################
# Create VID
# it makes the vid based on deviceConfig.json
# It will show vid and mnmn. Please keeps vid and mnmn
#    "vid": "1508c046-1429-3642-b115-a805a64ec459",
#    "mnmn": "SmartThingsCommunity",
# ./smartthings presentation:device-config:create -j --input deviceConfig.json
######################################################################################
# Publish DTH with updated display keys
# Go to Groovy IDE and update VID in DTH
######################################################################################
