#!/bin/bash

sudo docker run -d -it --restart always -p 20080:20080 --name samsung_aircon_connector -v /volume1/docker/samsung_legacy_aircon_connector/config:/config:cached zambobmaz/samsung_legacy_aircon_connector:1.2.0