package au.gov.dhs.bom.flume;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.annotations.VisibleForTesting;

import au.gov.dhs.bom.api.BomClient;
import au.gov.dhs.bom.flume.decode.DomEventDeserializer;
import au.gov.dhs.bom.flume.decode.DomEventDeserializerFactory;
import au.gov.dhs.bom.flume.decode.XMLDomEventDeserializer;
import generated.AmocType;

public class BomProductSource extends AbstractSource implements EventDrivenSource, Configurable {

	private static final Logger logger = LoggerFactory.getLogger(BomProductSource.class);

	private SourceCounter sourceCounter;
	private BomClient bomClient;
	// private EventReader reader;

	// // configurable
	private String domDeserializerType;
	private int batchSize = 100;
	private URL baseURL;
	private long poolingIntervalInSec = 60;
	List<String> products;

	@Override
	public synchronized void configure(Context context) {
		String baseUrlAsString = context.getString(BomProductSourceConstants.BASE_URL,
				BomProductSourceConstants.DEFAULT_BASE_URL);
		logger.info("BASE URL: {}", baseUrlAsString);
		try {
			baseURL = new URL(baseUrlAsString);
		} catch (MalformedURLException ex) {
			logger.error("resourceUrl is not a valid url", ex);
			throw new RuntimeException(ex);
		}

		domDeserializerType = context.getString(BomProductSourceConstants.DOM_DESERIALIZER,
				BomProductSourceConstants.DEFAULT_DOM_DESERIALIZER);

		poolingIntervalInSec = context.getInteger(BomProductSourceConstants.POOLING_INTERVAL_IN_SEC,
				BomObservationSourceConstants.DEF_POOLING_INTERVAL_IN_SEC);

		products = new ArrayList<String>();
		for (String productItem : context.getString(BomProductSourceConstants.PRODUCTS, "").split(",")) {
			products.add(productItem.trim());
		}

		if (sourceCounter == null) {
			sourceCounter = new SourceCounter(getName());
		}

		if (bomClient == null) {
			bomClient = new BomClient(products, poolingIntervalInSec, new BomClientListener(sourceCounter));
		}
	}

	@Override
	public synchronized void start() {
		logger.debug("Starting ...");
		super.start();
		bomClient.start();
		sourceCounter.start();
	}

	@Override
	public synchronized void stop() {
		logger.debug("Stopping ...");
		bomClient.stop();
		sourceCounter.stop();
		super.stop();
	}

	@VisibleForTesting
	SourceCounter getSourceCounter() {
		// TODO Auto-generated method stub
		return sourceCounter;
	}

	class BomClientListener implements BomClient.Listener {

		private final SourceCounter sourceCounter;

		public BomClientListener(SourceCounter sourceCounter) {
			super();
			this.sourceCounter = sourceCounter;
		}

		@Override
		public void onMsg(Document doc, AmocType amoc) {
			logger.debug("Reveived amoc: {}", amoc);
			try {
				DomEventDeserializer domDeserializer = DomEventDeserializerFactory.newInstance(domDeserializerType,
						null, doc);
				logger.debug("Deserializer: {}", domDeserializer);
				List<Event> events = domDeserializer.readEvents(batchSize);
				while (!events.isEmpty()) {
					sourceCounter.addToEventReceivedCount(events.size());
					sourceCounter.incrementAppendBatchReceivedCount();
					logger.info("Processing batch of size {} for: {}", events.size(),
							amoc.getIdentifier() + " - " + amoc.getIssueTimeLocal());
					getChannelProcessor().processEventBatch(events);
					logger.info("Done with batch of size {} for: {}", events.size(),
							amoc.getIdentifier() + " - " + amoc.getIssueTimeLocal());
					sourceCounter.addToEventAcceptedCount(events.size());
					sourceCounter.incrementAppendBatchAcceptedCount();
					events = domDeserializer.readEvents(batchSize);
				}
			} catch (IOException ex) {
				logger.error("Error while sending messgaes", ex);
				throw new RuntimeException(ex);
			}
		}
	}
}
