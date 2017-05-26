package au.gov.dhs.bom.flume;

import static org.junit.Assert.*;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BomObservationSourceTest {

	  BomObservationSource source;
	  MemoryChannel channel;

	  @Before
	  public void setUp() {
	    source = new BomObservationSource();
	    channel = new MemoryChannel();

	    Configurables.configure(channel, new Context());

	    List<Channel> channels = new ArrayList<Channel>();
	    channels.add(channel);

	    ChannelSelector rcs = new ReplicatingChannelSelector();
	    rcs.setChannels(channels);

	    source.setChannelProcessor(new ChannelProcessor(rcs));
	  }

	  @After
	  public void tearDown() {
	  }
	  
	  
	  @Test
	  public void testSomething() throws Exception {
		    Context context = new Context();
		    
		    Path  relativeIDT60920Path = Paths.get("src/test/xml/IDT60920.xml"); 
		    context.put(BomObservationSourceConstants.RESOURCE_URL, relativeIDT60920Path.toAbsolutePath().toUri().toASCIIString());
		    Configurables.configure(source, context);
		    source.start();
		    
		    while (source.getSourceCounter().getEventAcceptedCount() < 8) {
		      Thread.sleep(100);
		    }
		    Transaction txn = channel.getTransaction();
		    txn.begin();
		    Event e = channel.take();
		    System.out.println(e);
		    assertNotNull("Event must not be null", e);
		    assertNotNull("Event headers must not be null", e.getHeaders());
//		    Assert.assertNotNull(e.getHeaders().get("fileHeaderKeyTest"));
//		    Assert.assertEquals(f1.getAbsolutePath(),
//		        e.getHeaders().get("fileHeaderKeyTest"));
		    txn.commit();
		    txn.close();
		    source.stop();
	  }
	  
	  
//	  @Test
//	  public void testPutFilenameHeader() throws IOException, InterruptedException {
//	    Context context = new Context();
//	    File f1 = new File(tmpDir.getAbsolutePath() + "/file1");
//
//	    Files.write("file1line1\nfile1line2\nfile1line3\nfile1line4\n" +
//	                "file1line5\nfile1line6\nfile1line7\nfile1line8\n",
//	                f1, Charsets.UTF_8);
//
//	    context.put(SpoolDirectorySourceConfigurationConstants.SPOOL_DIRECTORY,
//	        tmpDir.getAbsolutePath());
//	    context.put(SpoolDirectorySourceConfigurationConstants.FILENAME_HEADER,
//	        "true");
//	    context.put(SpoolDirectorySourceConfigurationConstants.FILENAME_HEADER_KEY,
//	        "fileHeaderKeyTest");
//
//	    Configurables.configure(source, context);
//	    source.start();
//	    while (source.getSourceCounter().getEventAcceptedCount() < 8) {
//	      Thread.sleep(10);
//	    }
//	    Transaction txn = channel.getTransaction();
//	    txn.begin();
//	    Event e = channel.take();
//	    Assert.assertNotNull("Event must not be null", e);
//	    Assert.assertNotNull("Event headers must not be null", e.getHeaders());
//	    Assert.assertNotNull(e.getHeaders().get("fileHeaderKeyTest"));
//	    Assert.assertEquals(f1.getAbsolutePath(),
//	        e.getHeaders().get("fileHeaderKeyTest"));
//	    txn.commit();
//	    txn.close();
//	  }
}
