FROM golang:1.14 
MAINTAINER zambobmaz@gmail.com
EXPOSE 20080 

#RUN apt-get update
#RUN apt-get install -y vim

WORKDIR /app
# RUN git clone https://github.com/obmaz/samsung_legacy_aircon_connector.git
# WORKDIR /app/samsung_legacy_aircon_connector
# RUN git fetch --tags
# RUN git checkout v1.2.0
# RUN go mod download
# RUN go build
COPY samsung_legacy_aircon_connector /app/samsung_legacy_aircon_connector
COPY run.sh run.sh
COPY update_run.sh update_run.sh
COPY config/config.yaml config.yaml
#COPY config/config.yaml /config/config.yaml

ENTRYPOINT /app/run.sh