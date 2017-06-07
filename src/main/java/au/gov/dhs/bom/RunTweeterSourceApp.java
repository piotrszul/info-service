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

import au.com.cloudera.flume.source.TwitterSource;

public class RunTweeterSourceApp {

	public static void main(String[] args) throws Exception {

		TwitterSource source;
		MemoryChannel channel;

		source = new TwitterSource();
		channel = new MemoryChannel();
		Configurables.configure(channel, new Context());

		List<Channel> channels = new ArrayList<Channel>();
		channels.add(channel);

		ChannelSelector rcs = new ReplicatingChannelSelector();
		rcs.setChannels(channels);

		source.setChannelProcessor(new ChannelProcessor(rcs));

		Context context = new Context();
		context.put("consumerKey", "uolKIPc83edYjlAeE0QiJjsdx");
		context.put("consumerSecret", "SSoC4i6WLZLwh9vQubIkMESJN36yAzRqmGlv3vcDdVsRdyTSO4");
		context.put("accessToken", "764727998-7nnqWp8vff6hhQT7V6IvklWVrKLVT8rPojQGAvnZ");
		context.put("accessTokenSecret", "wLzUFsNsjK6euuUTiYv4wLXELaZLzaya3EcvO9AxaIsC4");
		context.put("keywords", "tutorials point,java, bigdata, mapreduce, mahout, hbase, nosql");
		
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
