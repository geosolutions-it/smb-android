# smb-app
SaveMyBike mobile app

## Setup of GOOGLE_API_KEY

### Beta

Replace in `app/src/beta/res/values/google_maps_api.xml` `YOUR_KEY_HERE` with the correct key (Starts with AIza...)

### Debug

Replace in `app/src/debug/res/values/google_maps_api.xml` `YOUR_KEY_HERE` with the correct key (Starts with AIza...)

## Development

Configure the application as following to setup to work with dev resources.

 - `app/src/main/java/it/geosolutions/savemybike/data/Constants.java`: change PORTAL_ENDPOINT and UPLOAD_RESOURCE to related dev URLs:

```
    public final static String PORTAL_ENDPOINT = "https://dev.savemybike.geo-solutions.it/"; // DEV
    public final static String UPLOAD_RESOURCE = "upload-dev/"; // DEV
```

 - `app/src/main/res/raw/auth_config.json`: change discovery_uri as following

 ```
 discovery_uri": "https://dev.savemybike.geo-solutions.it/auth/realms/save-my-bike/.well-known/openid-configuration",
 ```

  - app/google-services.json (download it from firebase console: setting --> General --> your applications)

