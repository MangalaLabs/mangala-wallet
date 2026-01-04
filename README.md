# Building project

1. Clone the project
2. Initialize submodules `git submodule update --init --recursive`
3. Get [Infura API Key and Secret key](https://docs.infura.io/api/getting-started#2-configure-your-api-key) and [Covalent](https://www.covalenthq.com/docs/unified-api/setup/) API Key
4. Create Github Personal Access Token with *read:packages* permission
5. Set values in `local.properties`: 
```
   GITHUB_ACTOR=[github username]
   GITHUB_TOKEN=[github personal access token created in step 4]
     
   ALCHEMY_API_KEY=[your Alchemy API key]
   COVALENTHQ_API_KEY=[your CovalentHQ API key]
   INFURA_API_KEY=[Infura API Key]
   INFURA_SECRET_KEY=[Infura Secret Key]

   REVENUECAT_ANDROID_API_KEY=[some foo empty string]
   REVENUECAT_IOS_API_KEY=[some foo empty string]
```

# Libraries used
- Paging: [Multiplatform Paging](https://github.com/cashapp/multiplatform-paging/tree/main-3.2.0-alpha05)
- kotlinx-serialization
- kotlinx-datetime

# Git branches

- `develop` - Development branch (for Firebase App Distribution builds)
- `uat` - Internal release branch (for Google Play internal)
- `staging` - Closed alpha release branch (for Google Play closed alpha)
- `master` - Public release branch (for Google Play Store)

# Build variants

## Overview
There are 3 app variants: Cold, Pro and Ui
- Cold: Wallet with transaction signing capability, intended to be use on a separate, air-gapped device (not connected to the Internet) for security
- UI: Wallet that can send transaction. Intended to be used in combination with Cold. Cold and UI can be used on the same device and can communicate with each other (planned feature) 
- Pro: Combination of Cold and Ui in one app

## Changing build variants

**Prerequisite for all platforms**
Change the value `currentFlavor` in `gradle.properties` to your desired build variant. Please remember to not commit this change to Git

**Android**
Set build variant in Android Studio

**iOS**
Select scheme in Xcode

**Desktop**
1. There's an additional value `desktopBuildType` in `gradle.properties`. Change this value to your desired build type (debug or release)
2. Run task `generateBuildKonfig` in `:common:utils` module to update the values

## Editing the prepopulated database

1. Make the changes you need in `./common/mokoresources/src/commonMain/moko-resources/files/`
2. Verify if you have sqlite3 command line tool by running `sqlite3 --version` in terminal. If you see a version number, you have sqlite3 installed
3. If not, install sqlite3 command line tool `brew install sqlite3` on Mac or `sudo apt-get install sqlite3` on Linux
4. cd into the `./common/mokoresources/src/commonMain/moko-resources/files/` folder
5. Run this command `for file in *.sql; do 
    sqlite3 mangalawallet.db < "$file"
done`
6. Be sure to add a new migration for the change in `data/local/src/commonMain/sqldelight/migrations` folder

## Common build problems and fixes
1. Execution failed for task ':composeApp:podInstallSyntheticIos'
   Run `./gradlew :composeApp:podInstallSyntheticIos` in terminal
2. The /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/ld
   command returned non-zero exit code: 1.
   output:
   ld: framework not found FirebaseMessaging
   Run `./gradlew :libraries:kmpnotifier:podInstallSyntheticIos`
3. Building iOS and getting error `Missing package product Kingfisher, Missing package product OHHTTPStubsSwift, ...`
   Go to XCode and open File -> Packages -> Reset Package Caches and rebuild
4. Cannot find `strcmp` in scope
   Modify the StatementAuthorizer file like this https://github.com/groue/GRDB.swift/commit/fcfdab2f11df7dd08e77bcd769ece6cb9f1261e4

## Useful commands

- Generating Kotlin file for DB after editing .sq file `./gradlew generateCommonMainAntelopeDatabaseInterface`