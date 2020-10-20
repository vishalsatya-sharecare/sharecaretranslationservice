#!/usr/bin/env bash

cp /etc/config/app/dhs-audience-service-lockerbox.properties conf/lockerbox.properties && sbt clean run