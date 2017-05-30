package au.gov.dhs.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message {

	public static class Header {
		private final String name;
		private final String value;
		public Header() {
			this(null,null);
		}
		public Header(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
		@Override
		public String toString() {
			return "Header [name=" + name + ", value=" + value + "]";
		}
		
	}
	
	private final String id;
	private final long timestamp;
	private final List<Header> headers = new ArrayList<Header>();
	private final byte[] payload;
	
	public Message(String id, long timedtamp, byte[] payload) {
		super();
		this.id = id;
		this.timestamp = timedtamp;
		this.payload = payload;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getId() {
		return id;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public byte[] getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", timestamp=" + timestamp + ", headers=" + headers + ", payload="
				+ Arrays.toString(payload) + "]";
	}
	
	
}
