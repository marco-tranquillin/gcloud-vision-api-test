package shared;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.appengine.api.utils.SystemProperty;

public class Utils{
	 
	/**
	 * Signs the input data with the private key.
	 * 
	 * @param input
	 *            data
	 * @return data signed
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(String policy, PrivateKey key)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException, UnsupportedEncodingException {
		Signature signer = Signature.getInstance("SHA256withRSA");
		signer.initSign(key);
		signer.update(policy.getBytes("UTF-8"));
		return new String(org.apache.commons.codec.binary.Base64.encodeBase64(
				signer.sign(), false), "UTF-8");
	}
	
	/**
	 * Get current date in Google API format (EEE, dd MMM yyyy HH:mm:ss zzz)
	 * 
	 * @return String that represent date in following format EEE, dd MMM yyyy
	 *         HH:mm:ss zzz
	 */
	public static String getCurrentDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(new Date());
		Date currentDate = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(currentDate);
	}
	
	/**
	 * Check if the app is running in production or in local dev
	 * @return true if it is in production, false otherwise
	 */
	public static boolean isGaeProduction(){
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}
	
	/**
	 * Check if the running environment is the dev
	 * @return true if it is the dev, false otherwise
	 */
	public static boolean isGaeDevEnvironment(){
		if(SystemProperty.applicationId.get() != null)
			return SystemProperty.applicationId.get().contains("dev");
		return true;
	}
	
}
