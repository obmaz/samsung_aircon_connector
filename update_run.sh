#!/bin/bash

git reset --hard origin/master
git checkout master
git pull
git fetch --tags

go mod download
go build

pkill -9 samsung_legacy_aircon_connector
./samsung_legacy_aircon_connector
