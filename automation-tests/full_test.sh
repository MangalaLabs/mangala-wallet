#!/bin/bash

# Full test script with Appium server startup
# This script will set up environment variables, start Appium server, and run tests

echo "Setting up environment variables..."
export ANDROID_HOME=~/Library/Android/sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin

echo "Environment variables set:"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"

echo "Checking if Appium is already running..."
APPIUM_PID=$(pgrep -f appium)
if [ ! -z "$APPIUM_PID" ]; then
    echo "Stopping existing Appium process ($APPIUM_PID)..."
    kill $APPIUM_PID
    sleep 2
fi

echo "Starting Appium server..."
appium --address 127.0.0.1 --port 4723 &
APPIUM_PID=$!
echo "Appium started with PID: $APPIUM_PID"

# Wait for Appium to be ready
echo "Waiting for Appium to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:4723/status > /dev/null; then
        echo "Appium is ready!"
        break
    fi
    echo "Waiting for Appium... attempt $i/30"
    sleep 1
done

# Run tests
echo "Running tests..."
mocha pin_screen_tests.js --timeout 60000

# Store the exit code
TEST_EXIT_CODE=$?

# Clean up
echo "Stopping Appium server..."
kill $APPIUM_PID
wait $APPIUM_PID 2>/dev/null

# Exit with the test's exit code
exit $TEST_EXIT_CODE
