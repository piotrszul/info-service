package au.gov.dhs.bom.flume;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.serialization.EventDeserializer;
import org.apache.flume.serialization.ResettableInputStream;

import com.google.common.base.Throwables;

import generated.AmocType;
import generated.ProductType;
import generated.StationType;
import twitter4j.internal.org.json.JSONException;

public class JsonEventDeserialiser implements EventDeserializer {

	private final InputStream reader;
	private AmocType amoc = null;
	private ProductType doc = null;
	private List<StationType> items = null;
	private int itemIndex = 0;
	private final ObservationJsonEncoder jsonEncoder = new ObservationJsonEncoder();

	public JsonEventDeserialiser(InputStream reader) {
		super();
		this.reader = reader;
	}

	private void ensureParsed() throws IOException {
		if (doc == null) {
			try {				
				JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
				Unmarshaller unm = ctx.createUnmarshaller();
				JAXBElement<ProductType> productElement = (JAXBElement<ProductType>) unm
						.unmarshal(reader);
				AmocType amoc = productElement.getValue().getAmoc();
				List<StationType> items = jsonEncoder.getItems(productElement.getValue());
				this.amoc = amoc;
				this.doc = productElement.getValue();
				this.itemIndex = 0;
				this.items = items;
			} catch (Exception ex) {
				throw new IOException(ex);
			}
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public void mark() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Event readEvent() throws IOException {
		ensureParsed();
		if (itemIndex < items.size()) {
			try {
				return EventBuilder.withBody(jsonEncoder.encode(amoc, items.get(itemIndex++)).toString().getBytes());
			} catch (JSONException ex) {
				throw new IOException(ex);
			}
		} else {
			return null;
		}
	}

	@Override
	public List<Event> readEvents(int eventsToRead) throws IOException {
		List<Event> events = new ArrayList<Event>(eventsToRead);
		Event event = readEvent();
		for(int i = 0; event != null && i< eventsToRead; i++) {
			events.add(event);
			event = readEvent();
		}
		return events;
	}

	@Override
	public void reset() throws IOException {
		itemIndex = 0;
	}

	static class InputStreamWrapper extends InputStream {

		final ResettableInputStream ris;
		
		public InputStreamWrapper(ResettableInputStream ris) {
			this.ris = ris;
		}
		
		@Override
		public int read() throws IOException {
			return ris.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return ris.read(b, off, len);
		}


		@Override
		public void mark(int limit) {
			try {
				ris.mark();
			} catch (IOException ex) {
				Throwables.propagate(ex);
			}
		}

		@Override
		public void reset() throws IOException {
			ris.reset();
		}

		@Override
		public void close() throws IOException {
			ris.close();
		}

		@Override
		public boolean markSupported() {
			// TODO Auto-generated method stub
			return false;
		}
				
	}
	
	public static class Builder implements EventDeserializer.Builder {

		@Override
		public EventDeserializer build(Context context, final ResettableInputStream in) {
			return new JsonEventDeserialiser(new InputStreamWrapper(in));
		}
		
	}
}
