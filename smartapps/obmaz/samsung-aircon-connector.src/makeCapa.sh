# Create custom capabilitiy
# It generates the uid like "imageafter45121.colorTemperatureMoon"
# ./smartthings capabilities:create

# Register custom capability
# it might register custom capavilitiy to smartthings server
# ./smartthings capabilities:presentation:create imageafter45121.colorTemperatureMoon 1 -y -i=colorTemperature.json

# Create deviceConfig.json
# The dth uid can be found on groovy ide url / af ha153 dth key : 86e1ea6e-d44a-4737-b721-c4d547f65fee
# It makes the deviceConfig.json based on dth
#./smartthings presentation:device-config:generate 86e1ea6e-d44a-4737-b721-c4d547f65fee --dth -o=deviceConfig.json -j


# Create vid
# it makes the vid based on deviceConfig.json
# It will show vid and mnmn. Please keeps vid and mnmn
#    "vid": "1508c046-1429-3642-b115-a805a64ec459",
#    "mnmn": "SmartThingsCommunity",
# Note that vid is a set of capavilites
# ./smartthings presentation:device-config:create -j -i deviceConfig.json

# Update DTH
# Use mnmn and vid to DTH
