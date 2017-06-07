package au.gov.dhs.morphlines;

import java.util.Map.Entry;

import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;

import com.google.common.base.Charsets;

import au.gov.dhs.nlp.NLPSentimentAnalysis;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class Morphlines {
	
	private final static NLPSentimentAnalysis SENTIMENT_ANALYZER = new NLPSentimentAnalysis();
	
	public static void toJson(Record record, Logger logger) {
        JSONObject json = new JSONObject();
        for (Entry<String, Object> field:record.getFields().entries()) {
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
	}


	public static void addSentiment(Record record, String textField, Logger logger) {
		
		Object obj = record.getFirstValue(textField);
		String text = obj != null ? obj.toString() : null;
		logger.info("Analyzig setiment for : " + text);
		record.put("sentiment",SENTIMENT_ANALYZER.findSentiment(text));
	}	
}
