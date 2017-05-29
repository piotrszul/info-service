package au.gov.dhs.bom.flume;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.flume.Event;
import org.apache.flume.client.avro.EventReader;

public class URLEventReader implements EventReader {
	
	private final URL sourceUrl;
	private XMLEventDeserializer deserialiser = null;

	public URLEventReader(URL sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	private void ensureDeserialiser() throws IOException {
		if (deserialiser == null) {
			deserialiser = new XMLEventDeserializer(sourceUrl.openStream());
		}
	}
	
	@Override
	public Event readEvent() throws IOException {
		ensureDeserialiser();
		return deserialiser.readEvent();
	}

	@Override
	public List<Event> readEvents(int n) throws IOException {
		ensureDeserialiser();
		return deserialiser.readEvents(n);
	}

	@Override
	public void close() throws IOException {
		if (deserialiser != null) {
			deserialiser.close();
		}
	}
	
}
