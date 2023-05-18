#!/bin/bash
driverId=927b7213-86cf-423e-9ee6-006b02f7e1e3
channel=699fefe6-7b99-40b2-acfd-662ed510a84d
hub=37d997a3-7579-47f2-8ae9-804fce729f7b
hub_address=192.168.0.119

vid=$(smartthings presentation:device-config:create --yaml --input ../resource/device-config/lan-af-ha153-device-config.yaml | grep vid)
sed -e "s/vid.*/$vid/g" ./profiles/lan-af-ha153.yaml | sponge ./profiles/lan-af-ha153.yaml

smartthings edge:drivers:package ./
smartthings edge:channels:assign $driverId --channel $channel
smartthings edge:drivers:install $driverId --channel $channel --hub $hub
smartthings edge:drivers:logcat $driverId --hub-address=$hub_address