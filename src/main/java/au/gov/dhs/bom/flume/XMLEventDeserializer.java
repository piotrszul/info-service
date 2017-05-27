package au.gov.dhs.bom.flume;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.serialization.EventDeserializer;
import org.apache.flume.serialization.ResettableInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Throwables;

public class XMLEventDeserializer implements EventDeserializer {

	private final InputStream reader;
	private String xmlHeader = null;
	private Document doc = null;
	private NodeList eventElements = null;
	private int elementIndex = 0;

	public XMLEventDeserializer(InputStream reader) {
		super();
		this.reader = reader;
	}

	private void ensureParsed() throws IOException {
		if (doc == null) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setCoalescing(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(reader);

				Element docElement = doc.getDocumentElement();
				// assert its product
				Element headerElement = (Element) docElement.getElementsByTagName("amoc").item(0);
				Element bodyElement = (Element) docElement.getElementsByTagName("observations").item(0);
				NodeList eventElements = bodyElement.getElementsByTagName("station");

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(headerElement), new StreamResult(writer));
				String xmlHeader = writer.toString();
				this.xmlHeader = xmlHeader;
				this.doc = doc;
				this.elementIndex = 0;
				this.eventElements = eventElements;
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
		if (elementIndex < eventElements.getLength()) {
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				final Map<String, String> headers = new HashMap<String, String>();
				headers.put("xml-header", xmlHeader);
				headers.put("xml-event-index", String.valueOf(elementIndex));
				StringWriter bodyWriter = new StringWriter();
				transformer.transform(new DOMSource(eventElements.item(elementIndex++)), new StreamResult(bodyWriter));
				return EventBuilder.withBody(bodyWriter.toString().getBytes(), headers);
			} catch (Exception ex) {
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
		elementIndex = 0;
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
			return new XMLEventDeserializer(new InputStreamWrapper(in));
		}
		
	}
}
