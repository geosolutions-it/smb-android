# smb-app
SaveMyBike mobile app

## Setup of GOOGLE_API_KEY and firebase secrets

To setup the application you have to setup the following resources:
- app/google-services.json (download it from firebase console: setting --> General --> your applications)
- `google_maps_api.xml`, depending on your build environment ( TODO: externalize also this )

### Beta

Replace in `app/src/beta/res/values/google_maps_api.xml` `YOUR_KEY_HERE` with the correct key (Starts with AIza...)

### Debug

Replace in `app/src/debug/res/values/google_maps_api.xml` `YOUR_KEY_HERE` with the correct key (Starts with AIza...)

## Development

From next commits after 0.9-beta the constants of the dev and production are managed directly in build.gradle. Resources like `auth_config.json` are managed as build resources (`app/src/main/res...` vs `app/rsc/debug/res`)



