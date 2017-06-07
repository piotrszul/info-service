package au.gov.dhs.morphlines;

import java.util.Map.Entry;

import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import au.gov.dhs.nlp.NLPSentimentAnalysis;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class Morphlines {

	private final static Logger LOGGER = LoggerFactory.getLogger(Morphlines.class);

	private static NLPSentimentAnalysis sentimentAnalyser = null;
	static {
		try {
			LOGGER.info("Cladd loader: " + NLPSentimentAnalysis.class.getClassLoader());
			sentimentAnalyser = new NLPSentimentAnalysis();

			LOGGER.info("NLPSentimentAnalysis ready");
		} catch (Throwable ex) {
			LOGGER.warn("Error while initializing NLPSentimentAnalysis. Will be disabled", ex);
		}
	}

	public static void toJson(Record record, Logger logger) {
		try {
			JSONObject json = new JSONObject();
			for (Entry<String, Object> field : record.getFields().entries()) {
				try {
					json.put(field.getKey(), field.getValue());
				} catch (JSONException ex) {
					// cannot add field
					logger.warn("Cannot include field: " + field.getKey(), ex);
				}
			}
			record.put(Fields.ATTACHMENT_BODY, json.toString().getBytes(Charsets.UTF_8));
			record.put(Fields.ATTACHMENT_MIME_TYPE, "application/json");
			record.put(Fields.ATTACHMENT_CHARSET, Charsets.UTF_8.name());
		} catch (Throwable ex) {
			LOGGER.warn("Exception converting to JSON. Not added", ex);
		}
	}

	public static void addSentiment(Record record, String textField, Logger logger) {
		if (sentimentAnalyser != null) {

			try {
				Object obj = record.getFirstValue(textField);
				String text = obj != null ? obj.toString() : null;
				int sentment = sentimentAnalyser.findSentiment(text);
				record.put("sentiment", sentment);
				record.put("sentiment_code", String.valueOf(sentment));
			} catch (Throwable ex) {
				LOGGER.warn("Exception while analyzing sentiment. Not added", ex);
			}
		} else {
			LOGGER.info("Sentiment analysis disabled noop");
		}
	}
}
