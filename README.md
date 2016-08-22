# Google Cloud Vision API - TEST

This is an application to test Google Cloud Vision API (https://cloud.google.com/vision/).
The application makes use of Google App Engine as backend so you should be confident with Google AppEngine standard JAVA environment (https://cloud.google.com/appengine/docs/java/) 

The application can be:
- tested locally
- deployed in a GAE environment

**Preliminary steps**
- setup Google Cloud Platform project (https://cloud.google.com/vision/docs/quickstart)
- enable Cloud Vision API (https://cloud.google.com/vision/docs/quickstart)
- enable Billing (https://cloud.google.com/vision/docs/quickstart)
- create a Google Cloud Storage bucket (https://cloud.google.com/vision/docs/quickstart)
- create a P12 Service Account key (https://cloud.google.com/vision/docs/common/auth#using-api-manager)

Once prepared the environment it is necessary to edit the **Constants.java** file:
- SERVICE_ACCOUNT_ID: the generated service account id
- GCS_BUCKET_NAME: the name of Google Cloud Storage bucket where all file will be uploaded

Do not forget to update the **POM.xml** file adding reference to your Google Cloud Platform project and to include your P12 Service Account key file under the folder **src/main/webapp/Key** (the file has to have the name **ServiceAccountPK.p12**)

**How to execute the program locally**

From command line execute following command:
```sh
$ mvn clean appengine:devserver
```

**How to deployt the solution**

From command line execute following command:
```sh
$ mvn clean appengine:update
```