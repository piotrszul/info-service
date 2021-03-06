package au.gov.dhs.bom.flume;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.flume.ChannelException;
import org.apache.flume.ChannelFullException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.client.avro.EventReader;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;

public class BomObservationSource extends AbstractSource implements EventDrivenSource, Configurable {

	private static final Logger logger = LoggerFactory.getLogger(BomObservationSource.class);

	private SourceCounter sourceCounter;
	private ScheduledExecutorService executor;
	//private EventReader reader;

	// // configurable
	private int batchSize = 100;
	private URL resourceURL;
	private long poolingIntervalInSec = 60;
	
	@Override
	public synchronized void configure(Context context) {
		logger.info("Resource URL: {}", context.getString(BomObservationSourceConstants.RESOURCE_URL));
		try {
			resourceURL = new URL(context.getString(BomObservationSourceConstants.RESOURCE_URL));
		} catch (MalformedURLException ex) {
			logger.error("resourceUrl is not a valid url", ex);
			Throwables.propagate(ex);
		}

		poolingIntervalInSec = context.getInteger(BomObservationSourceConstants.POOLING_INTERVAL_IN_SEC, 
				BomObservationSourceConstants.DEF_POOLING_INTERVAL_IN_SEC);

		if (sourceCounter == null) {
			sourceCounter = new SourceCounter(getName());
		}

	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub

		executor = Executors.newSingleThreadScheduledExecutor();

		Runnable runner = new BomServiceRunable(sourceCounter);
		executor.scheduleWithFixedDelay(runner, 0, poolingIntervalInSec, TimeUnit.SECONDS);

		super.start();
		sourceCounter.start();
	}

	@Override
	public synchronized void stop() {
		executor.shutdown();
		try {
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			logger.info("Interrupted while awaiting termination", ex);
		}
		executor.shutdownNow();
		super.stop();
		sourceCounter.stop();
	}

	@VisibleForTesting
	SourceCounter getSourceCounter() {
		// TODO Auto-generated method stub
		return sourceCounter;
	}

	class BomServiceRunable implements Runnable {

		private final SourceCounter sourceCounter;

		public BomServiceRunable(SourceCounter sourceCounter) {
			super();
			this.sourceCounter = sourceCounter;
		}

		@Override
		public void run() {

			
			logger.info("Pooling the resource at: " + new Date());
			System.out.println("Pooling the resource at: " + new Date());
			
			EventReader reader = new URLEventReader(resourceURL);
			int backoffInterval = 250;
			try {
				while (!Thread.interrupted()) {
					List<Event> events = reader.readEvents(batchSize);
					logger.info("Got batch of size: " + events.size());
					if (events.isEmpty()) {
						reader.close();
						break;
					}
					sourceCounter.addToEventReceivedCount(events.size());
					sourceCounter.incrementAppendBatchReceivedCount();

					try {
						getChannelProcessor().processEventBatch(events);
					} catch (ChannelFullException ex) {
						logger.warn("The channel is full, and cannot write data now. The "
								+ "source will try again after " + backoffInterval + " milliseconds");
						// hitChannelFullException = true;
						// backoffInterval =
						// waitAndGetNewBackoffInterval(backoffInterval);
						continue;
					} catch (ChannelException ex) {
						logger.warn("The channel threw an exception, and cannot write data now. The "
								+ "source will try again after " + backoffInterval + " milliseconds");
						// hitChannelException = true;
						// backoffInterval =
						// waitAndGetNewBackoffInterval(backoffInterval);
						continue;
					}
					backoffInterval = 250;
					sourceCounter.addToEventAcceptedCount(events.size());
					sourceCounter.incrementAppendBatchAcceptedCount();
				}
			} catch (Throwable t) {
				logger.error("FATAL: " + BomObservationSource.this.toString() + ": "
						+ "Uncaught exception in SpoolDirectorySource thread. "
						+ "Restart or reconfigure Flume to continue processing.", t);
				// hasFatalError = true;
				Throwables.propagate(t);
			}
			System.out.println("Exiting pool at: " + new Date());
			logger.info("Exiting pool at: " + new Date());
		}		
	}
}
