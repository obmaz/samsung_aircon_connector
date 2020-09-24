#!/bin/bash

git reset --hard
git pull
git fetch --tags
#git checkout v1.0
go mod download
go build

mkdir -p /config
cp -n ./config/config.yaml /config

pkill -9 samsung_legacy_aircon_connector
./samsung_legacy_aircon_connector
