#!/usr/bin/env bash
set -o nounset  # Throw error if uninitialized variable is used
set -o errexit  # Fail on any error

DIR=$(dirname "$0") # Get the directory this script is in
VERSION="1.0"

# TODO - Change to project name if differs from folder name
PROJECT_NAME=$(basename "$PWD")

SEPARATOR='********************************************************************************************************'

echo "$SEPARATOR"
echo "VARIABLES: [DIR: $DIR] [BASENAME: $PROJECT_NAME] [VERSION: $VERSION]"
echo "$SEPARATOR"

if [ "$DIR" == "" ]; then
    echo "$SEPARATOR"
    echo "'DIR' is empty. Can't continue."
    echo "$SEPARATOR"
    exit 1;
fi


function main {

    cp ~/.ivy2/.credentials .

    echo "$SEPARATOR"
    echo "1. docker build -t $PROJECT_NAME-deps-cache:latest -f $DIR/DepCacheDockerfile ."
    echo "$SEPARATOR"

    docker build -t "$PROJECT_NAME-deps-cache:latest" -f "$DIR/DepCacheDockerfile" .

    echo "$SEPARATOR"
    echo "2. docker tag $PROJECT_NAME-deps-cache:latest nexus.admin.sharecare.com/$PROJECT_NAME-deps-cache:$VERSION"
    echo "$SEPARATOR"

    docker tag "$PROJECT_NAME-deps-cache:latest" "nexus.admin.sharecare.com/$PROJECT_NAME-deps-cache:$VERSION"

    echo "$SEPARATOR"
    echo "3. docker push nexus.admin.sharecare.com/$PROJECT_NAME-deps-cache:$VERSION"
    echo "$SEPARATOR"

    docker push "nexus.admin.sharecare.com/$PROJECT_NAME-deps-cache:$VERSION"

    echo "$SEPARATOR"
    echo "(Main) Remove .credentials file"
    echo "$SEPARATOR"

    rm -f .credentials
}

function finish {
    echo "$SEPARATOR"
    echo "(Finish) Remove .credentials file"
    echo "$SEPARATOR"

    rm -f .credentials
}

trap finish EXIT
main