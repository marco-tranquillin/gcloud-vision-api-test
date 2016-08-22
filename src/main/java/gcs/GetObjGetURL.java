package gcs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.gson.Gson;

import shared.Constants;
import shared.Utils;

public class GetObjGetURL extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static final Logger log = Logger.getLogger(GetObjGetURL.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		
		InputStream privateKeyStream = null;
	    try {
	    	
	    		//load private key
	    		privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Constants.SERVICE_ACCOUNT_KEY_PATH));
	    		KeyStore 	ks = KeyStore.getInstance("PKCS12");
	    					ks.load(privateKeyStream, "notasecret".toCharArray());
	    		PrivateKey myOwnKey=(PrivateKey)ks.getKey("privatekey", "notasecret".toCharArray());
	     
	    		//prepare data to return
	    		final String HTTP_Verb="GET";
	    		final long EXPIRATION=((new java.util.Date().getTime()/1000))+(60*60); //1 hour expiration
	    		final String FILE_NAME=URLEncoder.encode(req.getParameter(Constants.GCS_INPUT_FILE),"UTF-8");
	    		String canonicalized_Resource="/"+Constants.GCS_BUCKET_NAME+"/"+FILE_NAME;
	     
	    		//write information to sign
			    String stringToSign = HTTP_Verb + "\n" +
			    		 	"\n"+
			    		 	"\n"+
			    		 	EXPIRATION + "\n" +
			    		    canonicalized_Resource;
	     
			    //sign information
			    String stringSigned = Utils.sign(stringToSign, myOwnKey);
	     
			    //prepare final URL
			    
			    GETobjData respToRet=new GETobjData();
			    	respToRet.setUrl((Utils.isGaeProduction()?Constants.PROTOCOL_S:Constants.PROTOCOL)+"://"+Constants.GCS_BUCKET_NAME+"." + Constants.GCS_API);
			    	respToRet.setGoogleAccessId(Constants.SERVICE_ACCOUNT_ID);
			    	respToRet.setExpiration(""+EXPIRATION);
			    	respToRet.setSignature(URLEncoder.encode(stringSigned,"UTF-8"));
			    
			    String response = new Gson().toJson(respToRet);
			    
			    //return final URL
			    resp.setContentType("application/json");
			    resp.getWriter().print(response);
	    }
	    catch(Exception e){
	    	e.printStackTrace();
			log.severe("[Exception] an error occured: " + Throwables.getStackTraceAsString(e));
	    }
		
	}

}
