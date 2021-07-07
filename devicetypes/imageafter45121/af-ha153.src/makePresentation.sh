######################################################################################
# Smartthings CLI        : https://github.com/SmartThingsCommunity/smartthings-cli
# Capabilities Reference : https://smartthings.developer.samsung.com/docs/api-ref/capabilities.html
# Community              : https://community.smartthings.com/t/custom-capability-and-cli-developer-preview/197296
# Presentation check     : https://api.smartthings.com/v1/capabilities/imageafter45121.coolingSetpoint/1/presentation
######################################################################################
# Custom Capability  : Create Capability -> Create Presentation
# Custom VID         : Create deviceConfig.json -> Create VID -> Updata VID to DTH
#  Capability is device definition
#  Presentation is control definition of capability
#  VID is a set of capavilites
######################################################################################
# Create custom capabilitiy
# It generates the uid like "imageafter45121.coolingSetpoint"
# ./smartthings capabilities:create -n imageafter45121
#-------------------------------------------------------------------------------------
# Show capabilities
# ./smartthings capabilities
#-------------------------------------------------------------------------------------
# Delete capability
# ./smartthings capabilities:delete {id}
######################################################################################
# Create Presentation custom capability
# it might register custom capavilitiy to smartthings server
# ./smartthings capabilities:presentation:create imageafter45121.thermostatCoolingSetpoint 1 -j -i=coolingSetpoint.json
#-------------------------------------------------------------------------------------
# Update Presentation custom capability
# ./smartthings capabilities:presentation:update imageafter45121.thermostatCoolingSetpoint 1 -j -i=coolingSetpoint.json
######################################################################################
# Create deviceConfig.json
# The dth uid can be found on groovy ide url / af ha153 dth key : b2bb3390-53d6-4f21-9637-99df925e52fe
# It makes the deviceConfig.json based on dth
#./smartthings presentation:device-config:generate b2bb3390-53d6-4f21-9637-99df925e52fe --dth -o=deviceConfig.json -j
######################################################################################
# Create VID
# it makes the vid based on deviceConfig.json
# It will show vid and mnmn. Please keeps vid and mnmn
#    "vid": "1508c046-1429-3642-b115-a805a64ec459",
#    "mnmn": "SmartThingsCommunity",
# ./smartthings presentation:device-config:create -j -i deviceConfig.json
######################################################################################
# Update DTH
# Use mnmn and vid to DTH
######################################################################################
