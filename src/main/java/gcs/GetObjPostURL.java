package gcs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Throwables;
import com.google.gson.Gson;

import shared.Constants;
import shared.Utils;

public class GetObjPostURL extends HttpServlet {

	private static final long	serialVersionUID	= -2897184013542686122L;
	private static final Logger log = Logger.getLogger(GetObjPostURL.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			InputStream privateKeyStream = null;
			
				//load private key
			
				privateKeyStream = new FileInputStream(this.getServletContext().getRealPath(Constants.SERVICE_ACCOUNT_KEY_PATH));
				KeyStore ks = KeyStore.getInstance("PKCS12");
				ks.load(privateKeyStream, "notasecret".toCharArray());
				PrivateKey myOwnKey = (PrivateKey) ks.getKey("privatekey", "notasecret".toCharArray());

				//generate policy
				Policy 	policy = new Policy();
					policy
					.setExpiration()
					.setBucket(Constants.GCS_BUCKET_NAME)
					.setKey("")
					.setSuccessActionStatus("201");

				//sign the policy
				String policyJsonStr = new Gson().toJson(policy);
				String policyToSign = Base64.encodeBase64String(policyJsonStr.getBytes());
				String signature = Utils.sign(policyToSign, myOwnKey);

				//prepare data to return
				POSTobjData respToRet = new POSTobjData();
					respToRet.setMethod("POST");
					respToRet.setUrl((Utils.isGaeProduction()?Constants.PROTOCOL_S:Constants.PROTOCOL)+"://" + Constants.GCS_BUCKET_NAME + "." + Constants.GCS_API);
					respToRet.setBucket(Constants.GCS_BUCKET_NAME);
					respToRet.setPolicy(policyToSign);
					respToRet.setSignature(signature);
					respToRet.setGoogleAccessID(Constants.SERVICE_ACCOUNT_ID);

				//convert data into JSON 
				String response = new Gson().toJson(respToRet);

				//return data in JSon format
				resp.setContentType("application/json");
				resp.getWriter().print(response);
		}
		catch (Exception e) {
			log.severe("[Exception] an error occured: " + Throwables.getStackTraceAsString(e));
		}
	}
}
