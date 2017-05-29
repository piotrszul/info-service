/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.gov.dhs.satori.flume;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.satori.rtm.RtmClient;
import com.satori.rtm.RtmClientAdapter;
import com.satori.rtm.RtmClientBuilder;
import com.satori.rtm.SubscriptionAdapter;
import com.satori.rtm.SubscriptionListener;
import com.satori.rtm.SubscriptionMode;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/**
 * A Flume Source, which pulls data from Twitter's streaming API. Currently,
 * this only supports pulling from the sample API, and only gets new status
 * updates.
 */
public class SatoriSource extends AbstractSource implements EventDrivenSource, Configurable {


	private static final Logger logger = LoggerFactory.getLogger(SatoriSource.class);

	/** Information necessary for accessing the Satori API */
	//private String endpoint = "wss://open-data.api.satori.com";
	//private String appkey = "2bEEc15CC97dCAEa4AB5A8fF872F5C9D";
	//private String channel = "RT-GTFS-Sydney-Public-Transport";

	
	private String endpoint;
	private String appkey;
	private String channel;

	RtmClient client = null;

	/**
	 * The initialization method for the Source. The context contains all the
	 * Flume configuration info, and can be used to retrieve any configuration
	 * values necessary to set up the Source.
	 */
	@Override
	public void configure(Context context) {
	
		endpoint = context.getString(SatoriSourceConstants.ENDPOINT, "wss://open-data.api.satori.com");
		appkey = context.getString(SatoriSourceConstants.APP_KEY);
		channel = context.getString(SatoriSourceConstants.CHANNEL);
		
		if (client == null) {
			client = new RtmClientBuilder(endpoint, appkey).setListener(new RtmClientAdapter() {
				@Override
				public void onEnterConnected(RtmClient client) {
					System.out.println("Connected to RTM!");
				}
			}).build();
		}
	}

	/**
	 * Start processing events. This uses the Twitter Streaming API to sample
	 * Twitter, and process tweets.
	 */
	@Override
	public void start() {
		// The channel is the piece of Flume that sits between the Source and
		// Sink,
		// and is used to process events.
		final ChannelProcessor channelProcessor = getChannelProcessor();
		SubscriptionListener listener = new SubscriptionAdapter() {
			@Override
			public void onSubscriptionData(SubscriptionData data) {
				final Map<String, String> headers = new HashMap<String, String>();
				for (AnyJson json : data.getMessages()) {
					System.out.println("Got message: " + json);
					logger.debug("tweet arrived");
					headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
					Event event = EventBuilder.withBody(json.toString().getBytes(), headers);
					channelProcessor.processEvent(event);
				}
			}
		};
		client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
		client.start();
		super.start();
	}

	/**
	 * Stops the Source's event processing and shuts down the Twitter stream.
	 */
	@Override
	public void stop() {
		logger.debug("Shutting down Satori sample stream...");
		client.shutdown();
		super.stop();
	}
}
