package au.gov.dhs.bom.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BOMProducts {
	public static String[] DISTRICT_FORECAST  = {		
			"IDN11020",
			"IDQ10606",
			"IDS11055",
			"IDV10750",
			"IDW13010"
	};

	public static String[] OBSERVATION_SUMMARY  = {		
			"ID*60920"
	};
	
	public static String[] CITY_FORECAST  = {
		"IDN11050", "IDD10198", "IDQ10605", "IDS10037", "IDT13630", "IDV10751", "IDW12400"
	};
	
		
	private final static Map<String, Collection<String>> productMap = new HashMap<>();
	static {
		productMap.put("districtForecast", Arrays.asList(DISTRICT_FORECAST));
		productMap.put("cityForecast", Arrays.asList(CITY_FORECAST));
		productMap.put("observationSummary", Arrays.asList(OBSERVATION_SUMMARY));
	}
	
	public static Collection<String> getProduct(String productName) {
		Collection<String> result = productMap.get(productName);
		if (result == null) {
			throw new IllegalArgumentException("Product: " + productName + " not found");
		} else {
			return result;
		}
	}
	
}
