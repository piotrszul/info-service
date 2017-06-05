package au.gov.dhs.bom.flume;

import au.gov.dhs.bom.api.BomClient;
import au.gov.dhs.bom.flume.decode.XMLDomEventDeserializer;

public class BomProductSourceConstants {

	public static final String BASE_URL = "baseUrl";
	public static final String DEFAULT_BASE_URL = BomClient.DEFAULT_BASE_URL;
	
	public static final int DEF_POOLING_INTERVAL_IN_SEC = 60;
	public static final String POOLING_INTERVAL_IN_SEC = "poolingIntervalInSec";
	public static final String PRODUCTS = "products";

	public static final String DOM_DESERIALIZER = "domDeserializer";
	public static final String DEFAULT_DOM_DESERIALIZER = XMLDomEventDeserializer.DomBuilder.class.getName();
}
