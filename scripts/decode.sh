#!/bin/sh

printf "%s" "$FIREBASE_CREDENTIAL" > ./firebase-admin-credentials.json

printf "%s" "$PLAY_CREDENTIAL" > ./google-pk.json

printf 'GITHUB_ACTOR="%s"\n' "$USERNAME" >> ./local.properties

printf 'GITHUB_TOKEN="%s"\n' "$TOKEN" >> ./local.properties

printf 'REVENUECAT_ANDROID_API_KEY="%s"\n' "$REVENUECATANDROID" >> ./local.properties

printf 'REVENUECAT_IOS_API_KEY="%s"\n' "$REVENUECATIOS" >> ./local.properties

printf 'INFURA_SECRET_KEY="%s"\n' "$INFURASECRET" >> ./local.properties

printf 'INFURA_API_KEY="%s"\n' "$INFURAAPI" >> ./local.properties

printf 'COVALENTHQ_API_KEY="%s"\n' "$COVALENTAPI" >> ./local.properties

printf 'ALCHEMY_API_KEY="%s"\n' "$ALCHEMYAPI" >> ./local.properties

printf 'MORALIS_API_KEY="%s"\n' "$MORALISAPI" >> ./local.properties