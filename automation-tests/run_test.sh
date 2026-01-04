#!/bin/bash

# Set environment variables
export ANDROID_HOME=~/Library/Android/sdk
export ANDROID_SDK_ROOT=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Navigate to the project directory
cd ~/AndroidStudioProjects/mangala-wallet/automation-tests

# Run the test connection script
node pin_screen_tests.js
