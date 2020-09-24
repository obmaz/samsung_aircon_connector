FROM golang:1.14 
MAINTAINER zambobmaz@gmail.com

RUN apt-get update
RUN apt-get install -y vim

WORKDIR /app
RUN git clone https://github.com/obmaz/samsung_legacy_aircon_connector.git

WORKDIR /app/samsung_legacy_aircon_connector

EXPOSE 20080

CMD ["sh","./update_run.sh"]
