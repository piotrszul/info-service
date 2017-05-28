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

import au.gov.dhs.satori.flume.SatoriSource;

public class RunSatoriSourceApp {

	public static void main(String[] args) throws Exception {

		SatoriSource source;
		MemoryChannel channel;

		source = new SatoriSource();
		channel = new MemoryChannel();
		Configurables.configure(channel, new Context());

		List<Channel> channels = new ArrayList<Channel>();
		channels.add(channel);

		ChannelSelector rcs = new ReplicatingChannelSelector();
		rcs.setChannels(channels);

		source.setChannelProcessor(new ChannelProcessor(rcs));

		Context context = new Context();
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
		// source.stop();
	}

}
