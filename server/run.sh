#!/bin/bash

cp -n /app/config/config.yaml /config/config.yaml
/app/samsung_aircon_connector -docker
