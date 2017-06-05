package au.gov.dhs.bom.flume.decode;

import org.apache.flume.Context;
import org.apache.flume.serialization.EventDeserializer;
import org.w3c.dom.Document;

public interface DomEventDeserializer extends EventDeserializer {

	public interface DomBuilder {
		public DomEventDeserializer build(Context ctx, Document doc);
	}
}
