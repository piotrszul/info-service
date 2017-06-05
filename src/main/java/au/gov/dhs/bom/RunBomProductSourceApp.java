package au.gov.dhs.bom;

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

import au.gov.dhs.bom.flume.BomProductSource;
import au.gov.dhs.bom.flume.BomProductSourceConstants;

public class RunBomProductSourceApp {

	public static void main(String[] args) throws Exception {
		
		BomProductSource source;
		MemoryChannel channel;
		
	    source = new BomProductSource();
	    channel = new MemoryChannel();
	    Configurables.configure(channel, new Context());

	    List<Channel> channels = new ArrayList<Channel>();
	    channels.add(channel);

	    ChannelSelector rcs = new ReplicatingChannelSelector();
	    rcs.setChannels(channels);

	    source.setChannelProcessor(new ChannelProcessor(rcs));		
		
		Context context = new Context();
		context.put(BomProductSourceConstants.POOLING_INTERVAL_IN_SEC, String.valueOf(30));
		//context.put(BomProductSourceConstants.PRODUCTS, "IDT60920,IDN60920,IDS60920,IDW60920,IDD60920,IDV60920,IDQ60920");
		context.put(BomProductSourceConstants.PRODUCTS, ":cityForecast");
		//context.put(BomProductSourceConstants.PRODUCTS, "ID*60920");
		//context.put(BomProductSourceConstants.DOM_DESERIALIZER, "au.gov.dhs.bom.flume.decode.JsonObservationDeserialiser$DomBuilder");
		Configurables.configure(source, context);
		source.start();

		while (true) {
			Transaction txn = channel.getTransaction();
			txn.begin();
			Event e = channel.take();
			if (e != null) {
				System.out.println("Event:");
				System.out.println(new String(e.getBody()));
			}
			txn.commit();
			txn.close();
		}
		//source.stop();
	}

}
