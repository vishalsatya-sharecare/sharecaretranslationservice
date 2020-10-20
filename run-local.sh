#!/usr/bin/env bash

# TODO - Change to project name if differs from folder
PROJECT_NAME=$(basename "$PWD")

echo '****************************************************'
echo "Starting up local project with name '$PROJECT_NAME'"
echo '****************************************************'

sbt test \
    universal:packageZipTarball \
    -Dsbt.boot.credentials=/secrets/.credentials \
    -Dsbt.override.build.repos=true \
    -Dsbt.repository.config=project/repositories

echo '****************************************************'
echo "Finished TEST for '$PROJECT_NAME'"
echo '****************************************************'

tar xvzf target/universal/$PROJECT_NAME.tgz

echo '****************************************************'
echo "Finished TARBALL for '$PROJECT_NAME'"
echo '****************************************************'

docker build . -t $PROJECT_NAME

echo '****************************************************'
echo "Finished BUILD for '$PROJECT_NAME'"
echo '****************************************************'

docker run \
       -p 9000:9000 \
       -it \
       -e"INSTANCE=reference" \
       -t $PROJECT_NAME

echo '****************************************************'
echo "Finished '$PROJECT_NAME'"
echo '****************************************************'
