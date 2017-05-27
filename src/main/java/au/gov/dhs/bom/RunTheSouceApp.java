package au.gov.dhs.bom;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.apache.flume.serialization.EventDeserializerFactory;

import au.gov.dhs.bom.flume.BomObservationSource;
import au.gov.dhs.bom.flume.BomObservationSourceConstants;
import au.gov.dhs.bom.flume.XMLEventDeserializer;

public class RunTheSouceApp {

	public static void main(String[] args) throws Exception {

		Object o = EventDeserializerFactory.getInstance(
				"au.gov.dhs.bom.flume.XMLEventDeserializer$Builder", null, null);
		
		BomObservationSource source;
		MemoryChannel channel;
		
	    source = new BomObservationSource();
	    channel = new MemoryChannel();
	    Configurables.configure(channel, new Context());

	    List<Channel> channels = new ArrayList<Channel>();
	    channels.add(channel);

	    ChannelSelector rcs = new ReplicatingChannelSelector();
	    rcs.setChannels(channels);

	    source.setChannelProcessor(new ChannelProcessor(rcs));		
		
		
		Context context = new Context();
		Path relativeIDT60920Path = Paths.get("src/test/xml/IDT60920.xml");
		context.put(BomObservationSourceConstants.RESOURCE_URL,
				relativeIDT60920Path.toAbsolutePath().toUri().toASCIIString());
		context.put(BomObservationSourceConstants.POOLING_INTERVAL_IN_SEC, String.valueOf(5));
		Configurables.configure(source, context);
		source.start();

		while (true) {
			Transaction txn = channel.getTransaction();
			txn.begin();
			Event e = channel.take();
			System.out.println(e);
			txn.commit();
			txn.close();
		}
		//source.stop();
	}

}
