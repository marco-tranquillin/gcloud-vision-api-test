package gcs;

public class GETobjData{
	 
	 private String url;
	 private String googleAccessId;
	 private String expiration;
	 private String signature;
	 
	 
	 public GETobjData(){
		 ;
	 }
	 
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	

	/**
	 * @return the googleAccessId
	 */
	public String getGoogleAccessId() {
		return googleAccessId;
	}

	/**
	 * @param googleAccessId the googleAccessId to set
	 */
	public void setGoogleAccessId(String googleAccessId) {
		this.googleAccessId = googleAccessId;
	}

	/**
	 * @return the expiration
	 */
	public String getExpiration() {
		return expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}
	


	
}
