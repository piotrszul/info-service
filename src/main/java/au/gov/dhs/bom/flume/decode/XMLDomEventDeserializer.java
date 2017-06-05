package au.gov.dhs.bom.flume.decode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDomEventDeserializer implements DomEventDeserializer {	

	
	private final static Logger logger  = LoggerFactory.getLogger(XMLDomEventDeserializer.class);
	
	private String xmlHeader = null;
	private Document doc = null;
	private NodeList eventElements = null;
	private int elementIndex = 0;
	private String xmlContext;
	private Transformer transformer;

	public XMLDomEventDeserializer(Document doc) {
		super();
		this.doc = doc;
	}

	private void ensureParsed() throws IOException {
		if (eventElements == null) {
			try{
				Element docElement = doc.getDocumentElement();
				// assert its product
				Element headerElement = (Element) docElement.getElementsByTagName("amoc").item(0);
				if (headerElement == null) {
					throw new RuntimeException("Cannot find headr node in document");
				}
				Node nextNode = headerElement.getNextSibling();
				while(nextNode !=  null && !(nextNode instanceof Element)) {
					nextNode = nextNode.getNextSibling();
				}
				if (nextNode == null) {
					throw new RuntimeException("Cannot find body node in document");
				}
				Element bodyElement = (Element) nextNode;
				NodeList eventElements = bodyElement.getChildNodes();
				transformer = TransformerFactory.newInstance().newTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(headerElement), new StreamResult(writer));
				String xmlHeader = writer.toString();
				this.xmlHeader = xmlHeader;
				this.elementIndex = 0;
				this.eventElements = eventElements;
				this.xmlContext = bodyElement.getTagName();
			} catch (TransformerException ex) {
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
		while (elementIndex < eventElements.getLength()) {
			Node elementNode = eventElements.item(elementIndex++);
			if (elementNode instanceof Element) {
				try {
					final Map<String, String> headers = new HashMap<String, String>();
					headers.put("xml-context", xmlContext);
					headers.put("xml-header", xmlHeader);
					headers.put("xml-event-index", String.valueOf(elementIndex));
					
					StringWriter bodyWriter = new StringWriter();
					transformer.transform(new DOMSource(elementNode), new StreamResult(bodyWriter));
					return EventBuilder.withBody(bodyWriter.toString().getBytes(), headers);
				} catch (Exception ex) {
					throw new IOException(ex);
				}
			}
		} 
		return null;
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
	
	public static class DomBuilder implements DomEventDeserializer.DomBuilder {

		@Override
		public DomEventDeserializer build(Context ctx, Document doc) {
			return new XMLDomEventDeserializer(doc);
		}
	}
	
}
