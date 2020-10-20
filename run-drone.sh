#!/usr/bin/env bash

yes | cp -rf .ivy2/* /root/.ivy2/
yes | cp -rf .sbt/* /root/.sbt/
mkdir -p $HOME/.sbt/0.13/plugins/
cp /secrets/credentials.sbt $HOME/.sbt/0.13/plugins/plugins.sbt
cp /secrets/.credentials $HOME/.ivy2/
apk update
apk add --no-cache libstdc++