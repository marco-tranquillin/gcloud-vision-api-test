package cloud.vision.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageSource;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import shared.Constants;

public class GetImageInformation extends HttpServlet {

	private static final long serialVersionUID = -5143522695817249653L;
	private static final Logger LOG = Logger.getLogger(GetImageInformation.class.getName());
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String APPLICATION_NAME = "Cloud Vision API - TEST";
	private static final String PARAM_MAX_RESULTS = "maxResults";
	private static final String PARAM_IMAGE_NAME = "imageName";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			// Get parameters
			Map<String, String[]> parameterMap = (Map<String, String[]>) req.getParameterMap();
			if (parameterMap.get(PARAM_MAX_RESULTS) == null) {
				String msg = "Parameter " + PARAM_MAX_RESULTS + " was not present.";
				LOG.severe(msg);
				resp.getWriter().println(msg);
				return;
			}
			int maxResults = Integer.parseInt(parameterMap.get(PARAM_MAX_RESULTS)[0]);

			if (parameterMap.get(PARAM_IMAGE_NAME) == null) {
				String msg = "Parameter " + PARAM_IMAGE_NAME + " was not present.";
				LOG.severe(msg);
				resp.getWriter().println(msg);
				return;
			}
			String imageName = parameterMap.get(PARAM_IMAGE_NAME)[0];
			String gcsURI = "gs://" + Constants.GCS_BUCKET_NAME + "/" + imageName;

			// load private key
			InputStream privateKeyStream = null;
			privateKeyStream = new FileInputStream(
					this.getServletContext().getRealPath(Constants.SERVICE_ACCOUNT_KEY_PATH));
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(privateKeyStream, "notasecret".toCharArray());
			PrivateKey myOwnKey = (PrivateKey) ks.getKey("privatekey", "notasecret".toCharArray());

			// Create Google Credential and add scopes
			// create OAuth 2.0 credentials based on PrivateKey
			GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
					.setJsonFactory(JSON_FACTORY).setServiceAccountId(Constants.SERVICE_ACCOUNT_ID)
					.setServiceAccountScopes(Arrays.asList(Constants.GCS_SCOPE)).setServiceAccountPrivateKey(myOwnKey)
					.build();

			credential = credential.createScoped(VisionScopes.all());

			// Create Vision Object
			Vision visionService = new Vision.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();

			// Define Image to be elaborated
			ImageSource imageSource = new ImageSource();
			imageSource.setGcsImageUri(gcsURI);
			Image image = new Image();
			image.setSource(imageSource);
			// Prepare request

			AnnotateImageRequest request = new AnnotateImageRequest().setImage(image)
					.setFeatures(this.getAllFatures(maxResults));

			Vision.Images.Annotate annotateRequest = visionService.images()
					.annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));

			// Execute request
			BatchAnnotateImagesResponse response = annotateRequest.execute();

			// Write response
			resp.setContentType("application/json");
			resp.getWriter().println(response.toPrettyString());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("[Exception] an error occured: " + Throwables.getStackTraceAsString(e));

		}
	}

	/**
	 * Get all features available in CLOUD VISION API
	 * 
	 * @param maxResults
	 *            max number of results that you want to return
	 * @return
	 */
	private List<Feature> getAllFatures(int maxResults) {
		List<Feature> features = Lists.newArrayList();

		Feature labelDetection = new Feature();
		labelDetection.setType("LABEL_DETECTION");
		labelDetection.setMaxResults(maxResults);
		Feature textDetection = new Feature();
		textDetection.setType("TEXT_DETECTION");
		textDetection.setMaxResults(maxResults);
		Feature faceDetection = new Feature();
		faceDetection.setType("FACE_DETECTION");
		faceDetection.setMaxResults(maxResults);
		Feature landmarkDetection = new Feature();
		landmarkDetection.setType("LANDMARK_DETECTION");
		landmarkDetection.setMaxResults(maxResults);
		Feature logoDetection = new Feature();
		logoDetection.setType("LOGO_DETECTION");
		logoDetection.setMaxResults(maxResults);
		Feature safeSearchDetection = new Feature();
		safeSearchDetection.setType("SAFE_SEARCH_DETECTION");
		safeSearchDetection.setMaxResults(maxResults);
		Feature imagePropertiesDetection = new Feature();
		imagePropertiesDetection.setType("IMAGE_PROPERTIES");
		imagePropertiesDetection.setMaxResults(maxResults);

		features.add(labelDetection);
		features.add(textDetection);
		features.add(faceDetection);
		features.add(landmarkDetection);
		features.add(logoDetection);
		features.add(safeSearchDetection);
		features.add(imagePropertiesDetection);

		return features;

	}
}
