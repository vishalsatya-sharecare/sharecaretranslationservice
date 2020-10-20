#!/usr/bin/env bash

echo "Start Date: $(date)"
echo $INSTANCE

# TODO - Change me to your project name
APPLICATION_NAME="dhs-audience-service"

# TODO - externalize
CRYPTO="jaredHooper"
CONF_FILE="/conf/$INSTANCE.conf"
PORT=9000

if [ ! -f $CONF_FILE ]; then
    echo -e "\n\n"
    echo '****************************************************'
    echo "The config file $CONF_FILE is missing"
    echo '****************************************************'
    echo -e "\n\n"
fi

# TODO - Update BASENAME with project name if it differs from the folder name
# TODO - Extract the $CRYPTO bit

COMMAND="bin/$APPLICATION_NAME \
     -Dhttp.port=$PORT \
     -J-server \
     -J-XX:+UseConcMarkSweepGC \
     -Dplay.crypto.secret=$CRYPTO \
     -Dplay.http.secret.key='$CRYPTO' \
     -Dconfig.file=$CONF_FILE"

echo ${COMMAND}
eval ${COMMAND}