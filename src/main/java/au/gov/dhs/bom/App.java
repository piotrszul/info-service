package au.gov.dhs.bom;

import com.satori.rtm.*;
import com.satori.rtm.model.*;
import java.util.concurrent.*;

public class App {
  static final String endpoint = "wss://open-data.api.satori.com";
  static final String appkey = "2bEEc15CC97dCAEa4AB5A8fF872F5C9D";
  static final String channel = "RT-GTFS-Sydney-Public-Transport";

  public static void main(String[] args) throws Exception {
    final RtmClient client = new RtmClientBuilder(endpoint, appkey)
        .setListener(new RtmClientAdapter() {
          @Override
          public void onEnterConnected(RtmClient client) {
            System.out.println("Connected to RTM!");
          }
        })
        .build();

    final CountDownLatch success = new CountDownLatch(1);

    SubscriptionListener listener = new SubscriptionAdapter() {
      @Override
      public void onSubscriptionData(SubscriptionData data) {
        for (AnyJson json : data.getMessages()) {
          System.out.println("Got message: " + json);
        }
        success.countDown();
      }
    };

    client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

    client.start();

    success.await(15, TimeUnit.SECONDS);

    client.shutdown();
  }
}