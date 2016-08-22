package gcs;


import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.common.base.Throwables;
import com.google.gson.Gson;

import shared.Constants;
import shared.Utils;

public class PutObjACL extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3720092520877413070L;
	private static final Logger log = Logger.getLogger(PutObjACL.class.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try{
			//return response
			RESTresponse response=new RESTresponse(Constants.HTTP_STATUS_501);
			resp.setContentType("application/json");
			resp.getWriter().print(new Gson().toJson(response));
		}
		catch(Exception e){
			e.printStackTrace();
			log.severe("[Exception] an error occured: " + Throwables.getStackTraceAsString(e));
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			// define HTTP_TRANSPORT and JSON_FACTORY
			final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
			final JsonFactory JSON_FACTORY = new JacksonFactory();
						
			//load private key
			InputStream privateKeyStream = null;
						privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Constants.SERVICE_ACCOUNT_KEY_PATH));
			KeyStore ks = KeyStore.getInstance("PKCS12");
					 ks.load(privateKeyStream, "notasecret".toCharArray());
			PrivateKey myOwnKey=(PrivateKey)ks.getKey("privatekey", "notasecret".toCharArray());
			log.info("[Authorization] Service Account private key loaded");
	     
			//create OAuth 2.0 credentials based on PrivateKey 
		    GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
	             .setJsonFactory(JSON_FACTORY)
	             .setServiceAccountId(Constants.SERVICE_ACCOUNT_ID)
	             .setServiceAccountScopes(Arrays.asList(Constants.GCS_SCOPE))
	             .setServiceAccountPrivateKey(myOwnKey)
	             .build();
		    log.info("[Authorization] credentials built");
		    
		    //get access token
		    credential.refreshToken();
		    String accessToken=credential.getAccessToken();
	     
		    //get input file name
		    String fileName=URLEncoder.encode(req.getParameter(Constants.GCS_INPUT_FILE),"UTF-8");
		   
		    //prepare URL to get access to the Clod Storage API
			URL myURL=new URL((Utils.isGaeProduction()?Constants.PROTOCOL_S:Constants.PROTOCOL)+"://"+Constants.GCS_BUCKET_NAME+"."+Constants.GCS_API+"/"+fileName+"?acl");
		    HTTPRequest put = new HTTPRequest(myURL,HTTPMethod.PUT);
		    	//set headers
		    	put.addHeader(new HTTPHeader("Host", Constants.GCS_BUCKET_NAME+"."+Constants.GCS_API));			
		    	put.addHeader(new HTTPHeader("Authorization","OAuth " + accessToken));
  			    put.addHeader(new HTTPHeader("Content-Length", ""+Constants.GCS_ACL_SERVICE_ACCOUNT_READ.length()));
  			    put.addHeader(new HTTPHeader("Content-Type", "application/xml; charset=UTF-8"));
  			    put.addHeader(new HTTPHeader("Date", Utils.getCurrentDate()));
		    	put.addHeader(new HTTPHeader("x-goog-api-version", Constants.GCS_REST_API_VERSION));
		    	put.setPayload(Constants.GCS_ACL_SERVICE_ACCOUNT_READ.getBytes());
		    	
		    //call API using URLFetch
		    URLFetchService restService = URLFetchServiceFactory.getURLFetchService();
		    HTTPResponse restResponse = restService.fetch(put);
		    
		    //return response in HTML mode
		    resp.setContentType("text/html");
		    resp.getWriter().print(restResponse.getResponseCode());
				
		}
		catch(Exception e){
			e.printStackTrace();
			log.severe("[Exception] an error occured: " + Throwables.getStackTraceAsString(e));
		}
		
	}
}
