package au.gov.dhs.bom.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import generated.AmocType;
import generated.ProductType;

// just take care of filterin all documents
public class BomClient {

	public static final String DEFAULT_BASE_URL = "ftp://ftp.bom.gov.au/anon/gen/fwo/";
	
	private final static Logger logger  = LoggerFactory.getLogger(BomClient.class);
	
	private String baseURL = "ftp://ftp.bom.gov.au/anon/gen/fwo/";
	private Set<String> products;
	private long poolingIntervalInSec;
	private Listener listener;

	private ScheduledExecutorService executor;
	private Map<String, Long> lastProductTimestamp = new HashMap<>();
		
	public interface Listener {
		public void onMsg(Document doc, AmocType amoc);
	}
	
	public BomClient(Collection<String> products, long poolingIntervalInSec, Listener listener) {
		super();
		this.products = BomUtils.resolveGlobs(products);
		this.poolingIntervalInSec = poolingIntervalInSec;
		this.listener = listener;
	}

	class RetrieveDocument implements Runnable {

		private final URL url;
		
		
		public RetrieveDocument(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				doRun();
			} catch (Throwable ex) {
				logger.warn("An unexpected exception while retrieving bom document. Will ignore and try again when sheduled", ex);
			}
		}
		
		public void doRun() {
			InputStream in = null;
			try {
				logger.info("Connecting to: " + url);
				in = url.openStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setCoalescing(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(in);
				Element docElement = doc.getDocumentElement();
				// assert product
				// build amoc 
				JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
				Unmarshaller unm = ctx.createUnmarshaller();
				JAXBElement<AmocType> amocElement = unm.unmarshal(docElement.getElementsByTagName("amoc").item(0), AmocType.class);
				AmocType amoc = amocElement.getValue();
				logger.debug("Amoc: " + amoc);
				String product = amoc.getIdentifier();
				long issuedAtTimestamp = amoc.getIssueTimeUtc().getValue().toGregorianCalendar().getTimeInMillis();
				logger.debug("Product: {} issued at: {}", product, issuedAtTimestamp);
				// get the last timestamp
				Long prevoiusTimestampAsLong =  lastProductTimestamp.get(product);
				long previousTimestamp = ( prevoiusTimestampAsLong != null) ?  prevoiusTimestampAsLong : 0L; 
				if (previousTimestamp < issuedAtTimestamp) {
					logger.info("Passing document generated as previous timestamp: {}", previousTimestamp);
					lastProductTimestamp.put(product, issuedAtTimestamp);
					listener.onMsg(doc, amoc);
				} else {
					logger.debug("Skipping document issued at: {}  as last timestamp {}",issuedAtTimestamp,previousTimestamp);
				}				
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException ex) {
				logger.error("Error while retrieving product", ex);
				throw new RuntimeException(ex);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}
	
	public void start() {
		logger.debug("Starting BomClient for products: " + products);
		executor = Executors.newSingleThreadScheduledExecutor();
		URL baseUrl;
		try {
			baseUrl = new URL(baseURL);
			for(String product:products) {
				Runnable runner = new RetrieveDocument(new URL(baseUrl, product + ".xml"));
				executor.scheduleWithFixedDelay(runner, 0, poolingIntervalInSec, TimeUnit.SECONDS);	
			}
		} catch (MalformedURLException ex) {
			logger.error("Illegal URL", ex);
			throw new RuntimeException(ex);			
		}
	}
	
	public void stop() {
		logger.debug("Stopping BomClient for products: " + products);
		executor.shutdown();
		//executor.awaitTermination(timeout, unit)
	}
	
}
