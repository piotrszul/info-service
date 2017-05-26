package au.gov.dhs.bom.flume;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.instrumentation.SourceCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;

public class BomObservationSource extends AbstractSource
	implements EventDrivenSource, Configurable {

	private static final Logger logger = LoggerFactory.getLogger(BomObservationSource.class);
	
	private SourceCounter sourceCounter;
	private ScheduledExecutorService executor;
	private EventReader reader;
	private int batchSize = 100;
	
	
	@Override
	public synchronized void configure(Context arg0) {
		// TODO Auto-generated method stub
		
		if (sourceCounter == null) {
			sourceCounter = new SourceCounter(getName());
		}
		try {
			reader = new URLEventReader(new URL("file:///Users/szu004/contract/dhs/info-services/src/test/xml/IDT60920.xml"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		
		executor = Executors.newSingleThreadScheduledExecutor();
		
	    Runnable runner = new BomServiceRunable(sourceCounter);
	    executor.scheduleWithFixedDelay(
	        runner, 0, 1000, TimeUnit.MILLISECONDS);
		
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
	      int backoffInterval = 250;
	      try {
	        while (!Thread.interrupted()) {
	          List<Event> events = reader.readEvents(batchSize);
	          if (events.isEmpty()) {
	            break;
	          }
	          sourceCounter.addToEventReceivedCount(events.size());
	          sourceCounter.incrementAppendBatchReceivedCount();

	          try {
	            getChannelProcessor().processEventBatch(events);
	          } catch (ChannelFullException ex) {
	            logger.warn("The channel is full, and cannot write data now. The " +
	                "source will try again after " + backoffInterval +
	                " milliseconds");
	            //hitChannelFullException = true;
	            //backoffInterval = waitAndGetNewBackoffInterval(backoffInterval);
	            continue;
	          } catch (ChannelException ex) {
	            logger.warn("The channel threw an exception, and cannot write data now. The " +
	                "source will try again after " + backoffInterval +
	                " milliseconds");
	            //hitChannelException = true;
	            //backoffInterval = waitAndGetNewBackoffInterval(backoffInterval);
	            continue;
	          }
	          backoffInterval = 250;
	          sourceCounter.addToEventAcceptedCount(events.size());
	          sourceCounter.incrementAppendBatchAcceptedCount();
	        }
	      } catch (Throwable t) {
	        logger.error("FATAL: " + BomObservationSource.this.toString() + ": " +
	            "Uncaught exception in SpoolDirectorySource thread. " +
	            "Restart or reconfigure Flume to continue processing.", t);
	        //hasFatalError = true;
	        Throwables.propagate(t);
	      }
	    }
	}	
}
