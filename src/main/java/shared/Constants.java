package shared;

public class Constants {

	/**
	 * OAuth 2.0 Keys
	 */
	public static final String SERVICE_ACCOUNT_ID="YOUR_SERVICE_ACCOUNT_KEY";
	public static final String SERVICE_ACCOUNT_KEY_PATH = "Key/ServiceAccountPK.p12";

	/**
	 * Google Cloud Storage
	 */
	public static final String GCS_SCOPE = "https://www.googleapis.com/auth/devstorage.full_control";
	public static final String GCS_INPUT_FILE = "inputFile";
	public static final String GCS_API = "storage.googleapis.com";
	public static final String GCS_BUCKET_NAME = "YOUR_GOOGLE_CLOUD_STORAGE_BUCKET_NAME";
	public static final String GCS_ACL_SERVICE_ACCOUNT_READ = "<AccessControlList><Entries><Entry><Scope type=\"UserByEmail\"><EmailAddress>"+SERVICE_ACCOUNT_ID+"</EmailAddress><Name>Service Account</Name></Scope><Permission>FULL_CONTROL</Permission></Entry></Entries></AccessControlList>";
	public static final String GCS_REST_API_VERSION = "2";

	/**
	 * HTTP
	 */
	public static final String PROTOCOL_S = "https";
	public static final String PROTOCOL = "http";
	public static final String HTTP_STATUS_200 = "200 OK";
	public static final String HTTP_STATUS_204 = "204 No content";
	public static final String HTTP_STATUS_501 = "501 Method not recognized";
	public static final String HTTP_STATUS_401 = "401 Unauthorized";

}
