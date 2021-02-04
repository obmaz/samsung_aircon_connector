FROM golang:1.14 
MAINTAINER zambobmaz@gmail.com
EXPOSE 20080 

WORKDIR /app

COPY samsung_legacy_aircon_connector samsung_legacy_aircon_connector
COPY run.sh run.sh
COPY update_run.sh update_run.sh
COPY config/config.yaml config.yaml

ENTRYPOINT /app/run.sh