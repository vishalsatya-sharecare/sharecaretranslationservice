#!/usr/bin/env bash

sbt clean coverage test coverageReport
open ./target/scala-2.11/scoverage-report/index.html