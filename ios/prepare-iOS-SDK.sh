#!/bin/sh
REPO_NAME="Gesturedeck-iOS"
FRAMEWORK_NAME="GesturedeckiOS.xcframework"

#!/bin/sh
rm -fR ${REPO_NAME}
mkdir ${REPO_NAME}
git clone https://github.com/navideck/${REPO_NAME}
# git -C ${REPO_NAME} checkout ###
find ./${REPO_NAME} -mindepth 1 -maxdepth 1 -not -name ${FRAMEWORK_NAME} -exec rm -rf '{}' \;   # Keep only the xcframework folder
