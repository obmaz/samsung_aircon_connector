FROM ubuntu:20.04
MAINTAINER zambobmaz@gmail.com
EXPOSE 20080

WORKDIR /app

COPY bin/samsung_aircon_connector samsung_aircon_connector
COPY run.sh run.sh
COPY config/config.yaml config.yaml

RUN chmod +x run.sh
RUN chmod +x samsung_aircon_connector

ENTRYPOINT /app/run.sh
