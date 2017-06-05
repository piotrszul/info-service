package au.gov.dhs.bom.flume.decode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.w3c.dom.Document;

import au.gov.dhs.bom.flume.ObservationJsonEncoder;
import generated.AmocType;
import generated.ProductType;
import generated.StationType;
import twitter4j.internal.org.json.JSONException;

public class JsonObservationDeserialiser implements DomEventDeserializer {

	private final Document document;
	private AmocType amoc = null;
	private ProductType product = null;
	private List<StationType> items = null;
	private int itemIndex = 0;
	private final ObservationJsonEncoder jsonEncoder = new ObservationJsonEncoder();

	public JsonObservationDeserialiser(Document document) {
		super();
		this.document = document;
	}

	private void ensureParsed() throws IOException {
		if (product == null) {
			try {
				JAXBContext ctx = JAXBContext.newInstance(ProductType.class);
				Unmarshaller unm = ctx.createUnmarshaller();
				JAXBElement<ProductType> productElement = (JAXBElement<ProductType>) unm.unmarshal(document);
				AmocType amoc = productElement.getValue().getAmoc();
				List<StationType> items = jsonEncoder.getItems(productElement.getValue());
				this.amoc = amoc;
				this.product = productElement.getValue();
				this.itemIndex = 0;
				this.items = items;
			} catch (Exception ex) {
				throw new IOException(ex);
			}
		}
	}

	@Override
	public void close() throws IOException {
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
		for (int i = 0; event != null && i < eventsToRead; i++) {
			events.add(event);
			event = readEvent();
		}
		return events;
	}

	@Override
	public void reset() throws IOException {
		itemIndex = 0;
	}

	public static class DomBuilder implements DomEventDeserializer.DomBuilder {

		@Override
		public DomEventDeserializer build(Context ctx, Document doc) {
			return new JsonObservationDeserialiser(doc);
		}
	}
}
